package put.ci.cevo.games.othello.evaluators;

import java.util.HashMap;
import java.util.Map;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

/**
 * It computes not the state value, but just a difference between current board and the board after the move.
 */
public class OthelloDoubleNTuplesMoveDeltaEvaluator implements BoardMoveEvaluator<OthelloBoard> {

	private final Map<Integer, OthelloNTuplesMoveDeltaEvaluator> evalators;

	public OthelloDoubleNTuplesMoveDeltaEvaluator(DoubleNTuples ntuples) {
		this.evalators = new HashMap<>();
		this.evalators.put(Board.BLACK, new OthelloNTuplesMoveDeltaEvaluator(ntuples.first()));
		this.evalators.put(Board.WHITE, new OthelloNTuplesMoveDeltaEvaluator(ntuples.second()));
	}

	/**
	 * Returns function evaluation value for a given board (its delta - only for the things that changed
	 */
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		return evalators.get(player).evaluateMove(board, move, player);
	}
}
