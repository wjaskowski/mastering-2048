package put.ci.cevo.experiments.benchmarks;

import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.experiments.dct.CADensityFactory;
import put.ci.cevo.experiments.dct.CARuleFactory;
import put.ci.cevo.experiments.dct.interaction.RuleDensityDCTInteraction;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.games.dct.CADensity;
import put.ci.cevo.games.dct.CARule;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class DCTBenchmark extends AbstractBenchmark {

	private ThreadedRandom random;

	@Before
	public void setUp() {
		random = new ThreadedRandom(123);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testPerformance() {
		final int timeSteps = 320;
		final int icLength = 149;
		final int neighborhoodSize = 3;
		final int popSize = 200;
		final InteractionDomain<CARule, CADensity> dct = new RuleDensityDCTInteraction(timeSteps, neighborhoodSize, 1);

		PopulationFactory<CARule> rules = new UniformRandomPopulationFactory<>(new CARuleFactory(neighborhoodSize));
		PopulationFactory<CADensity> densities = new UniformRandomPopulationFactory<>(new CADensityFactory(icLength));

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (CARule s : rules.createPopulation(popSize, random.forThread())) {
			for (CADensity t : densities.createPopulation(popSize, random.forThread())) {
				InteractionResult.aggregate(res, dct.interact(s, t, random.forThread()));
			}
		}
	}

}
