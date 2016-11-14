package put.ci.cevo.experiments.connect4;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.players.adapters.Connect4PlayerAgentAdapter;
import put.ci.cevo.util.RandomUtils;

public class Connect4RegressionTest {

	private static class DeterministicPlayer implements Connect4Player {

		private final RandomDataGenerator random;

		private DeterministicPlayer(int seed) {
			random = new RandomDataGenerator(new MersenneTwister(seed));
		}

		@Override
		public int getMove(Connect4Board board, int player, int[] validMoves, RandomDataGenerator random) {
			return RandomUtils.pickRandom(validMoves, this.random);
		}
	}

	private static final int NUM_TEST_GAMES = 50000;

	/**
	 * Asserts that {@link Connect4Interaction} and {@link Connect4AgentInteraction} which encapsulates Thill's
	 * Connect4 framework work exactly the same.
	 */
	@Test
	public void testDeterministicPlayers() {
		Connect4Interaction players = new Connect4Interaction();
		Connect4AgentInteraction agents = new Connect4AgentInteraction();

		for (int seed = 0; seed < NUM_TEST_GAMES; seed++) {
			InteractionResult r1 = players.interact(new DeterministicPlayer(seed), new DeterministicPlayer(seed + 1),
					null);
			InteractionResult r2 = agents.interact(
					new Connect4PlayerAgentAdapter(new DeterministicPlayer(seed)),
					new Connect4PlayerAgentAdapter(new DeterministicPlayer(seed + 1)), null);

			assertTrue(r1.firstResult() == r2.firstResult());
			assertTrue(r1.secondResult() == r2.secondResult());
		}
	}
}