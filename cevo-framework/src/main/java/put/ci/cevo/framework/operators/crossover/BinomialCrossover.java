package put.ci.cevo.framework.operators.crossover;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.vectors.DoubleVector;

/**
 * Implements a simple binomial crossover. At least one component from the other vector is chosen.
 */
public class BinomialCrossover implements CrossoverOperator<DoubleVector> {

	private final double rate;

	public BinomialCrossover(double rate) {
		this.rate = rate;
	}

	@Override
	public DoubleVector produce(Pair<DoubleVector, DoubleVector> individuals, RandomDataGenerator random) {
		DoubleVector donor = individuals.first();
		DoubleVector target = individuals.second();

		double[] trial = target.toArray();
		int j = random.nextInt(0, trial.length);
		for (int i = 0; i < trial.length; i++) {
			if (random.nextUniform(0, 1) < rate || i == j) {
				trial[i] = donor.get(i);
			}

		}
		return new DoubleVector(trial);
	}

}