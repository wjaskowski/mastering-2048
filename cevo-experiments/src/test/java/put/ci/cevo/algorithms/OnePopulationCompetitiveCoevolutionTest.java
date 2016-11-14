package put.ci.cevo.algorithms;

import com.google.common.primitives.Doubles;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.othello.OthelloWPCPopulationFactory;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.RetrospectiveAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.OnePopulationCompetitiveCoevolution;
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

public class OnePopulationCompetitiveCoevolutionTest {

	@Test
	public void testOnePopulationCompetitiveCoevolution01() {
		ThreadedContext context = new ThreadedContext(0, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createOnePopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.65625,0.693359375,0.619140625,0.619140625,0.681640625 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testOnePopulationCompetitiveCoevolution02() {
		ThreadedContext context = new ThreadedContext(1, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createOnePopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.6484375,0.607421875,0.669921875,0.654296875,0.607421875 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testOnePopulationCompetitiveCoevolution03() {
		ThreadedContext context = new ThreadedContext(2, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createOnePopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.609375,0.6015625,0.591796875,0.505859375,0.587890625 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testOnePopulationCompetitiveCoevolution04() {
		ThreadedContext context = new ThreadedContext(3, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createOnePopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.611328125,0.638671875,0.673828125,0.640625,0.658203125 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}

	@Test
	public void testOnePopulationCompetitiveCoevolution05() {
		ThreadedContext context = new ThreadedContext(4, 4);
		OthelloWPCInteraction domain = new OthelloWPCInteraction();

		GenerationalOptimizationAlgorithm algorithm = createOnePopulationCompetitiveCoevolution(domain, 50);
		TestCallback stateCallback = new TestCallback();
		algorithm.addNextGenerationListener(stateCallback);

		RetrospectiveAlgorithm retrospectiveAlgorithm = RetrospectiveAlgorithm.wrap(algorithm);
		Retrospector retrospector = retrospectiveAlgorithm.evolve(new GenerationsTarget(5), context);

		ExpectedUtility<WPC, WPC> measure = new ExpectedUtility<>(domain, new OthelloWPCPopulationFactory(), 128, context);
		List<Double> fitness = retrospector.inquire(new BestOfGenerationSolutionQuery<WPC>(), measure, context).map(EvaluatedIndividual.<WPC> toFitness()).toList();

		stateCallback.assertGenerations(Sequences.range(5).toList());
		stateCallback.assertEffort(Sequences.range(50 * 50 * 2, 50 * 50 * 2 * 6, 50 * 50 * 2).toList());

		double[] expected = new double[] { 0.666015625,0.560546875,0.666015625,0.662109375,0.646484375 };
		Assert.assertArrayEquals(expected, Doubles.toArray(fitness), 0.0000000000001);
	}


	public static OnePopulationCompetitiveCoevolution<WPC> createOnePopulationCompetitiveCoevolution(InteractionDomain<WPC, WPC> domain, int popSize) {
		RoundRobinInteractionScheme<WPC, WPC> scheme = new RoundRobinInteractionScheme<>(domain);
		EvolutionModel<WPC> evolutionModel = new MuPlusLambdaEvolutionModel<>(popSize / 2, popSize / 2, new WPCGaussianMutation(0.2, 0.2));
		Species<WPC> species = new Species<>(evolutionModel, new OthelloWPCPopulationFactory(), popSize);
		return new OnePopulationCompetitiveCoevolution<>(species, scheme, new SimpleSumFitness());
	}
}
