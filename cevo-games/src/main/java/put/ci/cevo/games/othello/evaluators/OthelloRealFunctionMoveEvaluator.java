package put.ci.cevo.games.othello.evaluators;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;
import put.ci.cevo.rl.agent.functions.RealFunction;

import com.carrotsearch.hppc.IntArrayList;

public class OthelloRealFunctionMoveEvaluator implements BoardMoveEvaluator<OthelloBoard> {

	private final RealFunction function;

	public OthelloRealFunctionMoveEvaluator(RealFunction function) {
		this.function = function;
	}

	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		IntArrayList positions = board.simulateMove(move, player);
		if (positions == null) {
			return BoardMoveEvaluator.INVALID_MOVE;
		}

		board.makeMove(positions, player);

		double result = function.getValue(BoardUtils.getValues(board));

		board.makeMove(positions, OthelloBoard.opponent(player));
		board.setValue(move, Board.EMPTY);

		return result;
	}
}
