package put.ci.cevo.algorithms;

import com.google.common.primitives.Doubles;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.othello.OthelloWPCPopulationFactory;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.RetrospectiveAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingEvolutionaryLearning;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.queries.BestOfGenerationSolutionQuery;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.sequence.Sequences;

import java.util.List;

public class RandomSamplingEvolutionaryLearningTest {

	@Test
	public void testRandomSamplingEvolutionaryLearning01() {
		ThreadedContext context = new ThreadedContext(0, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingEvolutionaryLearning(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.697265625,0.66796875,0.6640625,0.65625,0.70703125 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingEvolutionaryLearning02() {
		ThreadedContext context = new ThreadedContext(1, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingEvolutionaryLearning(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.580078125,0.60546875,0.6328125,0.6953125,0.638671875 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingEvolutionaryLearning03() {
		ThreadedContext context = new ThreadedContext(2, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingEvolutionaryLearning(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.552734375,0.58984375,0.62109375,0.716796875,0.63671875 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingEvolutionaryLearning04() {
		ThreadedContext context = new ThreadedContext(3, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingEvolutionaryLearning(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.65234375,0.697265625,0.65234375,0.61328125,0.697265625 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingEvolutionaryLearning05() {
		ThreadedContext context = new ThreadedContext(4, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingEvolutionaryLearning(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.650390625,0.6328125,0.6796875,0.666015625,0.6875 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	public static RandomSamplingEvolutionaryLearning<WPC, WPC> createRandomSamplingEvolutionaryLearning(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);

		EvolutionModel<WPC> solutionsModel = new MuPlusLambdaEvolutionModel<>(popSize / 2, popSize / 2, new WPCGaussianMutation(0.2, 0.2));
		Species<WPC> species = new Species<>(solutionsModel, new OthelloWPCPopulationFactory(), popSize);

		return new RandomSamplingEvolutionaryLearning<>(species, scheme, new SimpleSumFitness(),
				new OthelloWPCPopulationFactory(), 50);
	}

}
