package put.ci.cevo.games.othello.players;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.evaluators.OthelloNTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;

/**
 * A shortcut to <code>OthelloMoveEvaluatorPlayer(new OthelloNTuplesMoveEvaluator)</code>
 */
public class OthelloNTuplesPlayer implements OthelloPlayer {

	private final MoveEvaluatorPlayer<OthelloBoard> evaluatorPlayer;

	public OthelloNTuplesPlayer(NTuples ntuples) {
		this(ntuples, BoardEvaluationType.OUTPUT_NEGATION);
	}

	public OthelloNTuplesPlayer(NTuples ntuples, BoardEvaluationType boardEvaluationType) {
		evaluatorPlayer = new MoveEvaluatorPlayer<>(
			new OthelloNTuplesMoveDeltaEvaluator(ntuples), boardEvaluationType);
	}

	@Override
	public int getMove(OthelloBoard board, int player, int[] possibleMoves, RandomDataGenerator random) {
		return evaluatorPlayer.getMove(board, player, possibleMoves, random);
	}
}
