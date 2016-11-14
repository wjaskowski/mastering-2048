package put.ci.cevo.experiments.runs.profiles;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.factories.StaticPopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.profiles.experiments.PolarisPathProvider;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static put.ci.cevo.util.sequence.Sequences.range;

/**
 * Evaluates each bucket from performance profile database by playing 5000 double-games with each individual. Generates
 * a report for each bucket
 */
public class VerifyPerfProfileDatabasesBucketPerformances {
	private static Logger logger = Logger.getLogger(VerifyPerfProfileDatabasesBucketPerformances.class);

	// @formatter:off
	private static List<File> DB_FILES = Arrays.asList(
		//new File(DB_DIR, "othello-bw-profile-db-rsel.dump"),
		new File(PolarisPathProvider.getProfilesDBDir(), "othello-bw-profile-db-rsel-symmetric.dump"),
		new File(PolarisPathProvider.getProfilesDBDir(), "othello-pprofile-db-random-new.dump"));
	// @formatter:on

	public static void main(String[] args) throws SerializationException {
		UniformRandomPopulationFactory<WPC> populationFactory = new UniformRandomPopulationFactory<>(
			new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0));

		// Deterministic random, but a different one.
		final RandomDataGenerator globalRandom = new RandomDataGenerator(new MersenneTwister(12345));
		List<WPC> pool = populationFactory.createPopulation(5000, globalRandom);

		final ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(
			new OthelloWPCInteraction(true), new StaticPopulationFactory<>(pool), pool.size());

		SerializationManager serializationManager = SerializationManagerFactory.create();
		for (File file : DB_FILES) {
			logger.info(file);
			System.out.println(file);
			final PerfProfileDatabase<WPC> db = serializationManager.deserialize(file);

			final DescriptiveStatistics[] stats = new DescriptiveStatistics[db.getNumBuckets()];
			ThreadedContext context = new ThreadedContext(123);
			context.submit(new ThreadedContext.Worker<Integer, Void>() {
				@Override
				public Void process(Integer bucket, ThreadedContext context) throws Exception {
					DescriptiveStatistics bucketStatistics = new DescriptiveStatistics();
					for (EvaluatedIndividual<WPC> ind : db.getBucketPlayers(bucket)) {
						double perf = measure.measure(ind.getIndividual(), context).stats().getMean();
						bucketStatistics.addValue(perf);
					}
					stats[bucket] = bucketStatistics;
					return null;
				}
			}, range(db.getNumBuckets()));

			for (int i = 0; i < db.getNumBuckets(); ++i) {
				System.out.println(db.getBucketPerformance(i) + " " + stats[i].getMean() + " "
					+ stats[i].getStandardDeviation());
			}
		}
		logger.info("Finished");
	}
}
