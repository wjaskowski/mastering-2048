package put.ci.cevo.experiments.runs.profiles.generic;

import put.ci.cevo.framework.individuals.loaders.FilesIndividualLoader;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static put.ci.cevo.experiments.runs.profiles.generic.PerfProfileGeneratorConfigured.Config.*;
import static put.ci.cevo.util.configuration.Configuration.getConfiguration;

/**
 * A config-based version of performance profile generator
 * 
 * @param <T>
 *            genotype
 */
public class PerfProfileGeneratorConfigured<T> implements Runnable {

	public static enum Config implements ConfigurationKey {
		EXPERIMENT("experiment"),
		NUM_THREADS("num_threads"),
		DB_FILE("db_file"),

		RESULTS_DIR("results_input_dir"),
		RESULTS_SUBDIRS("results_subdirs"),
		RESULTS_WILDCARD("results_wildcard"),
		INDIVIDUALS_LOADER("individuals_loader"),

		PROFILES_OUTPUT_DIR("profiles_output_dir");

		private final String key;

		private Config(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return key;
		}

		@Override
		public ConfigurationKey dot(Object subKey) {
			return ConfKey.dot(this, subKey);
		}
	}

	private final static Configuration config = Configuration.getConfiguration();
	private final PerfProfileGenerator<T> experiment;

	@AccessedViaReflection
	public PerfProfileGeneratorConfigured(InteractionDomain<T, T> interaction) {
		// TODO: Move them as constructor parameter
		final FilesIndividualLoader<T> playersLoader = config.getObject(INDIVIDUALS_LOADER);
		final File dbFile = config.getFile(DB_FILE);

		final String resultsWildcard = config.getString(RESULTS_WILDCARD);
		final File resultsDir = config.getFile(RESULTS_DIR);
		final File profilesOutputDir = config.getFile(PROFILES_OUTPUT_DIR);
		final int numThreads = config.getInt(NUM_THREADS);
		final List<String> resultsSubdirs = config.getList(RESULTS_SUBDIRS);

		experiment = new PerfProfileGenerator<T>(
			interaction, numThreads, dbFile, profilesOutputDir, resultsWildcard, resultsDir, resultsSubdirs,
			playersLoader);
	}

	@Override
	public void run() {
		experiment.run();
	}

	public static <T extends Serializable> void main(String[] args) {
		PerfProfileGeneratorConfigured<T> profileGenerator = getConfiguration().getObject(EXPERIMENT);
		profileGenerator.run();
	}
}
