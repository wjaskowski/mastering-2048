package put.ci.cevo.games.board;

import put.ci.cevo.games.InvertibleTwoPlayerBoardGameState;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

/**
 * A wrapper around ActionEvaluator for two player board games. It inverts the board for the second player before
 * evaluation
 */
public class BoardInversionQFunction<S extends InvertibleTwoPlayerBoardGameState, A extends Action>
		implements ActionValueFunction<S, A> {

	private final ActionValueFunction<S, A> actionValueFunction;

	public BoardInversionQFunction(ActionValueFunction<S, A> actionValueFunction) {
		this.actionValueFunction = actionValueFunction;
	}

	@Override
	public double getValue(S state, A action) {
		BoardInversion<S> inversion = new BoardInversion<>();

		inversion.invert(state);
		double result = actionValueFunction.getValue(state, action);
		inversion.uninvert(state);

		return result;
	}
}
