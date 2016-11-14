package put.ci.cevo.games.othello.evaluators;

import static put.ci.cevo.games.othello.OthelloBoard.opponent;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.player.BoardMoveEvaluator;
import put.ci.cevo.rl.agent.functions.wpc.MarginWPC;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class OthelloWPCMoveDeltaEvaluator implements BoardMoveEvaluator<OthelloBoard> {

	public final static int NUM_WEIGHTS = OthelloBoard.SIZE.area();

	private final MarginWPC wpc;

	/**
	 * Creates a new OthelloWPCPlayer.
	 */
	public OthelloWPCMoveDeltaEvaluator(WPC wpc) {
		this.wpc = OthelloWPCPlayer.createMarginWPC(wpc);
	}

	/**
	 * Return the utility of a move for player number for the given board.
	 */
	@Override
	public double evaluateMove(OthelloBoard board, final int move, final int player) {
		assert board.isEmpty(move);

		double eval = 0;
		boolean moveIsValid = false;
		for (int dir : OthelloBoard.DIRS) {
			int pos = move + dir;
			if (board.buffer[pos] != opponent(player)) {
				// Neighbor is not opponent, so this is a wrong direction
				continue;
			}

			// Move along the line while there are opponent pieces
			double tmpEval = 0;
			do {
				tmpEval += 2 * wpc.buffer[pos]; // delta evaluator, so tmpEval is a delta; that is why 2 (+1-(-1))
				pos += dir;
			} while (board.buffer[pos] == opponent(player));

			// If the line ends with my piece, then we have a valid move - update the move board evaluation
			if (board.buffer[pos] == player) {
				eval += tmpEval;
				moveIsValid = true;
			}
		}

		if (!moveIsValid) {
			return BoardMoveEvaluator.INVALID_MOVE;
		}
		eval += wpc.buffer[move]; // Here we assume that empty piece is worth 0
		return eval * OthelloBoard.playerValue(player); // So that higher is better independent on the player color
	}
}
