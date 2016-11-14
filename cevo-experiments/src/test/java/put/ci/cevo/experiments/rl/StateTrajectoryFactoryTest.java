package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.othello.mdp.OthelloSelfPlayEnvironment;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.rl.environment.StateTrajectory;

public class StateTrajectoryFactoryTest {

	@Test
	public void testCreateRandomIndividual() throws Exception {
		RandomDataGenerator random = new RandomDataGenerator();
		StateTrajectoryFactory<OthelloState> factory = new StateTrajectoryFactory<>(new OthelloSelfPlayEnvironment());

		for (int i = 0; i < 10; i++) {
			StateTrajectory<OthelloState> trajectory = factory.createRandomIndividual(random);
			Assert.assertTrue(trajectory.getDepth() >= 1);
			Assert.assertTrue(trajectory.getDepth() <= 100);
		}
	}
}
