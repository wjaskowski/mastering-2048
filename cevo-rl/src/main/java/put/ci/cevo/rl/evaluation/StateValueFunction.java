package put.ci.cevo.rl.evaluation;

import put.ci.cevo.rl.environment.State;

public interface StateValueFunction<S extends State> {

	/** Returns value of a given state */
	double getValue(S state);
}
