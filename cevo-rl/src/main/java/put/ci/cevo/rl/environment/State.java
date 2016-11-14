package put.ci.cevo.rl.environment;

public interface State {

	//TODO: I think that we do not need this since we have FeatureExtractor. Then, actually, we do not need the State class altogether
	double[] getFeatures();
}
