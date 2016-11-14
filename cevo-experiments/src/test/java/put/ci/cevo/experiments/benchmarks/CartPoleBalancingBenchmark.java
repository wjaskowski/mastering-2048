package put.ci.cevo.experiments.benchmarks;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.experiments.mlp.FeedForwardNetworkIndividualFactory;
import put.ci.cevo.experiments.runs.cartpole.RealFunctionCartPoleEnvironmentInteraction;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.agent.functions.mlp.FeedForwardNetwork;
import put.ci.cevo.rl.agent.functions.mlp.MLP;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;
import put.ci.cevo.util.random.ThreadedContext;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class CartPoleBalancingBenchmark extends AbstractBenchmark {

	private final static int POP_SIZE = 1000;
	private final static int MAX_NUM_STEPS = 10000;
	
	private ThreadedContext context;
	
	private List<? extends RealFunction> mlpPopulation;
	private CartPoleEnvironment environment;
	private RealFunctionCartPoleEnvironmentInteraction<MLP> interaction;

	@Before
	public void setUp() {
		context = new ThreadedContext();
		
		environment = new CartPoleEnvironment(1);
		interaction = new RealFunctionCartPoleEnvironmentInteraction<MLP>(MAX_NUM_STEPS, true, false);
		
		FeedForwardNetworkIndividualFactory factory = new FeedForwardNetworkIndividualFactory(4, 1, 1, -6, 6);
		UniformRandomPopulationFactory<FeedForwardNetwork> populationFactory = new UniformRandomPopulationFactory<>(factory);
		
		mlpPopulation = populationFactory.createPopulation(POP_SIZE, context.getRandomForThread());
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testMDPInteractionsMLPPerformance() {
		double sum = 0;
		for (RealFunction controller : mlpPopulation) {
			InteractionResult result = interaction.interact(controller, environment, context.getRandomForThread());
			sum += result.firstResult();
		}
		System.out.println(sum / POP_SIZE);
	}
	
	
	@Test
	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	public void testFastEvaluateMLPPerformance() {
		double sum = 0;
		for (RealFunction controller : mlpPopulation) {
			sum += environment.evaluate(controller, MAX_NUM_STEPS, context.getRandomForThread());
		}
		System.out.println(sum / POP_SIZE);
	}
}
