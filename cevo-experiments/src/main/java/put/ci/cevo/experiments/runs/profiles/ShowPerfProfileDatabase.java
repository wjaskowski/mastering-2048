package put.ci.cevo.experiments.runs.profiles;

import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.*;

public class ShowPerfProfileDatabase {
	/**
	 * This code no longer works since WPC and other classes are no longer Serializable.
	 */
	public static void main(String[] args) throws SerializationException, FileNotFoundException, IOException {
		File dbFile = new File(args[0]);
		printDB(dbFile);
	}

	public static <T extends Serializable> void printDB(File dbFile) throws FileNotFoundException, IOException,
			SerializationException {
		SerializationManager serializationManager = SerializationManagerFactory.create();
		PerfProfileDatabase<T> db = serializationManager.deserialize(new BinarySerializationInput(new FileInputStream(
			dbFile)));
		System.out.println(db);
	}
}
