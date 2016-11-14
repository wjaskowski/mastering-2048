package put.ci.cevo.framework.operators.crossover;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.vectors.DoubleVector;

/**
 * Implements exponential (or two-point modulo) crossover operator. Assumes vectors of the same length.
 */
public class ExponentialCrossover implements CrossoverOperator<DoubleVector> {

	private final double rate;

	public ExponentialCrossover(double rate) {
		this.rate = rate;
	}

	@Override
	public DoubleVector produce(Pair<DoubleVector, DoubleVector> individuals, RandomDataGenerator random) {
		DoubleVector donor = individuals.first();
		DoubleVector target = individuals.second();
		int startingPoint = random.nextInt(0, donor.size());
		int numComponents = computeNumberComponents(random, donor.size());

		double[] trial = target.toArray();
		for (int i = startingPoint; i < startingPoint + numComponents; i++) {
			int idx = i % donor.size();
			trial[idx] = donor.get(idx);
		}
		return new DoubleVector(trial);
	}

	private int computeNumberComponents(RandomDataGenerator random, int size) {
		int components = 0;
		do {
			components++;
		} while (random.nextUniform(0, 1) < rate && components < size);
		return components;
	}

}