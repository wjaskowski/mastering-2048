package put.ci.cevo.games.connect4.evaluators;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.eval.BoardNTupleEvaluator;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Connect4NTuplesMoveEvaluator implements BoardMoveEvaluator<Connect4Board> {

	private final NTuples ntuples;
	private final BoardNTupleEvaluator evaluator = new BoardNTupleEvaluator();

	public Connect4NTuplesMoveEvaluator(NTuples ntuples) {
		this.ntuples = ntuples;
	}

	/**
	 * Returns function evaluation value for a given board
	 */
	@Override
	public double evaluateMove(Connect4Board board, int move, int player) {
		// Is it valid move?
		if (!board.isValidMove(move)) {
			return BoardMoveEvaluator.INVALID_MOVE;
		}
		// Make move...
		board.makeMove(move, player);
		double result = evaluator.evaluate(ntuples, board);
		// ... and revert it
		board.setValue(move, Board.EMPTY);
		return result;
	}
}
