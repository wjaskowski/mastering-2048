package put.ci.cevo.experiments.cig2048.sanitykey;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;

import java.io.File;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.ImmutableList.of;
import static put.ci.cevo.util.TableUtil.saveTableAsCSV;

class EvaluateAlphaBeta2048Experiment implements Runnable {

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
	private final int id;

	private final File outputDir;
	private final int numTestingGames;

	public EvaluateAlphaBeta2048Experiment() {
		this.id = configuration.getInt(Config.EXPERIMENT_ID);
		this.outputDir = configuration.getFile(Config.EXPERIMENT_OUTPUT);
		this.numTestingGames = configuration.getInt(new ConfKey("num_testing_games"), 50);
	}

	@Override
	public void run() {
		final Random random = new Random();

		final List<Integer> times = of(100);
		final TableBuilder tableBuilder = new TableBuilder("search-time", "won", "moves", "game-time");

		for (long time : times) {
			final Game game = new Game(random);
			IDDFSABPlayer player = new IDDFSABPlayer(random, game);
			game.setPlayer(player);

			for (int i = 0; i <= numTestingGames; i++) {
				game.reset();

				long startTime = System.nanoTime();
				game.playGame();
				long elapsedTime = System.nanoTime() - startTime;

				Boolean hasWon = game.getHasWon();
				int numMoves = -1000000; // game.getNumMoves(); HACK/FIXME (Marcin: Something uncommited?)
				tableBuilder.addRow(time, hasWon ? 1 : 0, numMoves, (elapsedTime / 1000000));

				if (i % 10 == 0) {
					saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));
				}
			}
		}
		saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));
		logger.info("Saving results to: " + new File(outputDir, "run-" + id + ".csv"));
	}

	public static void main(String[] args) {
		EvaluateAlphaBeta2048Experiment experiment = new EvaluateAlphaBeta2048Experiment();
		experiment.run();
	}
}
