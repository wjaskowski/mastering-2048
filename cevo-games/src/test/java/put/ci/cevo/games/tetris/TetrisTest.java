package put.ci.cevo.games.tetris;

import com.carrotsearch.hppc.IntArrayList;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.util.RandomUtils;

import java.util.List;

public class TetrisTest {

	@Test
	public void playSZTetris() {
		Tetris tetris = Tetris.newSZTetris();
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		TetrisState state = tetris.sampleInitialStateDistribution(random);

		Assert.assertTrue(state.getTetromino() == Tetromino.S || state.getTetromino() == Tetromino.Z);

		IntArrayList heights = new IntArrayList();

		int[] expectedHeights = { 3, 3, 5, 6, 6, 8, 8, 8, 8, 8, 10, 10, 11, 13, 13, 13, 15, 17, 19, 19 };

		while (!tetris.isTerminal(state)) {
			List<TetrisAction> actions = tetris.getPossibleActions(state);
			Assert.assertEquals(17, actions.size());

			TetrisAction action = RandomUtils.pickRandom(actions, random);
			//System.out.println(action);
			AgentTransition<TetrisState, TetrisAction> agentTransition = tetris.getAgentTransition(state, action
			);
			state = tetris.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
			//System.out.println(state);
			//System.out.println(state.getBoard().getMaxColumnHeight());
			heights.add(state.getBoard().getMaxColumnHeight());
		}
		Assert.assertArrayEquals(expectedHeights, heights.toArray());
	}
}