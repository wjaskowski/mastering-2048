package put.ci.cevo.experiments.runs.profiles.generic;

import org.joda.time.Duration;
import put.ci.cevo.experiments.profiles.generators.StrategyGenerator;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;

import java.io.File;
import java.io.Serializable;

import static put.ci.cevo.experiments.runs.profiles.generic.PerfProfileDatabaseGeneratorConfigured.Config.*;
import static put.ci.cevo.util.configuration.Configuration.getConfiguration;

/**
 * A config-based experiment, which generates a performance profile database
 */
public final class PerfProfileDatabaseGeneratorConfigured<T> implements Runnable {

	public static enum Config implements ConfigurationKey {
		DB_FILE("db_file"),

		NUM_BUCKETS("num_buckets"),
		MAX_BUCKET_SIZE("max_bucket_size"),
		NUM_OPPONENTS("num_opponents"),

		CHECKPOINT_INTERVAL_SECONDS("checkpoint_interval_seconds"),
		BACKUP_INTERVAL_HOURS("backup_interval_hours"),

		RANDOM_SEED("seed"),

		EXPERIMENT("experiment");

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

	private static final Configuration config = Configuration.getConfiguration();

	private final PerfProfileDatabaseGenerator<?> experiment;

	@AccessedViaReflection
	public PerfProfileDatabaseGeneratorConfigured(PerformanceMeasure<T> performanceMeasure,
			StrategyGenerator<T> strategyGenerator) {

		final int numBuckets = config.getInt(NUM_BUCKETS);
		final int maxBucketSize = config.getInt(MAX_BUCKET_SIZE);
		final File dbFile = config.getFile(Config.DB_FILE);
		final Duration checkpointInterval = Duration.standardSeconds(config.getInt(CHECKPOINT_INTERVAL_SECONDS));
		final Duration backupInterval = Duration.standardHours(config.getInt(BACKUP_INTERVAL_HOURS));
		final ThreadedContext random = new ThreadedContext(config.getSeed(Config.RANDOM_SEED));

		experiment = new PerfProfileDatabaseGenerator<>(
			performanceMeasure, strategyGenerator, numBuckets, maxBucketSize, dbFile, backupInterval,
			checkpointInterval, random);
	}

	@Override
	public void run() {
		experiment.run();
	}

	public static <T extends Serializable> void main(String[] args) {
		final PerfProfileDatabaseGeneratorConfigured<T> databaseGenerator = getConfiguration().getObject(EXPERIMENT);
		databaseGenerator.run();
	}
}
