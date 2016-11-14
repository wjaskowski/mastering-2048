package put.ci.cevo.profiles.experiments;

import java.io.File;

public class PolarisPathProvider {

	public static File getProfilesDBDir() {
		return new File(getPolarisPath(), "/ieeetec/profiles/db/");
	}

	public static File getPolarisPath() {
		String polarisPath = System.getenv("POLARIS_COEV_PATH");
		if (polarisPath == null) {
			throw new RuntimeException("Environment variable with path to the Polaris server not set!");
		}
		return new File(polarisPath);
	}

}
