package put.ci.cevo.games.player;

import put.ci.cevo.games.board.Board;

@Deprecated

/**
 * @deprecated Use: QFunction<T extends BoardState>
 */
public interface BoardMoveEvaluator<T extends Board> {

	public final static double INVALID_MOVE = Double.NEGATIVE_INFINITY;

	/**
	 * Should return <code>INVALID_MOVE</code> if the move is invalid
	 * @return evaluation value (higher means a better move)
	 */
	// TODO: Player should be probably an enum or class. This could prevent serious errors such as using it value in a
	// not appropriate way
	// TODO: evaluteMove(BoardGameState<T>, BoardGameMove)
	public double evaluateMove(T board, int move, int player);
}
