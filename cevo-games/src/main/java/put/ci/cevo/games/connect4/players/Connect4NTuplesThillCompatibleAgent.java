package put.ci.cevo.games.connect4.players;

import static put.ci.cevo.games.board.BoardEvaluationType.BOARD_INVERSION;
import static put.ci.cevo.games.board.BoardEvaluationType.STRAIGHT;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.evaluators.Connect4DoubleNTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.connect4.evaluators.Connect4NTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.connect4.players.adapters.Connect4PlayerAgentAdapter;
import put.ci.cevo.games.connect4.thill.c4.NotImplementedFakeAgent;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.player.MoveEvaluatorPlayer;

/**
 * This agent is compatible with Thill's code and allows interplay between our code (NTuples, Connect4Board,
 * MoveEvaluatorPlayer) and his efficient Connect4 implementation.
 */
public class Connect4NTuplesThillCompatibleAgent extends NotImplementedFakeAgent {

	private final Connect4PlayerAgentAdapter agent;

	public Connect4NTuplesThillCompatibleAgent(NTuples ntuples) {
		this.agent = new Connect4PlayerAgentAdapter(new MoveEvaluatorPlayer<>(
			new Connect4NTuplesMoveDeltaEvaluator(ntuples), BOARD_INVERSION));
	}

	public Connect4NTuplesThillCompatibleAgent(DoubleNTuples ntuples) {
		this.agent = new Connect4PlayerAgentAdapter(new MoveEvaluatorPlayer<>(
			new Connect4DoubleNTuplesMoveDeltaEvaluator(ntuples), STRAIGHT));
	}

	@Override
	public int getBestMove(int[][] table, RandomDataGenerator random) {
		return agent.getBestMove(table, random);
	}

}
