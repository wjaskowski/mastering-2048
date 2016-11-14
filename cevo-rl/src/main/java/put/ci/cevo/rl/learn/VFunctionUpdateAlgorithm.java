package put.ci.cevo.rl.learn;

import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public interface VFunctionUpdateAlgorithm<S extends State> {
	void update(LearnableStateValueFunction<S> vFunction, S state, double targetValue);
}
