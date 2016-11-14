package put.ci.cevo.experiments.ipd;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.vectors.IntegerVector;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Mutation replaces the original choice of an element in the direct look-up table with one of the remaining n−1
 * possible choices with an equal probability of 1/(n−1). In this way, each element has a fixed probability of being
 * replaced.
 */
public class IPDMutation implements MutationOperator<IPDVector> {

	private final double probability;

	@AccessedViaReflection
	public IPDMutation(double probability) {
		this.probability = probability;
	}

	@Override
	public IPDVector produce(IPDVector individual, RandomDataGenerator random) {
		IntegerVectorUniformMutation mutation = new IntegerVectorUniformMutation(
			0, individual.getChoices() - 1, probability, false);
		IntegerVector child = mutation.produce(individual.getIntegerVector(), random);
		return new IPDVector(child);
	}
}
