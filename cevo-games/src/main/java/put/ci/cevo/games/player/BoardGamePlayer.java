package put.ci.cevo.games.player;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.board.Board;

public interface BoardGamePlayer<T extends Board> {

	/**
	 * The board is unchanged when returns (but might be written temporarily).
	 */
	// I think that validMoves should be moved to the Board interface.
	public int getMove(T board, int player, int[] validMoves, RandomDataGenerator random);
}
