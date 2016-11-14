package put.ci.cevo.experiments.runs.profiles.generic;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.profiles.PerfProfile;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.framework.individuals.loaders.FilesIndividualLoader;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PerfProfileGenerator<T> implements Runnable {

	private static final Logger logger = Logger.getLogger(PerfProfileGenerator.class);
	private static final SerializationManager serializationManager = SerializationManagerFactory.create();

	private final InteractionDomain<T, T> interaction;
	private final int numThreads;
	private final PerfProfileDatabase<T> db;
	private final File profilesOutputDir;
	private final String resultsWildcard;
	private final File resultsDir;

	private final List<String> resultsSubdirs;

	private final FilesIndividualLoader<T> playersLoader;

	// TODO: Change results* into one class
	// TODO: dbFile param should be PerfProfileDatabase<T> instead of File
	public PerfProfileGenerator(InteractionDomain<T, T> interaction, int numThreads, File dbFile,
			File profilesOutputDir, String resultsWildcard, File resultsDir, List<String> resultsSubdirs,
			FilesIndividualLoader<T> playersLoader) {
		this.numThreads = numThreads;
		this.interaction = interaction;
		this.profilesOutputDir = profilesOutputDir;
		this.resultsWildcard = resultsWildcard;
		this.resultsDir = resultsDir;
		this.resultsSubdirs = resultsSubdirs;
		this.playersLoader = playersLoader;

		this.db = serializationManager.deserializeWrapExceptions(dbFile);
	}

	@Override
	public void run() {
		ThreadedContext context = new ThreadedContext(123, numThreads);

		for (String experiment : resultsSubdirs) {
			logger.info("Loading players for: " + experiment);
			List<T> players = playersLoader.loadIndividuals(new File(resultsDir, experiment), resultsWildcard);
			logger.info("Loaded " + players.size() + " players");

			logger.info("Generating a profile");
			PerfProfile pp = PerfProfile.createForPlayerTeam(db, interaction, players, context);

			saveProfile(experiment, pp);
			logger.info("Profile generated and saved");
		}
	}

	private void saveProfile(String exp, PerfProfile pp) {
		try {
			pp.saveAsCSV(new File(profilesOutputDir, exp + ".profile"));
		} catch (IOException e) {
			logger.error("Unable to save performance profile for experiment: " + exp, e);
		}
	}

}
