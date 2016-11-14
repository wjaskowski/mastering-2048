package put.ci.cevo.experiments.runs.profiles.generic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.util.RandomUtils;

public class PerfProfileRealDistributionDBFactory<T> implements PopulationFactory<T> {

	private final List<List<T>> buckets = new ArrayList<>();
	private final List<Integer> numPlayers = new ArrayList<>();

	public PerfProfileRealDistributionDBFactory(RealDistribution distribution, int poolSize, String dbFile) {
		for (int i = 0; i < 100; i++) {
			double range = distribution.cumulativeProbability(i + 1) - distribution.cumulativeProbability(i);
			int numplayers = (int) (range * poolSize);
			if (numplayers > 0) {
				numPlayers.add(numplayers);
				buckets.add(PerfProfileDBFactory.<T>readTestsOfDifficulty(dbFile, i, i + 1));
			}
		}
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		List<T> pool = new ArrayList<>();
		for (int bucket = 0; bucket < buckets.size(); bucket++) {
			int sampleSize = Math.min(buckets.get(bucket).size(), numPlayers.get(bucket));
			List<T> bucketSample = RandomUtils.sample(buckets.get(bucket), sampleSize, random);
			pool.addAll(bucketSample);
		}
		return RandomUtils.sample(pool, populationSize, random);
	}
	
	public static void main(String[] args) {
		NormalDistribution distribution = new NormalDistribution(70, 10);
		for (int i = 0; i < 100; i++) {
			double prob = distribution.cumulativeProbability(i+1) - distribution.cumulativeProbability(i);	
			System.out.println(i + " : " + prob);
		}
	}
}
