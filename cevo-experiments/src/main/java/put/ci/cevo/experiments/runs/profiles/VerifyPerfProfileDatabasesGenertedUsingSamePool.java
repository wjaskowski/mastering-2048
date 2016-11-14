package put.ci.cevo.experiments.runs.profiles;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase.Bucket;
import put.ci.cevo.framework.factories.StaticPopulationFactory;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;
import static put.ci.cevo.profiles.experiments.PolarisPathProvider.getProfilesDBDir;

/**
 * Checks whether our profile databases were generated using the same testing pool of random individuals. Does not test
 * all individuals - just o few of them from each bucket
 */
public class VerifyPerfProfileDatabasesGenertedUsingSamePool {
	private static Logger logger = Logger.getLogger(VerifyPerfProfileDatabasesGenertedUsingSamePool.class);

	// @formatter:off
	private static List<File> DB_FILES = Arrays.asList(
		new File(getProfilesDBDir(), "othello-bw-profile-db-rsel.dump"),
		new File(getProfilesDBDir(), "othello-bw-profile-db-rsel-symmetric.dump"),
		new File(getProfilesDBDir(), "othello-pprofile-db-random-new.dump"));
	// @formatter:on

	private static final int NUM_INDIVIDUALS_FROM_BUCKET = 3;

	public static void main(String[] args) throws SerializationException {
		SerializationManager serializationManager = SerializationManagerFactory.create();
		ArrayList<WPC> pool = serializationManager.deserialize(new File(getProfilesDBDir(), "othello_wpc_pool.dump"));

		final ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<WPC, WPC>(new OthelloWPCInteraction(true),
			new StaticPopulationFactory<WPC>(pool), pool.size());

		for (File file : DB_FILES) {
			logger.info(file);
			PerfProfileDatabase<WPC> db = serializationManager.deserialize(file);

			for (Bucket<WPC> bucket : db) {
				logger.info("Bucket no " + bucket.getBucketNo());
				List<EvaluatedIndividual<WPC>> sample = bucket.getIndividuals()
					.subList(0, min(bucket.size(), NUM_INDIVIDUALS_FROM_BUCKET));

				ThreadedContext context = new ThreadedContext(1337);
				context.submit(new ThreadedContext.Worker<EvaluatedIndividual<WPC>, Void>() {
					@Override
					public Void process(EvaluatedIndividual<WPC> ind, ThreadedContext context) {
						double perf = measure.measure(ind.getIndividual(), context).stats().getMean();
						if (perf != ind.getFitness()) {
							logger.warn("We have a problem because " + perf + " != " + ind.getFitness());
						}
						return null;
					}
				}, sample);
			}
		}
		logger.info("Finished");
	}
}
