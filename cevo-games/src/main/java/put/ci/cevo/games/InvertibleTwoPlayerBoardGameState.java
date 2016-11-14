package put.ci.cevo.games;

import put.ci.cevo.games.board.BoardState;

/**
 * Required by board inversion technique
 */
public interface InvertibleTwoPlayerBoardGameState extends TwoPlayerGameState, BoardState {
	/**
	 * Inverts the board (looking from the second player perspective) and changes the player to move. It is assumed that
	 * always state.invert().invert().equals(state)
	 */
	void invert();
}
