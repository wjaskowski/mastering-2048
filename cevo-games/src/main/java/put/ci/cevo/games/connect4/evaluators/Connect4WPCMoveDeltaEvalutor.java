package put.ci.cevo.games.connect4.evaluators;

import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.player.BoardMoveEvaluator;
import put.ci.cevo.rl.agent.functions.wpc.MarginWPC;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class Connect4WPCMoveDeltaEvalutor implements BoardMoveEvaluator<Connect4Board> {

	private final MarginWPC wpc;

	public Connect4WPCMoveDeltaEvalutor(WPC wpc) {
		this.wpc = createConnect4MarginWPC(wpc);
	}

	@Override
	public double evaluateMove(Connect4Board board, final int move, final int player) {
		return wpc.buffer[board.moveToPos(move)] * Connect4Board.playerValue(player);
	}

	private static MarginWPC createConnect4MarginWPC(WPC wpc) {
		return new MarginWPC(wpc, Connect4Board.BOARD_HEIGHT, Connect4Board.BOARD_WIDTH, BoardUtils.MARGIN_WIDTH);
	}
}
