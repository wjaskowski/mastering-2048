package put.ci.cevo.rl.evaluation;

import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;

public interface ActionValueFunction<S extends State, A extends Action> {
	/**
	 * How good a given action in a given state is. Interpretation: larger is better.
	 */
	double getValue(S state, A action);
}
