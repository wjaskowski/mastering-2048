package put.ci.cevo.games.connect4.players;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.connect4.evaluators.Connect4DoubleNTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.connect4.evaluators.Connect4NTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;

import static put.ci.cevo.games.board.BoardEvaluationType.BOARD_INVERSION;

public class Connect4NTuplesPlayer implements Connect4Player {

	private final MoveEvaluatorPlayer<Connect4Board> evaluatorPlayer;

	public Connect4NTuplesPlayer(NTuples ntuples) {
		this.evaluatorPlayer = new MoveEvaluatorPlayer<>(new Connect4NTuplesMoveDeltaEvaluator(ntuples),
				BOARD_INVERSION);
	}

	public Connect4NTuplesPlayer(DoubleNTuples ntuples) {
		this.evaluatorPlayer = new MoveEvaluatorPlayer<>(new Connect4DoubleNTuplesMoveDeltaEvaluator(ntuples),
				BoardEvaluationType.STRAIGHT);
	}

	@Override
	public int getMove(Connect4Board board, int player, int[] possibleMoves, RandomDataGenerator random) {
		return evaluatorPlayer.getMove(board, player, possibleMoves, random);
	}
}
