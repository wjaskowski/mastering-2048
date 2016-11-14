package put.ci.cevo.experiments.runs.profiles.generic;

import com.hazelcast.core.HazelcastInstance;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.experiments.profiles.PerfProfileHazelcastDatabase;
import put.ci.cevo.experiments.profiles.generators.StrategyGenerator;
import put.ci.cevo.framework.hazelcast.HazelCastFactory;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;

import static put.ci.cevo.framework.hazelcast.DataStructureName.RUN_ID;

public final class PerfProfileDatabaseGenerator<T> implements Runnable {

	private static final Logger logger = Logger.getLogger(PerfProfileDatabaseGenerator.class);

	private final HazelcastInstance hazelcast = HazelCastFactory.getInstance();
	private final ThreadedContext context;

	private final long workerId;

	private DateTime lastCheckpointTimestamp = new DateTime(0);
	private DateTime lastBackupTimestamp = new DateTime();

	private final SerializationManager serializationManager = SerializationManagerFactory.create();

	private final StrategyGenerator<T> strategyGenerator;
	private final PerformanceMeasure<T> performanceMeasure;

	private final int numBuckets;
	private final int maxBucketSize;
	private final File dbFile;
	private final Duration backupInterval;
	private final Duration checkpointInterval;

	private final boolean isMainWorker;
	private int totalNumStrategies;

	public PerfProfileDatabaseGenerator(PerformanceMeasure<T> performanceMeasure,
			StrategyGenerator<T> strategyGenerator, int numBuckets, int maxBucketSize, File dbFile,
			Duration backupInterval, Duration checkpointInterval, ThreadedContext context) {
		this(performanceMeasure, strategyGenerator, numBuckets, maxBucketSize, Integer.MAX_VALUE, dbFile,
			backupInterval, checkpointInterval, context);
	}

	// TODO: Make master an independent observer!
	public PerfProfileDatabaseGenerator(PerformanceMeasure<T> performanceMeasure,
			StrategyGenerator<T> strategyGenerator, int numBuckets, int maxBucketSize, int totalNumStrategies,
			File dbFile, Duration backupInterval, Duration checkpointInterval, ThreadedContext context) {
		this.performanceMeasure = performanceMeasure;
		this.strategyGenerator = strategyGenerator;
		this.numBuckets = numBuckets;
		this.maxBucketSize = maxBucketSize;
		this.totalNumStrategies = totalNumStrategies;
		this.dbFile = dbFile;
		this.backupInterval = backupInterval;
		this.checkpointInterval = checkpointInterval;
		this.workerId = hazelcast.getAtomicNumber(RUN_ID.getName()).incrementAndGet();
		this.context = context;

		this.isMainWorker = (workerId == 1);

		logger.info("WorkerId = " + workerId + " isMainWorker = " + isMainWorker);
	}

	@Override
	public void run() {
		logger.info("Creating hazelcast DB (a shared list for every bucket)");
		PerfProfileHazelcastDatabase<T> db = new PerfProfileHazelcastDatabase<>(numBuckets, maxBucketSize);

		if (isMainWorker) {
			logger.info("Initializing hazelcast DB");
			if (dbFile.exists()) {
				try {
					db.merge(PerfProfileDatabase.<T> fromFile(dbFile));
				} catch (SerializationException e) {
					logger.error("Unable to deserialize database", e);
				}
			}
		}

		while (db.countTotalIndividuals() < totalNumStrategies) {
			final T strategy = strategyGenerator.createNext(context);

			double performance = performanceMeasure.measure(strategy, context).stats().getMean();
			EvaluatedIndividual<T> evaluatedIndividual = new EvaluatedIndividual<T>(strategy, performance);

			if (db.add(evaluatedIndividual)) {
				// logger.info("Hurra! We have found a new individual!");
				strategyGenerator.reset();
			}

			if (isMainWorker) {
				try {
					makeCheckpoint(db);
					makeBackup(db);
				} catch (SerializationException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void makeCheckpoint(PerfProfileHazelcastDatabase<T> hazelcastDB) throws SerializationException {
		final DateTime now = new DateTime();
		if (new Duration(lastCheckpointTimestamp, now).isLongerThan(checkpointInterval)) {
			logger.info("Making checkpoint");
			// TODO: Making a checkpoint lasts very long: do something about it
			PerfProfileDatabase<T> db = hazelcastDB.toPerfProfileDatabase();
			serializationManager.serialize(db, dbFile);
			logger.info(db.toString());
			lastCheckpointTimestamp = now;
		}
	}

	private void makeBackup(PerfProfileHazelcastDatabase<T> hazelcastDB) throws SerializationException {
		final DateTime now = new DateTime();
		if (new Duration(lastBackupTimestamp, now).isLongerThan(backupInterval)) {
			logger.info("Making backup");
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm:ss");
			final File backupFile = new File(dbFile.getAbsolutePath() + '.' + now.toString(fmt) + ".bak");
			PerfProfileDatabase<T> db = hazelcastDB.toPerfProfileDatabase();
			serializationManager.serialize(db, backupFile);
			lastBackupTimestamp = now;
		}
	}

}
