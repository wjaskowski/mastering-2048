package put.ci.cevo.rl.learn;

import static java.lang.Math.abs;

import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public class TCLVFunctionUpdateAlgorithm<S extends State> implements VFunctionUpdateAlgorithm<S> {

	private final double metaLearningRate;
	private final LearnableStateValueFunction<S> absErrors;
	private final LearnableStateValueFunction<S> errors;

	public TCLVFunctionUpdateAlgorithm(double metaLearningRate, LearnableStateValueFunction<S> absErrors,
			LearnableStateValueFunction<S> errors) {
		this.metaLearningRate = metaLearningRate;
		this.absErrors = absErrors;
		this.errors = errors;
	}

	public void update(LearnableStateValueFunction<S> vFunction, S state, double targetValue) {
		int n = vFunction.getActiveFeaturesCount();
		double metaLearningRatePerWeight = metaLearningRate / n;

		double[] absErrors = new double[n];
		double[] normErrors = new double[n];
		double[] w = new double[n];

		for (int i = 0; i < n; ++i) {
			absErrors[i] = this.absErrors.getActiveWeight(state, i);
			normErrors[i] = errors.getActiveWeight(state, i);
			w[i] = vFunction.getActiveWeight(state, i);
		}

		double currentValue = 0.0;
		for (int i = 0; i < n; ++i) {
			currentValue += w[i];
		}
		double error = targetValue - currentValue;

		for (int i = 0; i < n; ++i) {
			double alpha = absErrors[i] == 0.0 ? 1 : abs(normErrors[i]) / absErrors[i];

			w[i] += metaLearningRatePerWeight * alpha * error;

			normErrors[i] += error;
			absErrors[i] += abs(error);
		}

		for (int i = 0; i < n; ++i) {
			this.absErrors.setActiveWeight(state, i, absErrors[i]);
			this.errors.setActiveWeight(state, i, normErrors[i]);
			vFunction.setActiveWeight(state, i, w[i]);
		}
	}
}
