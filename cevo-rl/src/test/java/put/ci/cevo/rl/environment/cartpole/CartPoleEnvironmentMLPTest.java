package put.ci.cevo.rl.environment.cartpole;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.rl.agent.ContinuousActionAgent;
import put.ci.cevo.rl.agent.functions.mlp.FeedForwardNetwork;
import put.ci.cevo.rl.agent.functions.mlp.Layer;
import put.ci.cevo.rl.agent.functions.mlp.MLP;
import put.ci.cevo.rl.agent.functions.mlp.Neuron;
import put.ci.cevo.rl.environment.ContinuousAction;
import put.ci.cevo.rl.environment.AgentTransition;

public class CartPoleEnvironmentMLPTest {

	private CartPoleEnvironment environment;
	private RandomDataGenerator random;

	@Before
	public void setUp() {
		environment = new CartPoleEnvironment(1);
		random = null;
	}

	@Test
	public void testMLP() {
		Neuron[] hidden = new Neuron[] { new Neuron(new double[] { 0.0, -3.1159412652729372, -3.861000981960273,
				-3.329831488893046, -1.2150874106605016 }) };
		Neuron[] output = new Neuron[] { new Neuron(new double[] { 0.0, -0.9955837388934388 }) };
		MLP mlp = new MLP(new Layer[] { new Layer(hidden), new Layer(output) });

		ContinuousActionAgent<CartPoleState> agent = new ContinuousActionAgent<>(mlp, environment);
		testAgent(agent);
	}

	@Test
	public void testFeedForwardNetwork() {
		double[][] weights = new double[][] { { -3.1159412652729372, -3.861000981960273, -3.329831488893046,
				-1.2150874106605016, -0.9955837388934388 } };
		FeedForwardNetwork network = new FeedForwardNetwork(weights);
		ContinuousActionAgent<CartPoleState> agent = new ContinuousActionAgent<>(network, environment);
		testAgent(agent);
	}
	
	public void testAgent(ContinuousActionAgent<CartPoleState> agent) {
		CartPoleState state = environment.sampleInitialStateDistribution(null);
		double[] encode = environment.encode(state);
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.3342271522318735, 0.0 }, encode, 10e-6);

		ContinuousAction action = agent.chooseAction(state, environment.getPossibleActions(state), random).getAction();
		Assert.assertEquals(0.6648949825171504, action.getValue(), 10e-6);

		AgentTransition<CartPoleState, ContinuousAction> agentTransition = environment.getAgentTransition(state,
				action);
		CartPoleState state2 = agentTransition.getAfterState();

		double[] encode2 = environment.encode(state2);
		Assert.assertArrayEquals(new double[] { 5.36223712965364E-4, 0.05362436033071162, 0.3260102991879332,
				-0.8221391699365818 }, encode2, 10e-6);

		ContinuousAction action2 = agent.chooseAction(state2, environment.getPossibleActions(state), random).getAction();
		Assert.assertEquals(0.2782099623850646, action2.getValue(), 10e-6);

		AgentTransition<CartPoleState, ContinuousAction> agentTransition2 = environment.getAgentTransition(state2,
				action2);
		double[] encode3 = environment.encode(agentTransition2.getAfterState());
		Assert.assertArrayEquals(new double[] { 0.0018308414559250357, 0.07584164804488404, 0.3066981899014446,
				-1.1101055231164358 }, encode3, 10e-6);
	}
}
