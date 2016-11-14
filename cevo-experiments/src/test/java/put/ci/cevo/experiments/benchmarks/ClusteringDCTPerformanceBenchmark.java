package put.ci.cevo.experiments.benchmarks;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.apache.log4j.Logger;
import org.junit.Test;
import put.ci.cevo.experiments.dct.CADensityFactory;
import put.ci.cevo.experiments.dct.CADensityMutation;
import put.ci.cevo.experiments.dct.CARuleFactory;
import put.ci.cevo.experiments.dct.CARuleMutation;
import put.ci.cevo.experiments.dct.interaction.RuleDensityDCTInteraction;
import put.ci.cevo.framework.algorithms.TwoPopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.algorithms.multiobjective.nsga2.NSGA2TwoPopulationEvaluator;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.evaluators.coev.TwoPopulationCoevolutionaryEvaluator;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.experiments.clustering.aggregates.KMeansFitnessAggregate;
import put.ci.cevo.experiments.clustering.aggregates.MultipleKMeansFitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.GenericEvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.util.math.VarianceDistanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.framework.selection.TournamentSelection;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.dct.CADensity;
import put.ci.cevo.games.dct.CARule;

import static put.ci.cevo.util.stats.EventsLoggerFactory.createDefault;

public class ClusteringDCTPerformanceBenchmark extends AbstractBenchmark {

	private static final Logger logger = Logger.getLogger(ClusteringDCTPerformanceBenchmark.class);

	private static final int GENERATIONS = 5;
	private static final int TIMESTEPS = 320;
	private static final int ICLENGTH = 149;
	private static final int NEIGHBORHOODSIZE = 3;
	private static final int POPSIZE = 200;
	private static final int TESTSIZE = 200;

	private static final InteractionDomain<CARule, CADensity> DCT = new RuleDensityDCTInteraction(TIMESTEPS,
			NEIGHBORHOODSIZE, 1);

	private static ThreadedContext context = ThreadedContext.withEventsLogger(new ThreadedRandom(123), 8, createDefault());

	@BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 1) @Test
	public void testMultiClusteringPerformance() {
		TwoPopulationEvolutionaryAlgorithm<CARule, CADensity> algorithm = createAlgorithm(createMultiClusteringEvaluator(),
				POPSIZE / 2, TESTSIZE);
		run(algorithm);
	}

	@BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 1) @Test
	public void testClusteringPerformance() {
		TwoPopulationEvolutionaryAlgorithm<CARule, CADensity> algorithm = createAlgorithm(createClusteringEvaluator(),
				POPSIZE / 2, TESTSIZE);
		run(algorithm);
	}

	@BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 1) @Test
	public void testPlainPerformance() {
		TwoPopulationEvolutionaryAlgorithm<CARule, CADensity> algorithm = createAlgorithm(createPlainEvaluator(),
				POPSIZE, TESTSIZE);
		run(algorithm);
	}

	private void run(TwoPopulationEvolutionaryAlgorithm<CARule, CADensity> algorithm) {
		algorithm.addLastGenerationListener(new LastGenerationListener() {
			@Override public void onLastGeneration(EvolutionState state) {
				logger.info("Total effort: " + state.getTotalEffort());
			}
		});
		algorithm.evolve(new GenerationsTarget(GENERATIONS), context);
	}

	private static TwoPopulationEvolutionaryAlgorithm<CARule, CADensity> createAlgorithm(
			TwoPopulationEvaluator<CARule, CADensity> evaluator, int popSize, int testSize) {

		CARuleMutation mut1 = new CARuleMutation(0.2);
		CADensityMutation mut2 = new CADensityMutation(0.05);

		EvolutionModel<CARule> m1 = new GenericEvolutionModel<>(new TournamentSelection<CARule>(popSize, 5), mut1);
		EvolutionModel<CADensity> m2 = new MuPlusLambdaEvolutionModel<>(testSize / 2, testSize / 2, mut2);

		Species<CARule> s1 = new Species<>(m1,new UniformRandomPopulationFactory<>(
				new CARuleFactory(NEIGHBORHOODSIZE)), popSize);
		Species<CADensity> s2 = new Species<>(m2, new UniformRandomPopulationFactory<>(new CADensityFactory(ICLENGTH)),
				testSize);

		return new TwoPopulationEvolutionaryAlgorithm<>(s1, s2, evaluator);
	}

	private static NSGA2TwoPopulationEvaluator<CARule, CADensity> createMultiClusteringEvaluator() {
		RoundRobinInteractionScheme<CARule, CADensity> scheme = new RoundRobinInteractionScheme<>(DCT);
		return new NSGA2TwoPopulationEvaluator<>(scheme, new MultipleKMeansFitnessAggregate(4, 10),
				new SimpleSumFitness());
	}

	private TwoPopulationEvaluator<CARule, CADensity> createClusteringEvaluator() {
		RoundRobinInteractionScheme<CARule, CADensity> scheme = new RoundRobinInteractionScheme<>(DCT);
		return new NSGA2TwoPopulationEvaluator<>(scheme, new KMeansFitnessAggregate(4, new VarianceDistanceMeasure()),
				new SimpleSumFitness());
	}

	private static TwoPopulationCoevolutionaryEvaluator<CARule, CADensity> createPlainEvaluator() {
		RoundRobinInteractionScheme<CARule, CADensity> scheme = new RoundRobinInteractionScheme<>(DCT);
		return new TwoPopulationCoevolutionaryEvaluator<>(new SimpleSumFitness(), new SimpleSumFitness(), scheme);
	}
}
