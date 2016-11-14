package put.ci.cevo.experiments.runs.profiles.generic;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

public class PerfProfileDiversifiedDBFactory<T> implements PopulationFactory<T> {

	private final List<List<T>> pools = new ArrayList<>();

	@AccessedViaReflection
	public PerfProfileDiversifiedDBFactory(String dbFile, int maxPerformance, int minPerformance, int performanceStep) {
		for (int performance = minPerformance; performance < maxPerformance; performance += performanceStep) {
			List<T> tests = PerfProfileDBFactory.readTestsOfDifficulty(dbFile, performance, performance
				+ performanceStep);
			pools.add(tests);
		}
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		List<T> sample = new ArrayList<>();
		int testsPerBucket = populationSize / pools.size();
		int leftTests = populationSize % pools.size();

		for (int i = 0; i < pools.size(); i++) {
			if (i < leftTests) {
				sample.addAll(RandomUtils.sample(pools.get(i), testsPerBucket + 1, random));
			} else {
				sample.addAll(RandomUtils.sample(pools.get(i), testsPerBucket, random));
			}
		}

		return sample;
	}
}
