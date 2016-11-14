package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.experiments.mlp.FeedForwardNetworkIndividualFactory;
import put.ci.cevo.experiments.mlp.MLPIndividualFactory;
import put.ci.cevo.experiments.runs.cartpole.CartPoleEnvironmentFactory;
import put.ci.cevo.experiments.runs.cartpole.CartPoleEnvironmentRealFunctionInteraction;
import put.ci.cevo.experiments.runs.cartpole.RealFunctionCartPoleEnvironmentInteraction;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.agent.functions.mlp.FeedForwardNetwork;
import put.ci.cevo.rl.agent.functions.mlp.Layer;
import put.ci.cevo.rl.agent.functions.mlp.MLP;
import put.ci.cevo.rl.agent.functions.mlp.Neuron;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;

public class CartPoleEnvironmentRegressionTest {

	private CartPoleEnvironment environment;
	private ThreadedContext context;

	@Before
	public void setUp() {
		environment = new CartPoleEnvironment(1);
		context = new ThreadedContext(123);
	}

	@Test
	public void testPerformance() {
		Neuron[] hidden = new Neuron[] { new Neuron(new double[] { 0.0,
				-3.1159412652729372, -3.861000981960273, -3.329831488893046,
				-1.2150874106605016 }) };
		Neuron[] output = new Neuron[] { new Neuron(new double[] { 0.0,
				-0.9955837388934388 }) };
		MLP mlp = new MLP(new Layer[] { new Layer(hidden), new Layer(output) });

		RealFunctionCartPoleEnvironmentInteraction<MLP> interaction = new RealFunctionCartPoleEnvironmentInteraction<>(
				100000, true, false);
		InteractionResult result = interaction.interact(mlp, environment,
				context.getRandomForThread());
		Assert.assertEquals(100000, result.firstResult(), 10e-6);

		double numSteps = environment.evaluate(mlp, 100000,
				context.getRandomForThread());
		Assert.assertEquals(100000, numSteps, 10e-6);
	}

	@Test
	public void testPerformance2() {
		Neuron[] hidden = new Neuron[] { new Neuron(new double[] {
				-1.2874238249910803, 5.462516408396738, 5.442066148881063,
				5.092398879249343, 0.571797638182237 }) };
		Neuron[] output = new Neuron[] { new Neuron(new double[] { 0.0,
				1.5090054310942058 }) };
		MLP mlp = new MLP(new Layer[] { new Layer(hidden), new Layer(output) });

		RealFunctionCartPoleEnvironmentInteraction<MLP> interaction = new RealFunctionCartPoleEnvironmentInteraction<>(
				100000, true, false);
		InteractionResult result = interaction.interact(mlp, environment,
				context.getRandomForThread());
		Assert.assertEquals(100000, result.firstResult(), 10e-6);

		double numSteps = environment.evaluate(mlp, 100000,
				context.getRandomForThread());
		Assert.assertEquals(100000, numSteps, 10e-6);
	}

	// @Test
	public void testMLPAveragePerformance() {
		int maxNumSteps = 100;
		MLPIndividualFactory factory = new MLPIndividualFactory(4, 10, 1, -6, 6);
		RealFunctionCartPoleEnvironmentInteraction<MLP> interaction = new RealFunctionCartPoleEnvironmentInteraction<>(
				maxNumSteps, true, false);

		int cnt = 0;
		SummaryStatistics statistics = new SummaryStatistics();
		for (int i = 0; i < 10000; i++) {
			MLP mlp = factory.createRandomIndividual(context
					.getRandomForThread());
			InteractionResult result = interaction.interact(mlp, environment,
					context.getRandomForThread());
			statistics.addValue(result.firstResult());
			if (result.firstResult() == maxNumSteps) {
				cnt++;
			}
		}

		System.out.println("MLP average performance = " + statistics.getMean());
		System.out.println("Num solved tests by MLP = " + cnt);
	}

	@Test
	public void testFFNAveragePerformance() {
		int maxNumSteps = 1000;
		FeedForwardNetworkIndividualFactory factory = new FeedForwardNetworkIndividualFactory(
				4, 10, 1, -6, 6);
		RealFunctionCartPoleEnvironmentInteraction<FeedForwardNetwork> interaction = new RealFunctionCartPoleEnvironmentInteraction<>(
				maxNumSteps, true, false);

		int cnt = 0;
		SummaryStatistics statistics = new SummaryStatistics();
		for (int i = 0; i < 1000; i++) {
			FeedForwardNetwork net = factory.createRandomIndividual(context
					.getRandomForThread());
			InteractionResult result = interaction.interact(net, environment,
					context.getRandomForThread());
			statistics.addValue(result.firstResult());
			if (result.firstResult() == maxNumSteps) {
				cnt++;
			}
		}

		Assert.assertEquals("FFN average performance = ", 71.348, statistics.getMean(), 10e-6);
		Assert.assertEquals("Num solved tests by MLP", 21, cnt);
	}

	@Test
	public void testWPCAveragePerformance() {
		int maxNumSteps = 100;
		WPCIndividualFactory factory = new WPCIndividualFactory(4, -6, 6);
		RealFunctionCartPoleEnvironmentInteraction<WPC> interaction = new RealFunctionCartPoleEnvironmentInteraction<>(
				maxNumSteps, true, false);

		int cnt = 0;
		SummaryStatistics statistics = new SummaryStatistics();
		for (int i = 0; i < 1000; i++) {
			WPC wpc = factory.createRandomIndividual(context
					.getRandomForThread());
			InteractionResult result = interaction.interact(wpc, environment,
					context.getRandomForThread());
			statistics.addValue(result.firstResult());
			if (result.firstResult() == maxNumSteps) {
				cnt++;
			}
		}

		Assert.assertEquals("FFN average performance = ", 40.654, statistics.getMean(), 10e-4);
		Assert.assertEquals("Num solved tests by MLP", 239, cnt);
	}

	@Test
	public void testEnvAveragePerformance() {
		int maxNumSteps = 1000;
		WPCIndividualFactory factory = new WPCIndividualFactory(4, -6, 6);
		CartPoleEnvironmentRealFunctionInteraction interaction = new CartPoleEnvironmentRealFunctionInteraction(
				maxNumSteps, true, false);

		int cnt = 0;
		SummaryStatistics statistics = new SummaryStatistics();
		for (int i = 0; i < 1000; i++) {
			WPC wpc = factory.createRandomIndividual(context
					.getRandomForThread());
			InteractionResult result = interaction.interact(environment, wpc,
					context.getRandomForThread());
			statistics.addValue(result.firstResult());
			if (result.firstResult() <= 0.0) {
				cnt++;
			}
		}

		Assert.assertEquals("Environment average performance", 0.910058, statistics.getMean(), 10e-4);
		Assert.assertEquals("Num solved tests by MLP", 34, cnt);
	}

	// @Test
	public void testWPCAveragePerformance2() {
		int maxNumSteps = 50;

		CartPoleEnvironmentFactory environmentFactory = new CartPoleEnvironmentFactory(
				1, 1.0, 1.0, 10.0);

		WPCIndividualFactory factory = new WPCIndividualFactory(4, -6, 6);
		InteractionDomain<CartPoleEnvironment, RealFunction> interaction = new CartPoleEnvironmentRealFunctionInteraction(
				maxNumSteps, true, false);

		UniformRandomPopulationFactory<RealFunction> populationFactory = new UniformRandomPopulationFactory<>(
				factory);

		StatisticalSummary statistics;
		CartPoleEnvironment environment2;
		do {
			environment2 = environmentFactory.createRandomIndividual(context
					.getRandomForThread());
			ExpectedUtility<CartPoleEnvironment, RealFunction> utility = new ExpectedUtility<>(
					interaction, populationFactory, 100, context);
			statistics = utility.measure(environment2, context).stats();
		} while (statistics.getMean() > 0.2);

		System.out.println(environment2);
		System.out.println("Environment average difficulty = "
				+ statistics.getMean());
		System.out.println("Environment min difficulty = "
				+ statistics.getMin());
		System.out.println("Environment max difficulty = "
				+ statistics.getMax());
	}
}
