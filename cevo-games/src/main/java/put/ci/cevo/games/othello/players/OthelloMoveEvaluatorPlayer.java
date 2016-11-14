package put.ci.cevo.games.othello.players;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;

public class OthelloMoveEvaluatorPlayer implements OthelloPlayer {

	private final MoveEvaluatorPlayer<OthelloBoard> moveEvaluatorPlayer;

	public OthelloMoveEvaluatorPlayer(BoardMoveEvaluator<OthelloBoard> evaluator,
			BoardEvaluationType boardEvaluationType) {
		this(new MoveEvaluatorPlayer<>(evaluator, boardEvaluationType));
	}

	public OthelloMoveEvaluatorPlayer(MoveEvaluatorPlayer<OthelloBoard> player) {
		this.moveEvaluatorPlayer = player;
	}

	@Override
	public int getMove(OthelloBoard board, int player, int[] validMoves, RandomDataGenerator random) {
		return moveEvaluatorPlayer.getMove(board, player, validMoves, random);
	}
}
