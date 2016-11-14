package put.ci.cevo.experiments.gecco2015tetris;

import static com.google.common.collect.ImmutableList.of;
import static put.ci.cevo.util.TableUtil.saveTableAsCSV;
import static put.ci.cevo.util.TextUtils.format;

import java.io.File;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.experiments.tetris.TetrisExpectedUtility;
import put.ci.cevo.experiments.tetris.TetrisNTuplesSystematicFactory;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuplesStateValueFunction;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.games.tetris.agents.DeltaNTuplesTetrisAgent;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.learn.TDAfterstateValueLearning;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class TDLTetrisExperiment implements Runnable, Experiment {

	private enum Config implements ConfigurationKey {
		EXPERIMENT_ID("experiment.id"),
		EXPERIMENT_SEED("experiment.seed"),
		EXPERIMENT_THREADS("experiment.threads"),
		EXPERIMENT_OUTPUT("experiment.output");

		private final String key;

		Config(String key) {
			this.key = key;
		}

		@Override
		public ConfigurationKey dot(Object subKey) {
			return ConfKey.dot(this, subKey);
		}

		@Override
		public String toString() {
			return key;
		}
	}

	private static final Logger logger = Logger.getLogger(ConfiguredExperiment.class);
	private static final Configuration configuration = Configuration.getConfiguration();
	private final ThreadedContext random;
	private final int id;

	private final File outputDir;
	private final int numLearningGames;
	private final int numTestingGames;
	private final int testFrequency;
	private final double explorationDecrease;
	private final int explorationDecreaseFrequency;
	private final boolean serialize;

	private final double alpha;
	private final double eps;
	private final String tuples;

	public TDLTetrisExperiment() {
		this.id = configuration.getInt(Config.EXPERIMENT_ID, 0);
		this.outputDir = configuration.getFile(Config.EXPERIMENT_OUTPUT);
		int threads = configuration.getInt(Config.EXPERIMENT_THREADS, 1);
		this.random = new ThreadedContext(configuration.getSeed(Config.EXPERIMENT_SEED), threads);
		this.numLearningGames = configuration.getInt(new ConfKey("num_learning_games"), 1000000);
		this.numTestingGames = configuration.getInt(new ConfKey("num_testing_games"), 1000);
		this.testFrequency = configuration.getInt(new ConfKey("test_frequency"), 5000);
		this.explorationDecreaseFrequency = configuration.getInt(new ConfKey("exploration_decrease_frequency"), 100000);
		this.explorationDecrease = configuration.getDouble(new ConfKey("exploration_decrease"), 0.9);
		this.serialize = configuration.getBoolean(new ConfKey("serialize"), true);

		this.alpha = configuration.getDouble(new ConfKey("learning_rate"), 0.001);
		this.eps = configuration.getDouble(new ConfKey("exploration_rate"), 0.1);
		this.tuples = configuration.getString(new ConfKey("tuples"), "1111|1111|1111|1111");
	}

	private final SerializationManager serializer = SerializationManagerFactory.create();

	@Override
	public void run() {
		RandomDataGenerator rdg = random.getRandomForThread();

		final List<Double> alphas = of(alpha);
		final List<Double> epses = of(eps);

		final TableBuilder tableBuilder = new TableBuilder("effort", "alpha", "eps", "perf", "time");

		double bestPerformance = 0;

		for (double eps : epses) {
			for (double alpha : alphas) {
				double currentEps = eps;

				TetrisNTuplesSystematicFactory ntuplesFactory = new TetrisNTuplesSystematicFactory(0, 0, tuples);
				NTuples nTuples = ntuplesFactory.createRandomIndividual(rdg);

				Tetris tetris = Tetris.newSZTetris();
				NTuplesStateValueFunction<TetrisState> valueFunction = new NTuplesStateValueFunction<>(nTuples);
				DeltaNTuplesTetrisAgent deltaTetrisAgent = new DeltaNTuplesTetrisAgent(nTuples);
				TetrisExpectedUtility performanceMeasure = new TetrisExpectedUtility(tetris, numTestingGames);
				TDAfterstateValueLearning<TetrisState, TetrisAction> algorithm = new TDAfterstateValueLearning<>(tetris,
						valueFunction, deltaTetrisAgent);

				long startTime = System.nanoTime();
				for (int i = 0; i <= numLearningGames; i++) {
					algorithm.fastLearningEpisode(currentEps, alpha, rdg);

					if (i > 0 && i % explorationDecreaseFrequency == 0) {
						currentEps *= explorationDecrease;
					}

					if (i % testFrequency == 0) {
						Measurement performance = performanceMeasure.measure(deltaTetrisAgent, random);

						long elapsedTime = System.nanoTime() - startTime;
						double meanPerformance = performance.stats().getMean();
						tableBuilder.addRow(i, alpha, eps, format(meanPerformance), (elapsedTime / 1000000));
						saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));

						if (meanPerformance > bestPerformance && serialize) {
							bestPerformance = meanPerformance;
							serializer.serializeWrapExceptions(nTuples, new File(outputDir, "run-" + id + "-" + alpha
									+ ".bin"));
						}
					}
				}
			}
		}
		saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));
		logger.info("Saving results to: " + new File(outputDir, "run-" + id + ".csv"));
	}

	public static void main(String[] args) {
		TDLTetrisExperiment experiment = new TDLTetrisExperiment();
		experiment.run(args);
	}

	@Override
	public void run(String[] args) {
		TDLTetrisExperiment experiment = new TDLTetrisExperiment();
		experiment.run();
	}
}
