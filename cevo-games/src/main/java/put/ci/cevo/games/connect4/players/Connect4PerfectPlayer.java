package put.ci.cevo.games.connect4.players;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.connect4.players.adapters.Connect4AgentPlayerAdapter;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import static put.ci.cevo.games.connect4.players.Connect4PerfectPlayer.Randomization.RANDOMIZE_LOSSES_AND_MOVES;

/**
 * Adapts Thill's perfect player {@link AlphaBetaAgent} to {@link Connect4Player} interface.
 *
 * This player is *not* thread-safe!
 */
public class Connect4PerfectPlayer implements Connect4Player {

	public enum Randomization {
		RANDOMIZE_LOSSES,
		RANDOMIZE_EQUAL_MOVES,
		RANDOMIZE_LOSSES_AND_MOVES;
	}

	private final Connect4AgentPlayerAdapter player;

	@AccessedViaReflection
	public Connect4PerfectPlayer() {
		this(RANDOMIZE_LOSSES_AND_MOVES);
	}

	@AccessedViaReflection
	public Connect4PerfectPlayer(Randomization randomization) {
		this.player = new Connect4AgentPlayerAdapter(AlphaBetaAgent.createAgent(randomization));
	}

	/** Not thread-safe! */
	@Override
	public int getMove(Connect4Board board, int player, int[] validMoves, RandomDataGenerator random) {
		return this.player.getMove(board, player, validMoves, random);
	}
}
