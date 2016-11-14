package put.ci.cevo.experiments.numbers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.List;

import static put.ci.cevo.util.RandomUtils.pickRandom;
import static put.ci.cevo.util.sequence.Sequences.range;

/**
 * Adds a random value chosen uniformly from [−distance−bias, distance−bias] to a given dimension. Mutation is applied
 * to two randomly chosen dimensions at the same time.
 */
public class NumbersGameBiasedMutation implements MutationOperator<DoubleVector> {

	private final double distance;
	private final double bias;

	@AccessedViaReflection
	public NumbersGameBiasedMutation(double distance, double bias) {
		this.distance = distance;
		this.bias = bias;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, RandomDataGenerator random) {
		double[] child = individual.toArray();
		List<Integer> indices = range(0, individual.size()).toList();
		int i = pickRandom(indices, random);
		int j = pickDimension(indices, random, i);

//		double noise = random.nextUniform(-distance - bias, distance - bias);
		child[i] = individual.get(i) + random.nextUniform(-distance - bias, distance - bias);
		child[j] = individual.get(j) + random.nextUniform(-distance - bias, distance - bias);
		return new DoubleVector(child);
	}

	private int pickDimension(List<Integer> indices, RandomDataGenerator random, int i) {
		indices.remove(i);
		return pickRandom(indices, random);
	}

}
