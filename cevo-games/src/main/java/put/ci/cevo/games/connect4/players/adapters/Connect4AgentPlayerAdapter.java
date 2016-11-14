package put.ci.cevo.games.connect4.players.adapters;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.Connect4;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.thill.c4.Agent;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;

/**
 * Adapts Thill's {@link Agent} interface to the {@link Connect4Player}.
 * This allows to use among others, {@link AlphaBetaAgent} in a lightweight {@link Connect4} game implementation.
 */
public class Connect4AgentPlayerAdapter implements Connect4Player {

	private final Agent agent;

	public Connect4AgentPlayerAdapter(Agent agent) {
		this.agent = agent;
	}

	@Override
	public int getMove(Connect4Board board, int player, int[] validMoves, RandomDataGenerator random) {
		return agent.getBestMove(board.toThillBoard(), random);
	}

}
