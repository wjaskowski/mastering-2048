package put.ci.cevo.experiments.cig2048;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.experiments.ntuple.NTuplesAllRectanglesFactory;
import put.ci.cevo.experiments.ntuple.NTuplesAllStraightFactory;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.IdentitySymmetryExpander;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static put.ci.cevo.util.TableUtil.saveTableAsCSV;
import static put.ci.cevo.util.TextUtils.format;

public class OptimizeGame2048TDLExperiment implements Runnable, Experiment {

	private static enum Config implements ConfigurationKey {
		EXPERIMENT_ID("experiment.id"),
		EXPERIMENT_SEED("experiment.seed"),
		EXPERIMENT_THREADS("experiment.threads"),
		EXPERIMENT_OUTPUT("experiment.output");

		private final String key;

		private Config(String key) {
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
	private final ThreadedRandom random;
	private final int id;

	private final File outputDir;
	private final int numLearningGames;
	private final int numTestingGames;
	private final int testFrequency;
	private final boolean expectimax;
	private final double explorationDecrease;
	private final int explorationDecreaseFrequency;
	private final boolean serialize;
	private final double alpha;

	public OptimizeGame2048TDLExperiment() {
		this.id = configuration.getInt(Config.EXPERIMENT_ID);
		this.random = new ThreadedRandom(configuration.getSeed(Config.EXPERIMENT_SEED));
		this.outputDir = configuration.getFile(Config.EXPERIMENT_OUTPUT);
		this.numLearningGames = configuration.getInt(new ConfKey("num_learning_games"), 500000);
		this.numTestingGames = configuration.getInt(new ConfKey("num_testing_games"), 1000);
		this.testFrequency = configuration.getInt(new ConfKey("test_frequency"), 5000);
		this.expectimax = configuration.getBoolean(new ConfKey("expectimax"), false);
		this.explorationDecreaseFrequency = configuration.getInt(new ConfKey("exploration_decrease_frequency"), 100000);
		this.explorationDecrease = configuration.getDouble(new ConfKey("exploration_decrease"), 1.0);
		this.serialize = configuration.getBoolean(new ConfKey("serialize"), true);
		this.alpha = configuration.getDouble(new ConfKey("alpha"), 0.0);
	}

	private final SerializationManager serializer = SerializationManagerFactory.create();

	@Override
	public void run() {
		RandomDataGenerator rdg = random.forThread();

		final List<Double> alphas = of(alpha);
		final List<Double> epses = of(0.0);// , 0.000, 0.01);
		final TableBuilder tableBuilder = new TableBuilder("effort", "alpha", "eps", "ratio", "perf", "time");
		for (double eps : epses) {
			for (double alpha : alphas) {
				double currentEps = eps;
				Game2048TDLearning game = new Game2048TDLearning();
				NTuples lines = new NTuplesAllStraightFactory(
					4, State2048.BOARD_SIZE, 14, 0, 0, new IdentitySymmetryExpander())
					.createRandomIndividual(random.forThread());
				NTuples squares = new NTuplesAllRectanglesFactory(
					new RectSize(2), State2048.BOARD_SIZE, 14, 0, 0, new IdentitySymmetryExpander())
					.createRandomIndividual(random.forThread());
				NTuples vFunction = lines.add(squares);

				long startTime = System.nanoTime();
				for (int i = 0; i <= numLearningGames; i++) {
					if (expectimax) {
						game.TDExpectimaxLearn(vFunction, currentEps, alpha, rdg);
					} else {
						game.TDAfterstateLearn(vFunction, currentEps, alpha, rdg);
					}

					if (i % explorationDecreaseFrequency == 0) {
						currentEps *= explorationDecrease;
					}

					if (i % testFrequency == 0) {
						double ratio = 0;
						final SummaryStatistics stats = new SummaryStatistics();
						for (int j = 0; j < numTestingGames; j++) {
							Game2048Outcome res = null;
							if (expectimax) {
								res = game.playByExpectimax(vFunction, rdg);
							} else {
								res = game.playByAfterstates(vFunction, rdg);
							}

							stats.addValue(res.score());
							ratio += (res.getLastState().getMaxTile() >= 2048) ? 1 : 0;
						}
						ratio /= numTestingGames;

						long elapsedTime = System.nanoTime() - startTime;
						tableBuilder.addRow(i, alpha, eps, format(ratio), format(stats.getMean()),
							(elapsedTime / 1000000));
						saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));
					}
				}

				if (serialize) {
					serializer.serializeWrapExceptions(vFunction, new File(outputDir, "run-" + id + "-" + alpha
						+ ".csv"));
				}
			}
		}
		saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));
		logger.info("Saving results to: " + new File(outputDir, "run-" + id + ".csv"));
	}

	public static void main(String[] args) {
		OptimizeGame2048TDLExperiment experiment = new OptimizeGame2048TDLExperiment();
		experiment.run(args);
	}

	@Override
	public void run(String[] args) {
		OptimizeGame2048TDLExperiment experiment = new OptimizeGame2048TDLExperiment();
		experiment.run();
	}
}
