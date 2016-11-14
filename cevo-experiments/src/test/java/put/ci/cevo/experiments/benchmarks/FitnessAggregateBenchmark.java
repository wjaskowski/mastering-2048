package put.ci.cevo.experiments.benchmarks;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.experiments.wpc.WPCPopulationFactory;
import put.ci.cevo.framework.algorithms.OptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.OnePopulationCompetitiveCoevolution;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.DistinctionsFitnessSharing;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class FitnessAggregateBenchmark extends AbstractBenchmark {

	private static final int GENERATIONS = 3;
	private static final int POP_SIZE = 100;
	private static final int THREADS = 8;

	private static final InteractionDomain<WPC, WPC> OTHELLO = new OthelloWPCInteraction(true);

	private ThreadedContext context;

	@Before
	public void setUp() {
		context = new ThreadedContext(123, THREADS);
	}

	@BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 1)
	@Test
	public void testDistinctionFitnessSharingPerformance() {
		createAlgorithm(new DistinctionsFitnessSharing()).evolve(new GenerationsTarget(GENERATIONS), context);
	}

	@BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 1)
	@Test
	public void testSimpleSumPerformance() {
		createAlgorithm(new SimpleSumFitness()).evolve(new GenerationsTarget(GENERATIONS), context);
	}

	@BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 1)
	@Test
	public void testRawInteractionPerformance() {
		PopulationFactory<WPC> generator = new WPCPopulationFactory(1.0, -1.0, OthelloBoard.NUM_FIELDS);
		final List<WPC> population = generator.createPopulation(POP_SIZE, new RandomDataGenerator());

		for (int i = 0; i < GENERATIONS; ++i) {
			context.submit(new ThreadedContext.Worker<WPC, Void>() {
				@Override
				public Void process(WPC solution, ThreadedContext context) throws Exception {
					for (WPC test : population) {
						OTHELLO.interact(solution, test, context.getRandomForThread());
					}
					return null;
				}
			}, population);
		}
	}

	private static <T> OptimizationAlgorithm createAlgorithm(FitnessAggregate aggregate) {
		final RoundRobinInteractionScheme<WPC, WPC> interactionScheme = new RoundRobinInteractionScheme<WPC, WPC>(
			OTHELLO);
		final MuPlusLambdaEvolutionModel<WPC> model = new MuPlusLambdaEvolutionModel<WPC>(
			POP_SIZE / 2, POP_SIZE / 2, new WPCGaussianMutation(0.5, 1.0));

		final Species<WPC> species = new Species<>(
			model, new WPCPopulationFactory(10, -10, OthelloBoard.NUM_FIELDS), POP_SIZE);
		return new OnePopulationCompetitiveCoevolution<WPC>(species, interactionScheme, aggregate);
	}
}
