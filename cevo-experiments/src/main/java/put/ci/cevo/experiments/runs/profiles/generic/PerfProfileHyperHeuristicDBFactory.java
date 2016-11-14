package put.ci.cevo.experiments.runs.profiles.generic;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import put.ci.cevo.framework.factories.StagedPopulationFactory;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.util.RandomUtils.sample;

public class PerfProfileHyperHeuristicDBFactory<S, T> implements StagedPopulationFactory<S, T> {

	private final List<List<T>> buckets = new ArrayList<>();
	private final List<Double> performances = new ArrayList<>();
	private PerformanceMeasure<S> measure;

	private double lastPerformanceMeasure;
	private int evaluationTime;
	private int currentBucket;

	@AccessedViaReflection
	public PerfProfileHyperHeuristicDBFactory(String dbFile, int maxPerformance, int minPerformance,
			int performanceStep, PerformanceMeasure<S> measure, int evaluationTime) {

		this.measure = measure;
		this.evaluationTime = evaluationTime;

		for (int performance = minPerformance; performance < maxPerformance; performance += performanceStep) {
			List<T> players = PerfProfileDBFactory.readTestsOfDifficulty(dbFile, performance, performance
				+ performanceStep);

			buckets.add(players);
			performances.add(Double.MAX_VALUE);
		}
	}

	@Override
	public List<T> createPopulation(int generation, List<S> solutions, int populationSize, ThreadedContext context) {
		if (generation % evaluationTime == 0) {
			if (generation != 0) {
				double newPerformanceMeasure = evaluatePopulation(solutions, context);
				performances.set(currentBucket, newPerformanceMeasure - lastPerformanceMeasure);
				lastPerformanceMeasure = newPerformanceMeasure;
				currentBucket = chooseBestBucket(context.getRandomForThread());
			} else {
				lastPerformanceMeasure = evaluatePopulation(solutions, context);
				currentBucket = chooseBestBucket(context.getRandomForThread());
			}
		}

		return sample(buckets.get(currentBucket), populationSize, context.getRandomForThread());
	}

	private double evaluatePopulation(List<S> solutions, ThreadedContext context) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (S solution : solutions) {
			StatisticalSummary summary = measure.measure(solution, context).stats();
			stats.addValue(summary.getMean());
		}
		return stats.getMean();
	}

	private int chooseBestBucket(RandomDataGenerator random) {
		double bestPerformance = Double.NEGATIVE_INFINITY;
		List<Integer> bestBuckets = new ArrayList<Integer>();

		for (int i = 0; i < performances.size(); i++) {
			double performance = performances.get(i);
			if (performance == bestPerformance) {
				bestBuckets.add(i);
			} else if (performance > bestPerformance) {
				bestPerformance = performance;
				bestBuckets.clear();
				bestBuckets.add(i);
			}
		}

		return RandomUtils.pickRandom(bestBuckets, random);
	}
}
