package put.ci.cevo.experiments.runs.profiles.generic;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ConfiguredExperiment.Config;
import put.ci.cevo.experiments.reports.ConfiguredReports;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.factories.StagedPopulationFactory;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.configuration.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerfProfileDistinctionsDBFactory<S, T> implements StagedPopulationFactory<S, T> {

	private final InteractionScheme<S, T> interactionScheme;
	private final ThreadedContext context;
	private final FitnessAggregate aggregate;

	private final List<List<T>> pools = new ArrayList<>();

	private static final Logger logger = Logger.getLogger(ConfiguredReports.class);

	@AccessedViaReflection
	public PerfProfileDistinctionsDBFactory(String dbFile, int maxPerformance, int minPerformance, int performanceStep,
			InteractionScheme<S, T> interactionScheme, FitnessAggregate aggregate, ThreadedContext threadedRandom) {
		this.interactionScheme = interactionScheme;
		this.aggregate = aggregate;
		this.context = threadedRandom;

		for (int performance = minPerformance; performance < maxPerformance; performance += performanceStep) {
			List<T> tests = PerfProfileDBFactory.readTestsOfDifficulty(dbFile, performance, performance
				+ performanceStep);
			pools.add(tests);
		}

		try {
			Configuration.addDedicateFiledAppender(logger, "staged_population_factory", Config.EXPERIMENT_ID);
		} catch (IOException e) {
			logger.error("Unable to create dedicated appender", e);
		}
	}

	@Override
	public List<T> createPopulation(int generation, List<S> solutions, int populationSize, ThreadedContext context) {
		int bestPool = -1;
		double maxSumFitness = 0;
		List<T> bestSample = null;

		for (int pool = 0; pool < pools.size(); pool++) {
			List<T> sample = RandomUtils.sample(pools.get(pool), populationSize, context.getRandomForThread());
			InteractionTable<S, T> interactionTable = interactionScheme.interact(solutions, sample, this.context);
			Map<T, Fitness> fitnessMap = aggregate.aggregateFitness(interactionTable.getTestsPayoffs(), this.context);

			int sumFitness = 0;
			for (T test : sample) {
				sumFitness += fitnessMap.get(test).fitness();
			}

			if (sumFitness > maxSumFitness) {
				bestPool = pool;
				bestSample = sample;
				maxSumFitness = sumFitness;
			}
		}

		logger.info("Generation: " + generation + "; best pool: " + bestPool + "; with the fitness: " + maxSumFitness);
		return bestSample;
	}

	public int countDistinctionsMade(PayoffTable<T, S> payoffTable) {
		List<S> solutions = payoffTable.tests().toList();
		List<T> tests = payoffTable.solutions().toList();

		int count = 0;
		for (int i = 0; i < solutions.size(); i++) {
			for (int j = 0; j < i; j++) {
				for (T test : tests) {
					Double result0 = payoffTable.get(test, solutions.get(i));
					Double result1 = payoffTable.get(test, solutions.get(j));
					if (result0 != null && result1 != null && !result0.equals(result1)) {
						count++;
						break;
					}
				}
			}
		}

		return count;
	}
}
