package put.ci.cevo.experiments.runs.profiles.cartpole;

import java.io.File;

import org.joda.time.Duration;

import put.ci.cevo.experiments.mlp.FeedForwardNetworkIndividualFactory;
import put.ci.cevo.experiments.mlp.MLPIndividualFactory;
import put.ci.cevo.experiments.profiles.generators.RandomStrategyGenerator;
import put.ci.cevo.experiments.profiles.generators.StrategyGenerator;
import put.ci.cevo.experiments.runs.cartpole.CartPoleEnvironmentFactory;
import put.ci.cevo.experiments.runs.cartpole.CartPoleEnvironmentRealFunctionInteraction;
import put.ci.cevo.experiments.runs.profiles.generic.PerfProfileDatabaseGenerator;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;
import put.ci.cevo.util.random.ThreadedContext;

public class CartPoleEnvironmentPerfProfileDatabaseRandomGenerator implements Experiment {

	private final InteractionDomain<CartPoleEnvironment, RealFunction> interaction;
	private final PerformanceMeasure<CartPoleEnvironment> performanceMeasure;

	private final StrategyGenerator<CartPoleEnvironment> strategyGenerator;

	private final PerfProfileDatabaseGenerator<CartPoleEnvironment> perfProfileDatabaseGenerator;
	private final CartPoleEnvironmentFactory envFactory;
	private final ThreadedContext context = new ThreadedContext();
	private final MLPIndividualFactory mlpFactory;
	private final WPCIndividualFactory wpcIndividualFactory;
	private final FeedForwardNetworkIndividualFactory feedForwardNetworkIndividualFactory;
	
	public CartPoleEnvironmentPerfProfileDatabaseRandomGenerator() {
		//R U Sure that + 1?
		mlpFactory = new MLPIndividualFactory(CartPoleEnvironment.NUM_INPUTS_ONE_POLE + 1, 10, 1, -6.0, 6.0);

		feedForwardNetworkIndividualFactory = new FeedForwardNetworkIndividualFactory(
				CartPoleEnvironment.NUM_INPUTS_ONE_POLE, 1, 1, -6, 6);
		wpcIndividualFactory = new WPCIndividualFactory(4, -6, 6);

		envFactory = new CartPoleEnvironmentFactory(1, 1.0, 1.0, 10.0, 10.0);

		interaction = new CartPoleEnvironmentRealFunctionInteraction(100, true, true);

		Integer numOpponents = 1000;
		performanceMeasure = new ExpectedUtility<CartPoleEnvironment, RealFunction>(interaction,
				new UniformRandomPopulationFactory<RealFunction>(wpcIndividualFactory), numOpponents, context);
		strategyGenerator = new RandomStrategyGenerator<>(envFactory);

		final Duration backupInterval = Duration.standardHours(2);
		final Duration checkpointInterval = Duration.standardSeconds(60);

		final int maxBucketSize = 100000;
		final int numBuckets = 100;
		final int totalNumStrategies = 100000;
		perfProfileDatabaseGenerator = new PerfProfileDatabaseGenerator<CartPoleEnvironment>(performanceMeasure,
				strategyGenerator, numBuckets, maxBucketSize, totalNumStrategies, new File(
						"cart-pole-envs-gravity-binary-wpc-100-steps-100000-in-total.dump"), backupInterval,
				checkpointInterval, context);
		// @formatter:on
	}

	@Override
	public void run(String[] args) {
		perfProfileDatabaseGenerator.run();
	}
}
