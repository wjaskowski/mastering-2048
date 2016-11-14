package put.ci.cevo.games.board;

import org.apache.commons.lang.NotImplementedException;
import put.ci.cevo.games.InvertibleTwoPlayerBoardGameState;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

/**
 * A wrapper around StateValueFunction for two player board games. It inverts the board for the second player before
 * any operation
 */
public class BoardInversionStateValueFunction<S extends InvertibleTwoPlayerBoardGameState>
		implements LearnableStateValueFunction<S> {

	private final LearnableStateValueFunction<S> stateValueFunction;

	public BoardInversionStateValueFunction(LearnableStateValueFunction<S> stateValueFunction) {
		this.stateValueFunction = stateValueFunction;
	}

	@Override
	public double getValue(S state) {
		BoardInversion<S> inversion = new BoardInversion<>();

		inversion.invert(state);
		double result = stateValueFunction.getValue(state);
		inversion.uninvert(state);
		return result;
	}

	@Override
	public void increase(S state, double delta) {
		BoardInversion<S> inversion = new BoardInversion<>();

		inversion.invert(state);
		stateValueFunction.increase(state, delta);
		inversion.uninvert(state);
	}

	@Override
	public int getActiveFeaturesCount() {
		throw new NotImplementedException();
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

