package put.ci.cevo.games;

import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

/**
 * It negates the value returned by the wrapped ActionEvaluator for the second game player
 */
public final class OutputNegationActionValueFunction<S extends TwoPlayerGameState, A extends Action>
		implements ActionValueFunction<S, A> {

	private final ActionValueFunction<S, A> actionValueFunction;

	public OutputNegationActionValueFunction(ActionValueFunction<S, A> actionValueFunction) {
		this.actionValueFunction = actionValueFunction;
	}

	@Override
	public double getValue(S state, A action) {
		double factor = state.isFirstPlayerToMove() ? 1 : -1;
		return factor * actionValueFunction.getValue(state, action);
	}
}
