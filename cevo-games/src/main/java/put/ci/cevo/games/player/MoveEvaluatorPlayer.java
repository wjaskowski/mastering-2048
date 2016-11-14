package put.ci.cevo.games.player;

import static put.ci.cevo.games.board.BoardEvaluationType.BOARD_INVERSION;
import static put.ci.cevo.games.board.BoardEvaluationType.OUTPUT_NEGATION;
import static put.ci.cevo.games.othello.OthelloBoard.opponent;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.util.ArrayUtils;
import put.ci.cevo.util.RandomUtils;

/**
 * A player that evaluates each move and selects the one with the highest score or a random one in case of equal scores
 */
public final class MoveEvaluatorPlayer<T extends Board> implements BoardGamePlayer<T> {

	private static final double EPSILON = 0.000001;

	private final BoardMoveEvaluator<T> moveEvaluator;
	private final BoardEvaluationType boardEvaluationType;

	/* TODO/CONSIDER: BoardEvaluationType should in principle be in moveEvaluator, since e.g. OthelloDoubleNTupleMoveEvaluator
	does make  sense with OUTPUT_NEGATION or BOARD_INVERSION. Also a OthelloNTupleMoveEvaluator does not make sense
	with STRAIGHT. However, doing it nicely would mean I have to repeat the code responsible for BoardInversion or
	Negation for all implementations of BoardMoveEvaluation (which also is not nice). So currently I leave it alone. */
	public MoveEvaluatorPlayer(BoardMoveEvaluator<T> moveEvaluator, BoardEvaluationType boardEvaluationType) {
		this.moveEvaluator = moveEvaluator;
		this.boardEvaluationType = boardEvaluationType;
	}

	@Override
	public int getMove(T board, int player, int[] validMoves, RandomDataGenerator random) {
		Preconditions.checkArgument(validMoves.length > 0);
		final boolean invert = (boardEvaluationType.equals(BOARD_INVERSION) && player == Board.WHITE);
		if (invert) {
			board.invert();
			player = opponent(player);
		}

		double bestEval = Double.NEGATIVE_INFINITY;
		final IntArrayList bestMoves = new IntArrayList();

		// Sorting moves is not required, but I needed it for comparison with old implementation
		int[] sortedPossibleMoves = ArrayUtils.sorted(validMoves);
		for (int move : sortedPossibleMoves) {
			double eval = moveEvaluator.evaluateMove(board, move, player);
			if (boardEvaluationType.equals(OUTPUT_NEGATION) && player == Board.WHITE) {
				eval *= -1; // So that we will take the smallest instead of the biggest
			}

			// EPSILON is harmless, but it prevents "nondeterminism" from numerical errors (gives an opportunity to
			// compare two implementations in a robust way)
			if (eval == bestEval || Math.abs(eval - bestEval) < EPSILON) {
				bestMoves.add(move);
			} else if (bestEval < eval) {
				bestEval = eval;
				bestMoves.clear();
				bestMoves.add(move);
			} else if (bestEval == Double.NEGATIVE_INFINITY) {
				throw new RuntimeException("moveEvaluator has eturned " + eval + " which I really cannot accept here");
			}
		}

		if (invert) {
			board.invert();
		}

		return RandomUtils.pickRandom(bestMoves.toArray(), random);
	}
}
