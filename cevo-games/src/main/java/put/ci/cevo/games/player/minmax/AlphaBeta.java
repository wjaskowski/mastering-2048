package put.ci.cevo.games.player.minmax;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static put.ci.cevo.games.board.Board.BLACK;
import static put.ci.cevo.games.board.Board.WHITE;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements simple minimax algorithm with alpha-beta pruning.
 */
public class AlphaBeta {

	private final GameStateEvaluator evaluator;

	public AlphaBeta(GameStateEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public NodeState minimax(GameNode node, int depth, int player) {
		return minimax(node, depth, player, NEGATIVE_INFINITY, POSITIVE_INFINITY);
	}

	/** Alpha and beta should be initialized to -inf and +inf respectively. */
	public NodeState minimax(GameNode node, int depth, int player, double alpha, double beta) {
		int[] validMoves = node.getValidMoves();
		if (validMoves.length == 0 || depth == 0) {
			return new NodeState(evaluator.evaluate(node.getState(player)));
		}
		int bestMove = -1;
		for (int move : validMoves) {
			node.makeMove(move, player);
			if (player == BLACK) {
				double score = minimax(node, depth - 1, WHITE, alpha, beta).getScore();
				if (score > alpha) {
					alpha = score;
					bestMove = move;
				}
			} else {
				double score = minimax(node, depth - 1, BLACK, alpha, beta).getScore();
				if (score < beta) {
					beta = score;
					bestMove = move;
				}
			}
			node.undoMove(move);
			// cut-off
			if (alpha >= beta) {
				break;
			}
		}
		return new NodeState(bestMove, player == BLACK ? alpha : beta);
	}

	public NodeState minimaxMoves(GameNode node, int depth, int player) {
		return minimaxMoves(node, depth, player, NEGATIVE_INFINITY, POSITIVE_INFINITY);
	}

	/** If there are several optimal moves, return them all */
	public NodeState minimaxMoves(GameNode node, int depth, int player, double alpha, double beta) {
		int[] validMoves = node.getValidMoves();
		if (validMoves.length == 0 || depth == 0) {
			return new NodeState(evaluator.evaluate(node.getState(player)));
		}
		List<Integer> bestMoves = new ArrayList<>(7);
		for (int move : validMoves) {
			node.makeMove(move, player);
			if (player == BLACK) {
				double score = minimaxMoves(node, depth - 1, WHITE, alpha, beta).getScore();
				if (score == alpha) {
					bestMoves.add(move);
				}
				if (score > alpha) {
					bestMoves.clear();
					alpha = score;
					bestMoves.add(move);
				}
			} else {
				double score = minimaxMoves(node, depth - 1, BLACK, alpha, beta).getScore();
				if (score == beta) {
					bestMoves.add(move);
				}
				if (score < beta) {
					bestMoves.clear();
					beta = score;
					bestMoves.add(move);
				}
			}
			node.undoMove(move);
			// cut-off
			if (alpha >= beta) {
				break;
			}
		}
		return new NodeState(bestMoves, player == BLACK ? alpha : beta);
	}

}
