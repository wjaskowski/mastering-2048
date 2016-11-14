package put.ci.cevo.experiments.cig2048;

import static put.ci.cevo.util.TableUtil.saveTableAsCSV;

import java.io.File;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;

import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class EvaluateCox2048Experiment implements Runnable {

	public enum Config implements ConfigurationKey {
		EXPERIMENT_ID("experiment.id"),
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
	private final int id;

	private final File outputDir;
	private final int numTestingGames;

	public EvaluateCox2048Experiment() {
		this.id = configuration.getInt(Config.EXPERIMENT_ID);
		this.outputDir = configuration.getFile(Config.EXPERIMENT_OUTPUT);
		this.numTestingGames = configuration.getInt(new ConfKey("num_testing_games"), 100000);
	}

	private final SerializationManager serializer = SerializationManagerFactory.create();

	@Override
	public void run() {
		final RandomDataGenerator random = new RandomDataGenerator();

		final TableBuilder tableBuilder = new TableBuilder("player", "won", "score", "game-time");
		Game2048TDLearning game = new Game2048TDLearning();

		File[] playerFiles = new File("/home/mszubert/CEVO/tdl-afterstate-cox-15").listFiles((file, name) -> {
			return name.endsWith("a.bin");
		});

		for (File file : playerFiles) {
			String playerName = file.getName();
			logger.info("Evaluating player " + playerName);

			NTuples vFunction = serializer.deserializeWrapExceptions(file);

			for (int j = 0; j <= numTestingGames; j++) {
				long startTime = System.nanoTime();
				Game2048Outcome res = game.playByAfterstates(vFunction, random);
				long elapsedTime = System.nanoTime() - startTime;


				tableBuilder.addRow(playerName, (res.getLastState().getMaxTile() >= 2048) ? 1 : 0, res.score(), (elapsedTime / 1000000));

				if (j % 100 == 0) {
					saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));
				}
			}
			saveTableAsCSV(tableBuilder.build(), new File(outputDir, "run-" + id + ".csv"));
			logger.info("Saving results to: " + new File(outputDir, "run-" + id + ".csv"));
		}
	}

	public static void main(String[] args) {
		EvaluateCox2048Experiment experiment = new EvaluateCox2048Experiment();
		experiment.run();
	}
}
