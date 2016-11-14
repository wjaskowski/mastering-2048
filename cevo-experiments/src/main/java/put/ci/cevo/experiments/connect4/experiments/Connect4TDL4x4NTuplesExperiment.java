package put.ci.cevo.experiments.connect4.experiments;

import static com.google.common.collect.ImmutableList.of;
import static put.ci.cevo.util.TableUtil.TableBuilder;
import static put.ci.cevo.util.TableUtil.saveTableAsCSV;
import static put.ci.cevo.util.TextUtils.format;

import java.io.File;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.connect4.Connect4Interaction;
import put.ci.cevo.experiments.connect4.mappers.NTuplesConnect4PlayerMapper;
import put.ci.cevo.experiments.connect4.measures.Connect4PerfectPlayerPerformanceMeasure;
import put.ci.cevo.experiments.connect4.measures.Connect4RandomPlayerPerformanceMeasure;
import put.ci.cevo.experiments.connect4.ntuples.Connect4NTuplesSystematicFactory;
import put.ci.cevo.experiments.rl.AgentExpectedUtility;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.state.MeasuredIndividual;
import put.ci.cevo.games.OutputNegationActionValueFunction;
import put.ci.cevo.games.connect4.Connect4Action;
import put.ci.cevo.games.connect4.Connect4SelfPlayEnvironment;
import put.ci.cevo.games.connect4.Connect4State;
import put.ci.cevo.games.connect4.players.Connect4DeltaNTuplesAgent;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuplesStateValueFunction;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.learn.TDAfterstateValueLearning;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class Connect4TDL4x4NTuplesExperiment implements Experiment {

	private static final Logger logger = Logger.getLogger(Connect4TDL4x4NTuplesExperiment.class);
	private static final SerializationManager serializer = SerializationManagerFactory.create();
	private static final Configuration config = Configuration.getConfiguration();

	// Technical params
	private static final int SEED = config.getInt(new ConfKey("seed"), 1);
	private static final int NUM_THREADS = config.getInt(new ConfKey("threads"), 1);
	private static final File RESULTS = config.getFile(new ConfKey("results_dir"), new File("."));

	private final ThreadedContext context = new ThreadedContext(SEED, NUM_THREADS);

	// Major params
	private static final double ALPHA = config.getDouble(new ConfKey("learning.rate"), 0.001);
	private static final double EPS = config.getDouble(new ConfKey("eps"), 0.1);

	private static final int NUM_TRAINING_EPISODES = config.getInt(new ConfKey("episodes"), 1000000);
	private static final int NUM_TESTING_GAMES = config.getInt(new ConfKey("test.games"), 1000);
	private static final int TEST_FREQUENCY = config.getInt(new ConfKey("test.freq"), 5000);
	private static final int TUPLE_SIZE = config.getInt(new ConfKey("tuple.size"), 4);

	private static final double EXP_DEC = config.getDouble(new ConfKey("exp.dec"), 0.9);
	private static final int EXP_DEC_FREQ = config.getInt(new ConfKey("exp.dec.freq"), 100000);

	private static final String TUPLES = config.getString(new ConfKey("TUPLES"), "1111|1111|1111|1111");

	private final int id = 1;


	// Interaction environment
	private final Connect4SelfPlayEnvironment c4 = new Connect4SelfPlayEnvironment();

	// Performance measures
	private static final boolean OBJECTIVE_EVAL = config.getBoolean(new ConfKey("eval.objective"), false);

	private final PerformanceMeasure<Connect4Player> objectiveMeasure =
			new Connect4PerfectPlayerPerformanceMeasure(new Connect4Interaction(), 200);
	private final PerformanceMeasure<Connect4Player> randomPlayerMeasure =
			new Connect4RandomPlayerPerformanceMeasure(new Connect4Interaction(), 10000);

	private final NTuplesConnect4PlayerMapper mapper = new NTuplesConnect4PlayerMapper();

	public void run() {
		RandomDataGenerator rdg = context.getRandomForThread();
		TableBuilder tableBuilder = new TableBuilder("effort", "alpha", "eps", "perf", "time");
		final List<Double> alphas = of(ALPHA);
		final List<Double> epses = of(EPS);

		double bestPerformance = 0;

		for (double eps : epses) {
			for (double alpha : alphas) {
				double currentEps = eps;

				Connect4NTuplesSystematicFactory ntuplesFactory = new Connect4NTuplesSystematicFactory(0, 0, TUPLES);
				NTuples nTuples = ntuplesFactory.createRandomIndividual(rdg);

				NTuplesStateValueFunction<Connect4State> valueFunction = new NTuplesStateValueFunction<>(nTuples);
				Connect4DeltaNTuplesAgent deltaAgent = new Connect4DeltaNTuplesAgent(nTuples,
						OutputNegationActionValueFunction::new);

				AgentExpectedUtility<Connect4State, Connect4Action> performanceMeasure =
						new AgentExpectedUtility<>(c4, NUM_TESTING_GAMES);

				TDAfterstateValueLearning<Connect4State, Connect4Action> algorithm =
						new TDAfterstateValueLearning<>(c4, valueFunction, deltaAgent);

				long startTime = System.nanoTime();
				for (int i = 0; i <= NUM_TRAINING_EPISODES; i++) {
					algorithm.fastLearningEpisode(currentEps, alpha, rdg);

					if (i % EXP_DEC_FREQ == 0) {
						currentEps *= EXP_DEC;
					}

					if (i % TEST_FREQUENCY == 0) {
						Measurement performance = performanceMeasure.measure(deltaAgent, context);

						long elapsedTime = System.nanoTime() - startTime;
						double meanPerformance = performance.stats().getMean();
						tableBuilder.addRow(i, alpha, eps, format(meanPerformance), (elapsedTime / 1000000));
						saveTableAsCSV(tableBuilder.build(), new File(RESULTS, "run-" + id + ".csv"));

						if (meanPerformance > bestPerformance) {
							bestPerformance = meanPerformance;
							serializer.serializeWrapExceptions(nTuples, new File(RESULTS, "run-" + id + "-" + alpha
									+ ".csv"));
						}
						objectiveEval(nTuples, i);
					}
				}
			}
		}
		saveTableAsCSV(tableBuilder.build(), new File(RESULTS, "run-" + id + ".csv"));
		logger.info("Saving results to: " + new File(RESULTS, "run-" + id + ".csv"));
	}

	private MeasuredIndividual<NTuples> bestOfRun = MeasuredIndividual.createNull();
	private final TableBuilder tb = new TableBuilder("effort", "perf");

	public void objectiveEval(NTuples individual, int episode) {
		logger.info(String.format("Episode %04d", episode));

		/*Connect4Player player = mapper.getPhenotype(individual, context.getRandomForThread());

		double performance = OBJECTIVE_EVAL ? objectiveMeasure.measure(player, context.singleThreaded()).stats().getMean() :
				randomPlayerMeasure.measure(player, context).stats().getMean();

		MeasuredIndividual<NTuples> bestOfGeneration = new MeasuredIndividual<>(individual, performance);

		if (bestOfGeneration.isBetterThan(bestOfRun)) {
			bestOfRun = bestOfGeneration;
			serializer.serializeWrapExceptions(bestOfRun.getIndividual(), new File(RESULTS, "best.dump"));
			logger.info(String.format("Found better. Perf = %5.3f", bestOfRun.getPerformance()));
		}

		tb.addRow(episode, bestOfGeneration.getPerformance());
		saveTableAsCSV(tb.build(), new File(RESULTS, "results-objective.csv"));*/
	}

	public static void main(String[] args) {
		new Connect4TDL4x4NTuplesExperiment().run(args);
	}

	@Override
	public void run(String[] args) {
		Connect4TDL4x4NTuplesExperiment experiment = new Connect4TDL4x4NTuplesExperiment();
		experiment.run();
	}

}
