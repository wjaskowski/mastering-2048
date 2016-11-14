package put.ci.cevo.rl.evaluation;

import com.google.common.base.Preconditions;
import put.ci.cevo.rl.environment.FeaturesExtractor;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.util.vectors.DoubleVector;
import org.apache.commons.lang.NotImplementedException;

/**
 * Provides a value function which is is a sum of weighted features
 */
public class LinearFeaturesStateValueFunction<S extends State> implements LearnableStateValueFunction<S> {
	private DoubleVector parameters;
	private final FeaturesExtractor<S> featuresExtractor;

	public LinearFeaturesStateValueFunction(FeaturesExtractor<S> featuresExtractor, DoubleVector parameters) {
		Preconditions.checkArgument(parameters.size() == featuresExtractor.featuresCount());
		this.featuresExtractor = featuresExtractor;
		this.parameters = parameters;
	}

	@Override
	public double getValue(S state) {
		double[] features = featuresExtractor.getFeatures(state);
		return parameters.dot(DoubleVector.of(features));
	}

	@Override
	public void increase(S state, double delta) {
		double[] features = featuresExtractor.getFeatures(state);
		double[] updates = new double[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			updates[i] = delta * features[i];
		}

		parameters = parameters.add(DoubleVector.of(updates));
	}

	@Override
	public int getActiveFeaturesCount() {
		return parameters.size();
	}

	@Override
	public double getActiveWeight(S state, int idx) {
		throw new NotImplementedException();
	}

	@Override
	public void setActiveWeight(S state, int idx, double value) {
		throw new NotImplementedException();
	}

	@Override
	public void increaseActiveWeight(S state, int idx, double delta) {
		throw new NotImplementedException();
	}
}
