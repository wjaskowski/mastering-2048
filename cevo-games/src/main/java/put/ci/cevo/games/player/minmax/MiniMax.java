package put.ci.cevo.games.player.minmax;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static put.ci.cevo.games.board.Board.BLACK;
import static put.ci.cevo.games.board.Board.WHITE;

/**
 * Implements naive minimax algorithm without alpha beta pruning.
 */
public class MiniMax {

	private final GameStateEvaluator evaluator;

	public MiniMax(GameStateEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public NodeState minimax(GameNode node, int depth, int player) {
		int[] validMoves = node.getValidMoves();
		if (validMoves.length == 0 || depth == 0) {
			return new NodeState(evaluator.evaluate(node.getState(player)));
		}
		double bestScore = (player == BLACK) ? NEGATIVE_INFINITY : POSITIVE_INFINITY;
		int bestMove = -1;
		for (int move : validMoves) {
			node.makeMove(move, player);
			if (player == BLACK) {
				double currentScore = minimax(node, depth - 1, WHITE).getScore();
				if (currentScore > bestScore) {
					bestScore = currentScore;
					bestMove = move;
				}
			} else {
				double currentScore = minimax(node, depth - 1, BLACK).getScore();
				if (currentScore < bestScore) {
					bestScore = currentScore;
					bestMove = move;
				}
			}
			node.undoMove(move);
		}
		return new NodeState(bestMove, bestScore);
	}

}
