package put.ci.cevo.experiments.new2048;

import static put.ci.cevo.util.TableUtil.TableBuilder;
import static put.ci.cevo.util.TableUtil.saveTableAsCSV;
import static put.ci.cevo.util.TextUtils.format;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.csvreader.CsvReader;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ntuple.BigNTuplesGeneralSystematicFactory;
import put.ci.cevo.experiments.ntuple.NTuplesFromLocationsFactory;
import put.ci.cevo.games.encodings.bigntuple.BigNTuples;
import put.ci.cevo.games.encodings.bigntuple.BigNTuplesStateValueFunction;
import put.ci.cevo.games.encodings.ntuple.*;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.game2048.*;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.newexperiments.ExperimentRunner;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.policies.GreedyAfterstatePolicy;
import put.ci.cevo.rl.agent.policies.MySoftMaxAfterstatePolicy;
import put.ci.cevo.rl.agent.policies.VFunctionControlPolicy;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.rl.evaluation.StateValueFunction;
import put.ci.cevo.rl.learn.*;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.StatisticUtils;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.Sequences;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class StagedGame2048TDLExperiment implements Experiment {

	private static final Logger logger = Logger.getLogger(StagedGame2048TDLExperiment.class);

	private final SerializationManager serializer = SerializationManagerFactory.create();
	private final Configuration config = Configuration.getConfiguration();

	// Technical params
	private final int ID = config.getInt(new ConfKey("id"), 1);
	private final int SEED = config.getInt(new ConfKey("seed"), 1);
	private final int NUM_THREADS = config.getInt(new ConfKey("threads"), 1);
	private final File RESULTS_DIR = ExperimentRunner.RESULTS_DIR;
	private final int NUM_GAMES_IN_BATCH = config.getInt(new ConfKey("batch_size"), 1000);
	private final int SAVE_BEST_FREQ = config.getInt(new ConfKey("save_best_freq"), 1000000); // -1 means save at the last episode

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);

	// Major params
	private final double LEARNING_RATE = config.getDouble(new ConfKey("learn_rate"), 1.0); // For some it means meta learning rate
	private final double AUTOSTEP_TAU = config.getDouble(new ConfKey("autostep_tau"), 0.0001);
	private final double INITIAL_LEARN_RATE = config.getDouble(new ConfKey("initial_learn_rate"), 1.0); // Autostep & IDBD
	private final double EPS = config.getDouble(new ConfKey("eps"), 0.0);
	private final double EXP_DEC = config.getDouble(new ConfKey("exploration_decrease"), 1.0);
	private final int EXP_DEC_FREQ = config.getInt(new ConfKey("exploration_decrease_freq"), 100000);

	private final int MAX_LEARNING_EPISODES = config.getInt(new ConfKey("episodes"), 10000000);
	private final long MAX_LEARNING_ACTIONS = config.getLong(new ConfKey("max_actions"), Long.MAX_VALUE);
	private final int NUM_TESTING_GAMES = config.getInt(new ConfKey("test_games"), 1000);
	private final long TEST_FREQ = config.getLong(new ConfKey("test_freq"), 20000L);

	private enum FreqUnit {EPISODE, ACTION}

	private final FreqUnit FREQ_UNIT = config.getEnumValue(FreqUnit.class, new ConfKey("freq_unit"), FreqUnit.EPISODE);

	private final double SOFTMAX_TEMP = config.getDouble(new ConfKey("softmax_temp"), Double.MAX_VALUE); // Means greedy

	private final double BACKTRACKING_RESTART_PROB = config.getDouble(new ConfKey("restart_prob"), 1.0);
	private final double BACKTRACKING_PERCENT = config.getDouble(new ConfKey("backtracking_perc"), 0.0);

	private final String TUPLES_PATTERN = config.getString(new ConfKey("ntuples_pattern"), "11|11");

	private final int CONTINUE_FROM_EPISODE = config.getInt(new ConfKey("continue_from_episode"), 0); // restart by default

	private final int EXPECTIMAX_DEPTH = config.getInt(new ConfKey("expectimax_depth"), 3);
	private final int EXPECTIMAX_TEST_FREQ = config.getInt(new ConfKey("expectimax_freq"), 200000);
	private final int EXPECTIMAX_NUM_TESTING_GAMES = config.getInt(new ConfKey("expectimax_games"), 100);

	private final int NUM_VALUES = config.getInt(new ConfKey("num_board_values"), 16);
	private int SHAPING_EPISODES_PER_STAGE = config.getInt(new ConfKey("shaping_episodes_per_stage"), 10000);

	private enum FunctionKind {
		NORMAL, BIG, TILES, TILES_COMBINED, HASH_TILES
	}

	private final FunctionKind FUNCTION_KIND = config.getEnumValue(FunctionKind.class, new ConfKey("ntuples_kind"),
			FunctionKind.NORMAL);

	private final int TILES_SEGMENTS = config.getInt(new ConfKey("tiles_num_segments"), 4);
	private final int CAROUSEL_TILES_SEGMENTS = config.getInt(new ConfKey("carousel_num_segments"), TILES_SEGMENTS);
	private final int TILES_MAX_SEGMENT = config.getInt(new ConfKey("tiles_max_segment"), 15);
	private final boolean TILES_SPECIAL = config.getBoolean(new ConfKey("tiles_special"), true);

	private final long HASH_TILES_TOTAL_CAPACITY = config.getLong(new ConfKey("hash_tiles_capacity"), 100000000l);

	private final double LAMBDA = config.getDouble(new ConfKey("lambda"), 0.0);
	private final double LAMBDA_LIMIT_PROB = config.getDouble(new ConfKey("lambda_limit_prob"), 0.1);

	private final double BENCHMARK_SECONDS = config.getInt(new ConfKey("benchmark_seconds"), 60);
	private final boolean BENCHMARK = config.getBoolean(new ConfKey("benchmark"), false);

	private final boolean SYSTEMATIC = config.getBoolean(new ConfKey("systematic"), true);
	private final boolean REMOVE_SUBTUPLES = config.getBoolean(new ConfKey("remove_subtuples"), true);

	enum LearningAlgorithm {
		TD_0, TD_LAMBDA, TD_0_IDBD, TD_0_TCL, TD_LAMBDA_DELAYED, TD_0_AUTOSTEP, TD_LAMBDA_DELAYED_TCL,
		TD_LAMBDA_DELAYED_AUTOSTEP, TD_BACKTRACKING, TD_LAMBDA_DELAYED_TCL_STAGED, TD_SHAPING
	}

	private final LearningAlgorithm LEARNING_ALGORITHM = config.getEnumValue(LearningAlgorithm.class, new ConfKey(
			"learning_alg"), LearningAlgorithm.TD_0);

	// Interaction environment
	private final Game2048 game2048 = new Game2048();

	private final TableBuilder tableBuilder = new TableBuilder(
			"id", "alpha", "eps", "episodes", "actions", "perf", "conf",
			"64",
			"32_16_8_4", "32_16_8", "32_16_4", "32_16",
			"32_8_4", "32_8", "32_4", "32",
			"16_8_4", "16_8", "16_4", "16",
			"8_4", "8", "4", "2",
			"time", "expectimax-perf");

	private final File SAVED_FILE = new File(RESULTS_DIR, "run-" + ID + ".bin");
	private final File CSV_FILE = new File(RESULTS_DIR, "run-" + ID + ".csv");

	private final VFunctionControlPolicy<State2048, Action2048> expectimax = new Game2048ExpectimaxPolicy(game2048,
			EXPECTIMAX_DEPTH);
	private final Game2048PerformanceMeasure expectimaxMeasure = new Game2048PerformanceMeasure(
			EXPECTIMAX_NUM_TESTING_GAMES);

	private final Game2048PerformanceMeasure oneplyMeasure = new Game2048PerformanceMeasure(NUM_TESTING_GAMES);

	interface VFunctionSupplier {
		LearnableStateValueFunction<State2048> get(double initialValue);

		default LearnableStateValueFunction<State2048> get() {
			return get(0.0);
		}
	}

	@Override
	public void run(String[] args) {
		/*Preconditions.checkArgument(TEST_FREQ % NUM_GAMES_IN_BATCH == 0);
		Preconditions.checkArgument(EXP_DEC_FREQ % NUM_GAMES_IN_BATCH == 0);
		Preconditions.checkArgument(SAVE_BEST_FREQ == -1 || SAVE_BEST_FREQ % NUM_GAMES_IN_BATCH == 0);
		Preconditions.checkArgument(SAVE_BEST_FREQ == -1 || SAVE_BEST_FREQ % TEST_FREQ == 0);
		Preconditions.checkArgument(EXPECTIMAX_TEST_FREQ % TEST_FREQ == 0);*/

		double bestExpectiMaxPerformance = 0;
		long elapsedLearningTime = 0;
		long numActionsPerformed = 0;
		long previousActionsPerformed;

		if (CONTINUE_FROM_EPISODE > 0) {
			logger.info("Continuing run. Deserializing from: " + SAVED_FILE);
		}

		VFunctionSupplier vFunctionSupplier = getAppropriateVFunctionSupplier();

		if (CONTINUE_FROM_EPISODE > 0) {
			// Read csv to continue the run
			try {
				CsvReader csvReader = new CsvReader(new FileReader(CSV_FILE));
				csvReader.readHeaders();
				while (csvReader.readRecord()) {
					String[] values = csvReader.getValues();
					ArrayList<String> row = new ArrayList<>(Arrays.asList(values));
					while (row.size() < tableBuilder.getColumnsCount())
						row.add("");
					//TODO: Make it possible to change the order of columns
					tableBuilder.addRow(row);

					if (Integer.parseInt(csvReader.get("episodes")) == CONTINUE_FROM_EPISODE) {
						elapsedLearningTime = Long.parseLong(csvReader.get("time")) * 1000000;
						bestExpectiMaxPerformance = Double.parseDouble(csvReader.get("perf"));
						numActionsPerformed = Long.parseLong(csvReader.get("actions"));
						break;
					}
				}
				saveTableAsCSV(tableBuilder.build(), CSV_FILE);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		logger.info("Starting from episode: " + CONTINUE_FROM_EPISODE + "; time: " + elapsedLearningTime + "; perf: "
				+ bestExpectiMaxPerformance);

		final LearnableStateValueFunction<State2048> vFunction = createAppropriateLearnableStateValueFunction(
				vFunctionSupplier);

		final VFunctionLearningAlgorithm<State2048, Action2048> algorithm = getAppropriateLearningAlgorithm(
				vFunctionSupplier, vFunction);

		Agent<State2048, Action2048> learningAgent = new AfterstateFunctionAgent<>(
				vFunction, SOFTMAX_TEMP < Double.MAX_VALUE ? new MySoftMaxAfterstatePolicy<>(game2048, SOFTMAX_TEMP) :
				new GreedyAfterstatePolicy<>(game2048));

		// Measure initial performance
		if (!BENCHMARK && CONTINUE_FROM_EPISODE == 0) {
			measureAndSavePerformance(0, elapsedLearningTime, numActionsPerformed, vFunction, true);
		}

		// An optimistic parallel learning
		int episode;
		int previousEpisode;
		Stopwatch everyEvaluationStopwatch = Stopwatch.createStarted();
		@SuppressWarnings("unchecked")
		List<CircularFifoQueue<State2048>> initialStates = new ArrayList<>(1<<CAROUSEL_TILES_SEGMENTS);
		for (int i = 0; i < (1<<CAROUSEL_TILES_SEGMENTS); ++i) {
			initialStates.add(new CircularFifoQueue<>(1000));
		}
		final MutableInt currentStage = new MutableInt(0);
		for (episode = CONTINUE_FROM_EPISODE; episode < MAX_LEARNING_EPISODES && numActionsPerformed < MAX_LEARNING_ACTIONS; ) {
			long startTime = System.nanoTime();
			List<Pair<Long, State2048[]>> results = context.invoke((x, rand) -> {
				State2048[] achievedStates = new State2048[1<<CAROUSEL_TILES_SEGMENTS];
				State2048 initialState;
				if (currentStage.getValue() == 0) {
					initialState = State2048.getInitialState(rand.getRandomForThread());
				} else {
					initialState = RandomUtils.pickRandom(initialStates.get(currentStage.getValue()).toArray(
									new State2048[initialStates.get(currentStage.getValue()).size()]),
							rand.getRandomForThread());
				}
				long numActions = ((StagedDelayedTDLambdaAfterstateValueLearningTCL) algorithm).learnFromEpisode(
						game2048, vFunction, learningAgent, rand.getRandomForThread(), initialState, achievedStates);
				return Pair.create(numActions, achievedStates);
			}, Sequences.range(NUM_GAMES_IN_BATCH)).toList();

			for (State2048[] achieved : results.stream().map(Pair::second).collect(Collectors.toList())) {
				for (int i = currentStage.getValue() + 1; i < 1<<CAROUSEL_TILES_SEGMENTS; ++i) {
					if (achieved[i] == null)
						break;
					initialStates.get(i).add(achieved[i]);
				}
			}

			currentStage.increment();
			if (currentStage.getValue() == (1<<CAROUSEL_TILES_SEGMENTS) || initialStates.get(currentStage.getValue()).size() == 0) {
				currentStage.setValue(0);
			}
			String s = "";
			for (int i = 0; i < (1<<CAROUSEL_TILES_SEGMENTS); ++i) {
				s += initialStates.get(i).size() + " ";
			}
			logger.info(s);

			elapsedLearningTime += System.nanoTime() - startTime;

			previousActionsPerformed = numActionsPerformed;
			previousEpisode = episode;

			numActionsPerformed += results.stream().map(Pair::first).mapToLong(x -> x).sum();
			episode += NUM_GAMES_IN_BATCH;

			if (BENCHMARK && elapsedLearningTime / 1.e9 > BENCHMARK_SECONDS) {
				break;
			}
			if (BENCHMARK) {
				continue;
			}

			boolean specialLastEpisode = SAVE_BEST_FREQ == -1 && (episode >= MAX_LEARNING_EPISODES
					|| numActionsPerformed >= MAX_LEARNING_ACTIONS);

			boolean timeToTest = FREQ_UNIT == FreqUnit.EPISODE ?
					previousEpisode / TEST_FREQ < episode / TEST_FREQ :
					previousActionsPerformed / TEST_FREQ < numActionsPerformed / TEST_FREQ;

			if (timeToTest || specialLastEpisode) {
				logger.info(String.format("Elapsed since last evaluation: %4ds", everyEvaluationStopwatch.elapsed(
						TimeUnit.SECONDS)));

				boolean timeToTestExpectimax = FREQ_UNIT == FreqUnit.EPISODE ?
						previousEpisode / EXPECTIMAX_TEST_FREQ < episode / EXPECTIMAX_TEST_FREQ :
						previousActionsPerformed / EXPECTIMAX_TEST_FREQ < numActionsPerformed / EXPECTIMAX_TEST_FREQ;

				long roundedActions =
						FREQ_UNIT == FreqUnit.EPISODE ?
								numActionsPerformed :
								(numActionsPerformed / TEST_FREQ) * TEST_FREQ;

				double meanExpectimaxPerformance = measureAndSavePerformance(episode, elapsedLearningTime,
						roundedActions, vFunction, timeToTestExpectimax);

				if (SAVE_BEST_FREQ != 0) {
					boolean timeToSaveBest = FREQ_UNIT == FreqUnit.EPISODE ?
							previousEpisode / SAVE_BEST_FREQ < episode / SAVE_BEST_FREQ :
							previousActionsPerformed / SAVE_BEST_FREQ < numActionsPerformed / SAVE_BEST_FREQ;

					if ((timeToSaveBest && bestExpectiMaxPerformance < meanExpectimaxPerformance)
							|| specialLastEpisode) {
						Stopwatch sw = Stopwatch.createStarted();
						bestExpectiMaxPerformance = meanExpectimaxPerformance;
						logger.info("Serializing best...");
						switch (FUNCTION_KIND) {
						case NORMAL:
							serializer.serializeWrapExceptions(
									((NTuplesStateValueFunction<State2048>) vFunction).getNtuples(), SAVED_FILE);
							break;
						case BIG:
							serializer.serializeWrapExceptions(
									((BigNTuplesStateValueFunction<State2048>) vFunction).getNtuples(), SAVED_FILE);
							break;
						case TILES:
							serializer.serializeWrapExceptions(
									((TilingsSet2048VFunction) vFunction).getTiles(), SAVED_FILE);
							break;
						default:
							logger.warn("serializing not implemented");
						}
						logger.info(String.format("... serialized in %ds", sw.elapsed(TimeUnit.SECONDS)));
					}
				}

				if (FUNCTION_KIND.equals(FunctionKind.HASH_TILES)) {
					logger.info("Hash Tilings total weights: " +
							((HashTilingsSet2048VFunction) vFunction).getTiles().getTotalWeights());
				}
				everyEvaluationStopwatch.reset();
				everyEvaluationStopwatch.start();
			}
		}

		if (BENCHMARK) {
			double perf = measureAndSavePerformance(0, elapsedLearningTime, numActionsPerformed, vFunction, false);
			logger.info((int) (elapsedLearningTime / 1.e9) + ", " + numActionsPerformed + ", "
					+ numActionsPerformed / BENCHMARK_SECONDS + ", " + episode + ", " + perf);
		}
	}

	public VFunctionLearningAlgorithm<State2048, Action2048> getAppropriateLearningAlgorithm(
			VFunctionSupplier vFunctionSupplier, LearnableStateValueFunction<State2048> vFunction) {
		switch (LEARNING_ALGORITHM) {
		case TD_0:
			return new TDAfterstateValueLearningV2<>(LEARNING_RATE, EPS);

		case TD_0_IDBD:
			return new TDAfterstateValueLearningIDBD<>(LEARNING_RATE, EPS,
					vFunctionSupplier.get(Math.log(INITIAL_LEARN_RATE)), vFunctionSupplier.get());

		case TD_0_TCL:
			return new TDAfterstateValueLearningTCL<>(LEARNING_RATE, EPS, vFunctionSupplier.get(),
					vFunctionSupplier.get());

		case TD_LAMBDA_DELAYED_AUTOSTEP: {
			double initialValue = INITIAL_LEARN_RATE / vFunction.getActiveFeaturesCount();
			return new DelayedTDLambdaAfterstateValueLearningAutoStep<>(LEARNING_RATE, AUTOSTEP_TAU, EPS,
					LAMBDA, LAMBDA_LIMIT_PROB,
					vFunctionSupplier.get(initialValue), vFunctionSupplier.get(0), vFunctionSupplier.get(0));
		}
		case TD_0_AUTOSTEP: {
			double initialValue = INITIAL_LEARN_RATE / vFunction.getActiveFeaturesCount();
			return new TDAfterstateValueLearningAutoStep<>(LEARNING_RATE, AUTOSTEP_TAU, EPS,
					vFunctionSupplier.get(initialValue), vFunctionSupplier.get(), vFunctionSupplier.get());
		}
		case TD_LAMBDA_DELAYED_TCL:
			return new DelayedTDLambdaAfterstateValueLearningTCL<>(LEARNING_RATE, EPS, LAMBDA, LAMBDA_LIMIT_PROB,
					vFunctionSupplier.get(), vFunctionSupplier.get());

		case TD_LAMBDA_DELAYED_TCL_STAGED:
			return new StagedDelayedTDLambdaAfterstateValueLearningTCL(LEARNING_RATE, EPS, LAMBDA, LAMBDA_LIMIT_PROB,
					TILES_MAX_SEGMENT, CAROUSEL_TILES_SEGMENTS, vFunctionSupplier.get(), vFunctionSupplier.get());

		case TD_SHAPING:
			return new DelayedTDLambdaAfterstateValueLearningTCLShaping(
					new DelayedTDLambdaAfterstateValueLearningTCL<>(LEARNING_RATE, EPS, LAMBDA, LAMBDA_LIMIT_PROB,
							vFunctionSupplier.get(), vFunctionSupplier.get()), SHAPING_EPISODES_PER_STAGE);

		case TD_LAMBDA:
			return new ClassicalTDLambdaAfterstateValueLearning<>(LEARNING_RATE, EPS, LAMBDA, LAMBDA_LIMIT_PROB);

		case TD_LAMBDA_DELAYED:
			return new DelayedTDLambdaAfterstateValueLearning<>(LEARNING_RATE, EPS, LAMBDA, LAMBDA_LIMIT_PROB);

		case TD_BACKTRACKING:
			//TODO: Make updateAlgorithm configurable
			TCLVFunctionUpdateAlgorithm<State2048> updateAlgorithm = new TCLVFunctionUpdateAlgorithm<>(
					LEARNING_RATE, vFunctionSupplier.get(), vFunctionSupplier.get());
			return new TDAfterstateValueBacktrackingLearning<>(updateAlgorithm, EPS, BACKTRACKING_PERCENT,
					BACKTRACKING_RESTART_PROB);

		default:
			throw new IllegalStateException();
		}
	}

	public VFunctionSupplier getAppropriateVFunctionSupplier() {
		final List<List<int[]>> locations;
		if (!FUNCTION_KIND.equals(FunctionKind.TILES_COMBINED)) {
			 locations = (SYSTEMATIC ?
					new NTuplesLocationsGeneralSystematicSupplier(TUPLES_PATTERN, State2048.BOARD_SIZE,
							new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE), REMOVE_SUBTUPLES) :
					new NTuplesLocationsGeneralSupplier(TUPLES_PATTERN, State2048.BOARD_SIZE,
							new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE))
			).get();
		} else {
			locations = null;
		}

		switch (FUNCTION_KIND) {
		case NORMAL:
			return (initialValue) -> new NTuplesStateValueFunction<>(new NTuplesFromLocationsFactory(
					locations, NUM_VALUES, initialValue, initialValue).createRandomIndividual(
					context.getRandomForThread()));

		case BIG:
			return (initialValue) ->
					new BigNTuplesStateValueFunction<>(
							new BigNTuplesGeneralSystematicFactory(
									TUPLES_PATTERN, State2048.BOARD_SIZE, NUM_VALUES, initialValue, initialValue,
									new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE), SYSTEMATIC)
									.createRandomIndividual(context.getRandomForThread()));

		case TILES:
			return (initialValue) -> new TilingsSet2048VFunction(
					TilingsSet2048.createWithRandomWeights(
							TILES_SEGMENTS, TILES_MAX_SEGMENT, locations, NUM_VALUES, initialValue, initialValue,
							context.getRandomForThread()), TILES_SPECIAL);

		case HASH_TILES:
			return (initialValue) -> {
				Preconditions.checkArgument(initialValue == 0);
				return new HashTilingsSet2048VFunction(
						HashTilingsSet2048.create(
								TILES_SEGMENTS, TILES_MAX_SEGMENT, locations, NUM_VALUES,
								HASH_TILES_TOTAL_CAPACITY),
						TILES_SPECIAL);
			};
		case TILES_COMBINED: {
			// Format: 4_11|11;111|111+0_11|11;111|111+0_111+0_1111_r
			return (initialValue) -> {
				List<LearnableStateValueFunction<State2048>> list = new ArrayList<>();
				for (String subpattern : TUPLES_PATTERN.split("\\+")) {
					String[] splitted = subpattern.split("_");
					int segments = Integer.parseInt(splitted[0]);
					String pattern = splitted[1];
					boolean relative = (splitted.length > 2 && splitted[2].equals("r"));

					final List<List<int[]>> mylocations = (SYSTEMATIC ?
							new NTuplesLocationsGeneralSystematicSupplier(pattern, State2048.BOARD_SIZE,
									new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE), REMOVE_SUBTUPLES) :
							new NTuplesLocationsGeneralSupplier(pattern, State2048.BOARD_SIZE,
									new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE))
					).get();

					if (!relative) {
						list.add(new TilingsSet2048VFunction(TilingsSet2048.createWithRandomWeights(segments,
								TILES_MAX_SEGMENT, mylocations, NUM_VALUES, initialValue, initialValue,
								context.getRandomForThread()),
								TILES_SPECIAL));
					} else {
						list.add(new RelativeNTuplesStateValueFunction<>(RelativeNTuples.createWithRandomWeights(
								mylocations, NUM_VALUES, initialValue, initialValue, context.getRandomForThread())));

					}
				}
				return new CombinedVFunction<>(list);
			};
		}
		}
		throw new IllegalStateException();
	}

	public double measureAndSavePerformance(int episode, long elapsedTime, long numActionsPerformed,
			StateValueFunction<State2048> vFunction, boolean timeToTestExpectimax) {
		Stopwatch sw = Stopwatch.createUnstarted();

		logger.info("Effort = " + episode + " " + numActionsPerformed + "; Evaluating performance...");
		sw.reset();
		sw.start();
		Game2048Measurement measurement = (Game2048Measurement) oneplyMeasure.measure(
				new AfterstateFunctionAgent<>(vFunction, new GreedyAfterstatePolicy<>(game2048)), context);
		logger.info(String.format("... evaluated in %ds\n%s", sw.elapsed(TimeUnit.SECONDS), measurement));

		double meanPerformance = measurement.stats().getMean();

		double meanExpectimaxPerformance = 0.0;
		Game2048Measurement expectimaxMeasurement = null;
		if (timeToTestExpectimax) {
			logger.info(String.format("Evaluating expectimax (d=%d)...", EXPECTIMAX_DEPTH));
			sw.reset();
			sw.start();
			expectimaxMeasurement = (Game2048Measurement) expectimaxMeasure.measure(
					new AfterstateFunctionAgent<>(vFunction, expectimax), context);
			logger.info(String.format("... evaluated in %ds\n%s", sw.elapsed(TimeUnit.SECONDS), expectimaxMeasurement));
			meanExpectimaxPerformance = expectimaxMeasurement.stats().getMean();
		}

		tableBuilder.addRow(ID, LEARNING_RATE, EPS,
				episode,
				numActionsPerformed,
				format(meanPerformance, 0),
				format(StatisticUtils.getConfidenceIntervalDelta(measurement.stats(), 0.05), 0),
				format(measurement.getPercentage(65536), 2),
				format(measurement.getPercentage(32768, 16384, 8192, 4096), 2),
				format(measurement.getPercentage(32768, 16384, 8192), 2),
				format(measurement.getPercentage(32768, 16384, 4096), 2),
				format(measurement.getPercentage(32768, 16384), 2),
				format(measurement.getPercentage(32768, 8192, 4096), 2),
				format(measurement.getPercentage(32768, 8192), 2),
				format(measurement.getPercentage(32768, 4096), 2),
				format(measurement.getPercentage(32768), 2),
				format(measurement.getPercentage(16384, 8192, 4096), 2),
				format(measurement.getPercentage(16384, 8192), 2),
				format(measurement.getPercentage(16384, 4096), 2),
				format(measurement.getPercentage(16384), 2),
				format(measurement.getPercentage(8192, 4096), 2),
				format(measurement.getPercentage(8192), 2),
				format(measurement.getPercentage(4096), 2),
				format(measurement.getPercentage(2048), 2),
				elapsedTime / 1000000,
				expectimaxMeasurement == null ? "" : format(expectimaxMeasurement.stats().getMean(), 0)
		);
		saveTableAsCSV(tableBuilder.build(), CSV_FILE);
		return meanExpectimaxPerformance;
	}

	private LearnableStateValueFunction<State2048> createAppropriateLearnableStateValueFunction(
			VFunctionSupplier vFunctionSupplier) {
		//TODO: Make them descriptable
		switch (FUNCTION_KIND) {
		case NORMAL: {
			NTuplesStateValueFunction<State2048> f;
			if (CONTINUE_FROM_EPISODE > 0)
				f = new NTuplesStateValueFunction<>(serializer.deserializeWrapExceptions(SAVED_FILE));
			else {
				f = (NTuplesStateValueFunction<State2048>) vFunctionSupplier.get();
			}
			logger.info(
					"Ntuples. main: " + f.getNtuples().getMain().size() + ", all: " + f.getNtuples().getAll().size()
							+ ", total weights: " + f.getNtuples().totalWeights());
			return f;
		}
		case BIG: {
			BigNTuplesStateValueFunction<State2048> f;
			if (CONTINUE_FROM_EPISODE > 0)
				f = new BigNTuplesStateValueFunction<>(serializer.deserializeWrapExceptions(SAVED_FILE));
			else {
				f = (BigNTuplesStateValueFunction<State2048>) vFunctionSupplier.get();
			}
			BigNTuples bigNTuples = f.getNtuples();
			logger.info(
					"BigNtuples. main: " + bigNTuples.getMain().size() + ", all: " + bigNTuples.getAll().size()
							+ ", total weights: " + bigNTuples.totalWeights());
			return f;
		}
		case TILES: {
			TilingsSet2048VFunction f;
			if (CONTINUE_FROM_EPISODE > 0)
				f = new TilingsSet2048VFunction(serializer.deserializeWrapExceptions(SAVED_FILE), TILES_SPECIAL);
			else {
				f = (TilingsSet2048VFunction) vFunctionSupplier.get();
			}
			TilingsSet2048 tilings = f.getTiles();
			logger.info(
					"Tilings. main: " + tilings.getNumMainTilings() + ", all: " + tilings.getNumAllTilings()
							+ ", total weights: " + tilings.getTotalWeights());
			return f;
		}
		case HASH_TILES: {
			HashTilingsSet2048VFunction f;
			if (CONTINUE_FROM_EPISODE > 0)
				f = new HashTilingsSet2048VFunction(serializer.deserializeWrapExceptions(SAVED_FILE), TILES_SPECIAL);
			else {
				f = (HashTilingsSet2048VFunction) vFunctionSupplier.get();
			}
			logger.info("Tilings with hash maps");
			return f;
		}
		case TILES_COMBINED: {
			if (CONTINUE_FROM_EPISODE > 0)
				throw new NotImplementedException();
			LearnableStateValueFunction<State2048> vFunction = vFunctionSupplier.get();
			logger.info(vFunction.toString());
			return vFunction;
		}
		}
		throw new IllegalStateException();
	}

	public static void main(String[] args) throws IllegalAccessException, InstantiationException {
		ExperimentRunner.main(args);
	}
}
