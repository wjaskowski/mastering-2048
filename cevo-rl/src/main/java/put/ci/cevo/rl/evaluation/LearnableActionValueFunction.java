package put.ci.cevo.rl.evaluation;

import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;

public interface LearnableActionValueFunction<S extends State, A extends Action> extends ActionValueFunction<S, A> {

	void update(S state, A action, double targetValue, double learningRate);
}
