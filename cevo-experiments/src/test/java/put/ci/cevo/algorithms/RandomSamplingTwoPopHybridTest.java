package put.ci.cevo.algorithms;

import com.google.common.primitives.Doubles;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.othello.OthelloWPCPopulationFactory;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.RetrospectiveAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingTwoPopHybrid;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.GenericEvolutionModel;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.queries.BestOfGenerationSolutionQuery;
import put.ci.cevo.framework.selection.StochasticUniversalSampling;
import put.ci.cevo.framework.selection.TournamentSelection;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.sequence.Sequences;

import java.util.List;

public class RandomSamplingTwoPopHybridTest {

	@Test
	public void testRandomSamplingTwoPopHybrid01() {
		ThreadedContext context = new ThreadedContext(0, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingTwoPopHybrid(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.662109375,0.62109375,0.669921875,0.72265625,0.736328125 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingTwoPopHybrid02() {
		ThreadedContext context = new ThreadedContext(1, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingTwoPopHybrid(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.55859375,0.69140625,0.73828125,0.728515625,0.671875 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingTwoPopHybrid03() {
		ThreadedContext context = new ThreadedContext(2, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingTwoPopHybrid(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.59375,0.65234375,0.64453125,0.65625,0.681640625 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingTwoPopHybrid04() {
		ThreadedContext context = new ThreadedContext(3, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingTwoPopHybrid(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.685546875,0.673828125,0.578125,0.671875,0.708984375 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testRandomSamplingTwoPopHybrid05() {
		ThreadedContext context = new ThreadedContext(4, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createRandomSamplingTwoPopHybrid(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.67578125,0.587890625,0.677734375,0.70703125,0.71875 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	public static RandomSamplingTwoPopHybrid<WPC, WPC> createRandomSamplingTwoPopHybrid(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);
		final WPCGaussianMutation mut = new WPCGaussianMutation(0.2, 0.2);

		EvolutionModel<WPC> solutionsModel = new GenericEvolutionModel<>(new TournamentSelection<WPC>(popSize, 5), mut);
		EvolutionModel<WPC> testsModel = new GenericEvolutionModel<>(new StochasticUniversalSampling<WPC>(popSize / 2), mut);
		Species<WPC> solutionsSpecies = new Species<>(solutionsModel, new OthelloWPCPopulationFactory(), popSize);
		Species<WPC> testSpecies = new Species<>(testsModel, new OthelloWPCPopulationFactory(), popSize / 2);
		return new RandomSamplingTwoPopHybrid<>(solutionsSpecies, testSpecies, new SimpleSumFitness(), scheme,
				new OthelloWPCPopulationFactory(), popSize / 2);
	}

}
