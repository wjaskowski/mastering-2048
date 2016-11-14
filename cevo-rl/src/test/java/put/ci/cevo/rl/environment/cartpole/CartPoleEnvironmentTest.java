package put.ci.cevo.rl.environment.cartpole;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.rl.environment.ContinuousAction;
import put.ci.cevo.rl.environment.AgentTransition;

public class CartPoleEnvironmentTest {

	private CartPoleEnvironment environment;
	private RandomDataGenerator random;

	@Before
	public void setUp() {
		random = new RandomDataGenerator();
		environment = new CartPoleEnvironment(2);
	}

	@Test
	public void testSampleInitialStateDistribution() throws Exception {
		CartPoleState state = environment.sampleInitialStateDistribution(random);
		Assert.assertEquals(state.getCartPosition(), 0, 0.001);
		Assert.assertEquals(state.getCartVelocity(), 0, 0.001);
		Assert.assertEquals(state.getPoleVelocity(0), 0, 0.001);
		Assert.assertEquals(state.getPoleAngle(0), 0.07, 0.001);
		Assert.assertEquals(state.getPoleVelocity(1), 0, 0.001);
		Assert.assertEquals(state.getPoleAngle(1), 0, 0.001);

		Assert.assertArrayEquals(new double[] { 0, 0, 0.07, 0, 0, 0 }, state.getFeatures(), 0.001);
	}

	@Test
	public void testComputeTransition() throws Exception {
		CartPoleState state = environment.sampleInitialStateDistribution(random);
		AgentTransition<CartPoleState, ContinuousAction> agentTransition = environment.getAgentTransition(state,
				new ContinuousAction(0.1));
		CartPoleState afterState = agentTransition.getAfterState();

		double[] expectedAfterState = new double[] { 1.8460712833571393E-4, 0.01846151705355583, 0.06992936472236927,
			-0.007068204747243641, -0.0027815818040247595, -0.2794759845255993 };
		Assert.assertArrayEquals(expectedAfterState, afterState.getFeatures(), 10e-6);
	}

	@Test
	public void testMotionEquations() throws Exception {
		CartPoleState state = environment.sampleInitialStateDistribution(random);
		double[] dy = new double[6];
		environment.motionEquations(1.0, state.getFeatures(), dy);
		Assert.assertArrayEquals(new double[] { 0.0, 0.9229968444719383, 0.0, -0.35294478228867243, 0.0,
			-13.844952667079076 }, dy, 10e-9);
	}

	@Test
	public void testRungeKutta4() throws Exception {
		double[] dy = new double[6];
		double[] expected = new double[] { 4.6150293059333864E-5, 0.009230155470065143, 0.06998234991908409,
			-0.003530594904452524, -6.929639567933079E-4, -0.13874989599620138 };

		CartPoleState state = environment.sampleInitialStateDistribution(random);
		environment.motionEquations(1.0, state.getFeatures(), dy);

		double[] variables = state.getFeatures();
		environment.RungeKutta4(1.0, variables, dy, variables);

		Assert.assertArrayEquals(expected, variables, 10e-9);
	}

}
