package put.ci.cevo.experiments.cig2048;

import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.evaluation.LearnableActionValueFunction;

public class ActionValueFunction2048 implements LearnableActionValueFunction<State2048, Action2048> {

	// 4 functions - UP, RIGHT, DOWN, LEFT
	private RealFunction[] functions;

	public ActionValueFunction2048(RealFunction[] functions) {
		this.functions = functions;
	}

	@Override
	public double getValue(State2048 state, Action2048 action) {
		return functions[action.ordinal()].getValue(state.getFeatures());
	}

	@Override
	public void update(State2048 state, Action2048 action, double expectedValue, double learningRate) {
		functions[action.ordinal()].update(state.getFeatures(), expectedValue, learningRate);
	}
}
