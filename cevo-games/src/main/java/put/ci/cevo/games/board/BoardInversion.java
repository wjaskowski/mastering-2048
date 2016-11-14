package put.ci.cevo.games.board;

import com.google.common.base.Preconditions;
import put.ci.cevo.games.InvertibleTwoPlayerBoardGameState;

/**
 * A utility class facilitating board inversion
 */
public class BoardInversion<S extends InvertibleTwoPlayerBoardGameState> {
	private boolean actuallyInverted = false;
	private boolean invertedState = false;

	public void invert(S state) {
		Preconditions.checkArgument(!invertedState);
		invertedState = true;

		actuallyInverted = !state.isFirstPlayerToMove();
		if (actuallyInverted) {
			state.invert();
		}
	}

	public void uninvert(S state) {
		Preconditions.checkArgument(invertedState);
		invertedState = false;

		if (actuallyInverted) {
			state.invert();
		}
	}
}
