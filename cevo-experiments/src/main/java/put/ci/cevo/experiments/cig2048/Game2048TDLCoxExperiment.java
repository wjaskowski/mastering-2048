package put.ci.cevo.experiments.cig2048;

import static com.google.common.collect.ImmutableList.of;
import static put.ci.cevo.util.TableUtil.saveTableAsCSV;
import static put.ci.cevo.util.TextUtils.format;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.csvreader.CsvReader;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.experiments.ntuple.NTuplesGeneralSystematicFactory;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.Sequences;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class Game2048TDLCoxExperiment implements Runnable, Experiment {

	private static final Logger logger = Logger.getLogger(ConfiguredExperiment.class);
	private static final Configuration configuration = Configuration.getConfiguration();
	private final ThreadedContext context;
	private final int id;

	private final File outputDir;
	private final int numLearningGames;
	private final int numTestingGames;
	private final int testFrequency;
	private final boolean expectimax;
	private final double explorationDecrease;
	private final int explorationDecreaseFrequency;
	private final boolean serialize;
	private final boolean continueRun;
	private final IndividualFactory<NTuples> functionFactory;
	private final int threads;
	private final String nTuplesPattern;
	private final int saveEvery;

	public Game2048TDLCoxExperiment() {
		this.id = configuration.getInt(new ConfKey("experiment.id"), 1);
		this.threads = configuration.getInt(new ConfKey("threads"), 1);
		this.context = new ThreadedContext(configuration.getSeed(new ConfKey("experiment.seed"), 123), this.threads);
		this.outputDir = configuration.getFile(new ConfKey("experiment.output"), new File("./"));
		this.numLearningGames = configuration.getInt(new ConfKey("num_learning_games"), 10000000);
		this.numTestingGames = configuration.getInt(new ConfKey("num_testing_games"), 1000);
		this.testFrequency = configuration.getInt(new ConfKey("test_frequency"), 5000);
		this.expectimax = configuration.getBoolean(new ConfKey("expectimax"), false);
		this.explorationDecreaseFrequency = configuration.getInt(new ConfKey("exploration_decrease_frequency"), 100000);
		this.explorationDecrease = configuration.getDouble(new ConfKey("exploration_decrease"), 1.0);
		this.serialize = configuration.getBoolean(new ConfKey("serialize"), true);
		this.alpha = configuration.getDouble(new ConfKey("alpha"), 0.0025);
		this.continueRun = configuration.getBoolean(new ConfKey("continue_run"), false);
		this.nTuplesPattern = configuration.getString(new ConfKey("ntuples_pattern"), "11|11");
		this.saveEvery = configuration.getInt(new ConfKey("save_every"), 1000000);
		this.functionFactory = configuration.getObject(new ConfKey("function_factory"),
		// new NTuples2x3and4RandomIndividualFactory(
		// State2048.BOARD_SIZE, 16, 0, 0, new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE)));
		// new NTuplesAllRectanglesFactory(
		// new RectSize(2, 3), State2048.BOARD_SIZE, 16, 0, 0, new RotationMirrorSymmetryExpander(
		// State2048.BOARD_SIZE)));
			new NTuplesGeneralSystematicFactory(nTuplesPattern, new RectSize(4), 16, -1, +1, new RotationMirrorSymmetryExpander(
					new RectSize(4))));

	}

	private final SerializationManager serializer = SerializationManagerFactory.create();
	private final Double alpha;

	final class Stats {
		public SummaryStatistics score = new SummaryStatistics();
		public double ratio2048 = 0;
		public double ratio4096 = 0;
		public double ratio8192 = 0;
		public double ratio16384 = 0;
		public double ratio32768 = 0;
	}

	@Override
	public void run() {
		logger.info("run id = " + id);
		logger.info("host = " + System.getenv("HOSTNAME"));
		final List<Double> alphas = of(alpha);
		final List<Double> epses = of(0.0);// , 0.000, 0.01);
		final TableBuilder tableBuilder = new TableBuilder(
			"effort", "alpha", "eps", "perf", "max_perf", "ratio_2048", "ratio_4096", "ratio_8192", "ratio_16384",
			"ratio_32768", "time");
		File csvFile = new File(outputDir, "run-" + id + ".csv");
		for (double eps : epses) {
			for (final double alpha : alphas) {
				double currentEps = eps;
				final Game2048TDLearning game = new Game2048TDLearning();

				File bestFile = new File(outputDir, "run-" + id + "-" + alpha + "-" + ".bin");

				int learningGameStart = 0;
				double maxAvgPerf = 0.0;

				final NTuples vFunction;
				long learningTimeStart = 0;
				if (!continueRun) {
					vFunction = functionFactory.createRandomIndividual(context.getRandomForThread());
				} else {
					logger.info("Continuing run. Deserializing...");
					vFunction = serializer.deserializeWrapExceptions(bestFile);
					// Read csv to continue the run
					try {
						CsvReader csvReader = new CsvReader(new FileReader(csvFile));
						csvReader.readHeaders();
						while (csvReader.readRecord()) {
							String[] values = csvReader.getValues();
							ArrayList<String> row = new ArrayList<>(Arrays.asList(values));
							while (row.size() < tableBuilder.getColumnsCount())
								row.add("0");
							tableBuilder.addRow(row);
							learningGameStart = Integer.parseInt(csvReader.get("effort"));
							learningTimeStart = Long.parseLong(csvReader.get("time")) * 1000000;
							maxAvgPerf = Math.max(maxAvgPerf, Double.parseDouble(csvReader.get("perf")));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				logger.info("Staring from: " + learningTimeStart + " " + learningGameStart);
				long startTime = System.nanoTime() - learningTimeStart;

				final int NUM_GAMES_IN_BATCH = 100;
				for (int i = learningGameStart; i <= numLearningGames; i += NUM_GAMES_IN_BATCH) {
					final double finalEps = currentEps;

					// An optimistic parallel learning
					context.submit(new ThreadedContext.Worker<Integer, Void>() {
						@Override
						public Void process(Integer piece, ThreadedContext context) throws Exception {
							if (expectimax) {
								game.TDExpectimaxLearn(vFunction, finalEps, alpha, context.getRandomForThread());
							} else {
								game.TDAfterstateLearn(vFunction, finalEps, alpha, context.getRandomForThread());
							}
							return null;
						}

					}, Sequences.range(NUM_GAMES_IN_BATCH));

					if (i % explorationDecreaseFrequency == 0) {
						currentEps *= explorationDecrease;
					}

					if (i % testFrequency == 0) {
						logger.info("effort = " + i + ". Evaluating...");

						Stats stats = evalutePerformance(game, vFunction);
						logger.info("Evaluated");

						long elapsedTime = System.nanoTime() - startTime;
						tableBuilder.addRow(i, alpha, eps, format(stats.score.getMean(), 1),
							format(stats.score.getMax(), 1), format(stats.ratio2048), format(stats.ratio4096),
							format(stats.ratio8192), format(stats.ratio16384), format(stats.ratio32768),
							elapsedTime / 1000000);
						saveTableAsCSV(tableBuilder.build(), csvFile);

						if (i > 0 && i % saveEvery == 0) {
							if (maxAvgPerf < stats.score.getMean()) {
								logger.info("Found best of perf " + format(stats.score.getMean(), 1));
								maxAvgPerf = stats.score.getMean();
								logger.info(". Saving...");
								if (serialize) {
									serializer.serializeWrapExceptions(vFunction, bestFile);
								}
								logger.info("Saved");
							}
						}
					}
				}
			}
		}
		saveTableAsCSV(tableBuilder.build(), csvFile);
		logger.info("Saving results to: " + csvFile);
	}

	private Stats evalutePerformance(final Game2048TDLearning game, final RealFunction vFunction) {
		Stats stats = new Stats();

		List<Game2048Outcome> outcomes = context.invoke(new ThreadedContext.Worker<Integer, Game2048Outcome>() {
			@Override
			public Game2048Outcome process(Integer piece, ThreadedContext context) throws Exception {
					if (expectimax) {
						return game.playByExpectimax(vFunction, context.getRandomForThread());
					}
					return game.playByAfterstates(vFunction, context.getRandomForThread());
				}
			}, Sequences.range(numTestingGames)).toList();

		for (Game2048Outcome outcome : outcomes) {
			stats.score.addValue(outcome.score());
			stats.ratio2048 += (outcome.getLastState().getMaxTile() >= 2048) ? 1 : 0;
			stats.ratio4096 += (outcome.getLastState().getMaxTile() >= 4096) ? 1 : 0;
			stats.ratio8192 += (outcome.getLastState().getMaxTile() >= 8192) ? 1 : 0;
			stats.ratio16384 += (outcome.getLastState().getMaxTile() >= 16384) ? 1 : 0;
			stats.ratio32768 += (outcome.getLastState().getMaxTile() >= 32768) ? 1 : 0;
		}
		stats.ratio2048 /= numTestingGames;
		stats.ratio4096 /= numTestingGames;
		stats.ratio8192 /= numTestingGames;
		stats.ratio16384 /= numTestingGames;
		stats.ratio32768 /= numTestingGames;

		return stats;
	}

	public static void main(String[] args) {
		Game2048TDLCoxExperiment experiment = new Game2048TDLCoxExperiment();
		experiment.run();
	}

	@Override
	public void run(String[] args) {
		run();
	}
}
