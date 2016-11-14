package put.ci.cevo.rl.environment;

/**
 * Extracts feature values (e.g number of white pieces) from a given state (e.g. a board).
 */
public interface FeaturesExtractor<S extends State> {
	/**
	 * Always return featuresCount features
	 */
	double[] getFeatures(S state);

	/** The number of features getFeatures() return */
	int featuresCount();
}
