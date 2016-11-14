package put.ci.cevo.experiments.runs.profiles;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.profiles.experiments.PolarisPathProvider;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PerfProfileDatabaseLookForDuplicates {
	private static Logger logger = Logger.getLogger(PerfProfileDatabaseLookForDuplicates.class);

	// @formatter:off
	private static List<File> DB_FILES = Arrays.asList(
		//new File(DB_DIR, "othello-bw-profile-db-rsel.dump"));
//		new File(PolarisPathProvider.getProfilesDBDir(), "othello-bw-profile-db-rsel-symmetric.dump")
		new File(PolarisPathProvider.getProfilesDBDir(), "othello-pprofile-db-random-new.dump")
	);
	// @formatter:on

	public static void main(String[] args) throws SerializationException {
		SerializationManager serializationManager = SerializationManagerFactory.create();
		for (File file : DB_FILES) {
			logger.info(file);
			final PerfProfileDatabase<WPC> db = serializationManager.deserialize(file);
			System.out.println(file);
			System.out.println(db);

			HashSet<EvaluatedIndividual<WPC>> set = new HashSet<EvaluatedIndividual<WPC>>();

			for (int b = 0; b < db.getNumBuckets(); ++b) {
				List<EvaluatedIndividual<WPC>> bucket = db.getBucketPlayers(b);
				int numDuplicates = 0;
				for (int i = 0; i < bucket.size(); i++) {
					EvaluatedIndividual<WPC> ind = bucket.get(i);
					boolean aNewOne = set.add(ind);
					if (!aNewOne) {
						// System.out.println(String.format("Duplicates found in bucket %02d of perf %.3f\n%s", b,
						// ind.getFitness(), new OthelloWPC(ind.getIndividual()).toString()));
						@SuppressWarnings("unchecked")
						EvaluatedIndividual<WPC>[] arr = set.toArray(new EvaluatedIndividual[0]);
						for (int j = 0; j < arr.length; ++j) {
							if (arr[j].equals(ind)) {
								// System.out.println(new OthelloWPC(arr[j].getIndividual()).toString());
							}
						}
						numDuplicates += 1;
					}
				}
				System.out.println(b + ": " + numDuplicates);
			}
		}
		logger.info("Finished");
	}
}
