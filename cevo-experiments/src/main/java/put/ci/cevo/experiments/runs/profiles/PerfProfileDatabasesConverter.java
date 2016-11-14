package put.ci.cevo.experiments.runs.profiles;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static put.ci.cevo.profiles.experiments.PolarisPathProvider.getProfilesDBDir;

public class PerfProfileDatabasesConverter {

	private static Logger logger = Logger.getLogger(PerfProfileDatabasesConverter.class);

	private static final SerializationManager serializer = SerializationManagerFactory.create();

	private static final List<File> OLD_DB_FILES = Arrays.asList(new File(
		getProfilesDBDir(), "othello-bw-profile-db-rsel.dump"), new File(
		getProfilesDBDir(), "othello-bw-profile-db-rsel-symmetric.dump"), new File(
		getProfilesDBDir(), "othello-pprofile-db-random-new.dump"));

	private static final List<File> NEW_DB_FILES = Arrays.asList(new File(
		getProfilesDBDir(), "converted-othello-bw-profile-db-rsel.dump"), new File(
		getProfilesDBDir(), "converted-othello-bw-profile-db-rsel-symmetric.dump"), new File(
		getProfilesDBDir(), "converted-othello-pprofile-db-random-new.dump"));

	public static void main(String[] args) throws SerializationException {
		for (int i = 0; i < OLD_DB_FILES.size(); i++) {
			logger.info(OLD_DB_FILES.get(i));
			PerfProfileDatabase<WPC> oldDB = serializer.deserialize(OLD_DB_FILES.get(i));
			serializer.serialize(oldDB, NEW_DB_FILES.get(i));
		}

		for (int i = 0; i < OLD_DB_FILES.size(); i++) {
			PerfProfileDatabase<WPC> oldDB = serializer.deserialize(OLD_DB_FILES.get(i));
			PerfProfileDatabase<WPC> newDB = serializer.deserialize(NEW_DB_FILES.get(i));
			logger.info("Elems in new DB : " + newDB.getTotalNumElements());
			logger.info("Elems in old DB : " + oldDB.getTotalNumElements());
			logger.info("Databases equal : " + oldDB.equals(newDB));
		}

	}
}
