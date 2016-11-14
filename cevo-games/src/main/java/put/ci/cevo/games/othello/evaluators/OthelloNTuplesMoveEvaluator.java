package put.ci.cevo.games.othello.evaluators;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.eval.BoardNTupleEvaluator;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import com.carrotsearch.hppc.IntArrayList;

public class OthelloNTuplesMoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {

	private final NTuples ntuples;
	private final BoardNTupleEvaluator evaluator = new BoardNTupleEvaluator();

	public OthelloNTuplesMoveEvaluator(NTuples ntuples) {
		this.ntuples = ntuples;
	}

	/**
	 * Returns function evaluation value for a given board
	 */
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		// Is it valid move?
		IntArrayList positions = board.simulateMove(move, player);
		if (positions == null) {
			return BoardMoveEvaluator.INVALID_MOVE;
		}

		// Make move...
		board.makeMove(positions, player);

		double result = evaluator.evaluate(ntuples, board);

		// ... and revert it
		board.makeMove(positions, OthelloBoard.opponent(player));
		board.setValue(move, Board.EMPTY);

		return result;
	}
}
