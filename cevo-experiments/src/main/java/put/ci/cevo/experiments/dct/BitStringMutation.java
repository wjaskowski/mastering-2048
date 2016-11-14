package put.ci.cevo.experiments.dct;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.vectors.IntegerVector;

public class BitStringMutation implements MutationOperator<IntegerVector> {

	private final double probability;

	public BitStringMutation(double probability) {
		this.probability = probability;
	}

	@Override
	public IntegerVector produce(IntegerVector individual, RandomDataGenerator random) {
		int[] childVector = individual.getVector().clone();
		for (int i = 0; i < individual.getSize(); i++) {
			if (random.nextUniform(0, 1) < probability) {
				childVector[i] ^= 1;
			}
		}
		return new IntegerVector(childVector);
	}

}
