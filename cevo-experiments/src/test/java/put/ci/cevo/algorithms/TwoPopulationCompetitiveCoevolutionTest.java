package put.ci.cevo.algorithms;

import com.google.common.primitives.Doubles;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.othello.OthelloWPCPopulationFactory;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.RetrospectiveAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.TwoPopulationCompetitiveCoevolution;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuCommaLambdaEvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.queries.BestOfGenerationSolutionQuery;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.sequence.Sequences;

import java.util.List;

public class TwoPopulationCompetitiveCoevolutionTest {

	@Test
	public void testTwoPopulationCompetitiveCoevolution01() {
		ThreadedContext context = new ThreadedContext(0, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createTwoPopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.625,0.66015625,0.630859375,0.701171875,0.625 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testTwoPopulationCompetitiveCoevolution02() {
		ThreadedContext context = new ThreadedContext(1, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createTwoPopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.609375,0.71484375,0.650390625,0.6015625,0.654296875 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testTwoPopulationCompetitiveCoevolution03() {
		ThreadedContext context = new ThreadedContext(2, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createTwoPopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.626953125,0.55078125,0.671875,0.701171875,0.58984375 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testTwoPopulationCompetitiveCoevolution04() {
		ThreadedContext context = new ThreadedContext(3, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createTwoPopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.59765625,0.599609375,0.626953125,0.662109375,0.67578125 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testTwoPopulationCompetitiveCoevolution05() {
		ThreadedContext context = new ThreadedContext(4, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createTwoPopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.5859375,0.666015625,0.666015625,0.6640625,0.681640625 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	public static TwoPopulationCompetitiveCoevolution<WPC, WPC> createTwoPopulationCompetitiveCoevolution(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);
		EvolutionModel<WPC> solutionsModel = new MuPlusLambdaEvolutionModel<>(popSize / 2, popSize / 2, new WPCGaussianMutation(0.2, 0.2));
		EvolutionModel<WPC> testsModel = new MuCommaLambdaEvolutionModel<>(popSize / 2, popSize, new WPCGaussianMutation(0.2, 0.2));
		Species<WPC> solutionsSpecies = new Species<>(solutionsModel, new OthelloWPCPopulationFactory(), popSize);
		Species<WPC> testsSpecies = new Species<>(testsModel, new OthelloWPCPopulationFactory(), popSize);
		return new TwoPopulationCompetitiveCoevolution<>(solutionsSpecies, testsSpecies, new SimpleSumFitness(), scheme);
	}

}
