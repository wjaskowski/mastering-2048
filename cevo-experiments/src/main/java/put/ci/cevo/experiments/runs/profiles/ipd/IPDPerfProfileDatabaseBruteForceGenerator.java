package put.ci.cevo.experiments.runs.profiles.ipd;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ipd.IPDStrategiesGenerator;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase.PerfProfileDatabaseBuilder;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.vectors.IntegerVector;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.info.ProgressInfo;
import put.ci.cevo.util.info.TextProgressInfo;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static put.ci.cevo.experiments.runs.profiles.ipd.IPDPerfProfileDatabaseBruteForceGenerator.Config.*;

public class IPDPerfProfileDatabaseBruteForceGenerator implements Runnable {

	public static enum Config implements ConfigurationKey {
		EXPERIMENT_NAME("experiment.name"),
		EXPERIMENT_SEED("experiment.seed"),

		IPD_MIN_GENE("ipd.minGene"),
		IPD_MAX_GENE("ipd.maxGene"),
		IPD_GENOME_LENGTH("ipd.genomeLength"),

		DB_SLICE_SIZE("db.sliceSize"),
		DB_BUCKETS("db.buckets"),
		DB_MAX_BUCKET_SIZE("db.maxBucketSize"),

		DB_OUTPUT_FILE("db.outputFile");

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

	private final Runnable savingTask = new Runnable() {
		@Override
		public void run() {
			logger.info("Saving database to: " + dbFile);
			synchronized (builder) {
				PerfProfileDatabase<IntegerVector> db = builder.buildPerfProfileDatabase();
				try {
					serializer.serialize(db, dbFile);
				} catch (SerializationException e) {
					logger.error("Unable to serialize database!", e);
				}
				logger.info("Serialized database: " + db);
			}
		}
	};

	private static final int DEFAULT_SLICE_SIZE = 10000;

	private static final Logger logger = Logger.getLogger(IPDPerfProfileDatabaseBruteForceGenerator.class);

	private static final Configuration configuration = Configuration.getConfiguration();
	private static final SerializationManager serializer = SerializationManagerFactory.create();

	private final PerformanceMeasure<IntegerVector> measure;
	private final IPDStrategiesGenerator generator;

	private final PerfProfileDatabaseBuilder<IntegerVector> builder;
	private final ThreadedContext context;

	private final File dbFile;

	@AccessedViaReflection
	public IPDPerfProfileDatabaseBruteForceGenerator(PerformanceMeasure<IntegerVector> measure,
			IPDStrategiesGenerator generator) {
		this.measure = measure;
		this.generator = generator;
		this.builder = new PerfProfileDatabaseBuilder<>(
			configuration.getInt(DB_BUCKETS), configuration.getInt(DB_MAX_BUCKET_SIZE));
		this.context = new ThreadedContext(configuration.getSeed(EXPERIMENT_SEED));
		this.dbFile = configuration.getFile(DB_OUTPUT_FILE);
	}

	@Override
	public void run() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(savingTask, 10, 30, TimeUnit.MINUTES);

		final ProgressInfo info = new TextProgressInfo("PerfProfileBuilding").withInfoInterval(30 * 60);
		context.submit(new ThreadedContext.Worker<Sequence<IntegerVector>, Void>() {
			@Override
			public Void process(Sequence<IntegerVector> candidateSolutions, ThreadedContext context) throws Exception {
				for (IntegerVector solution : candidateSolutions) {
					double performance = measure.measure(solution, context).stats().getMean();
					builder.addIndividual(new EvaluatedIndividual<>(solution, performance));
				}
				info.multiProcessed(candidateSolutions.size());
				return null;
			}
		}, generator.createStrategies().slice(configuration.getInt(DB_SLICE_SIZE, DEFAULT_SLICE_SIZE)));

		logger.info("Shutting down the main thread");
		executor.execute(savingTask);
		executor.shutdown();
		logger.info("Awaiting termination...");
		try {
			executor.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.error("Interrupted while awaiting termination", e);
		}
	}

	public static void main(String[] args) {
		IPDPerfProfileDatabaseBruteForceGenerator exp = configuration.getObject(new ConfKey("experiment"));
		exp.run();
	}
}
