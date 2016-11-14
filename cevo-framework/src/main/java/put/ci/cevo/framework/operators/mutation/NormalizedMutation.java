package put.ci.cevo.framework.operators.mutation;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.ArrayUtils;
import put.ci.cevo.util.vectors.DoubleVector;

/**
 * Mutate using mutation, but normalize the vector afterwards
 * 
 */
public class NormalizedMutation implements MutationOperator<DoubleVector> {

	private final MutationOperator<DoubleVector> mutation;

	public NormalizedMutation(MutationOperator<DoubleVector> mutation) {
		this.mutation = mutation;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, RandomDataGenerator random) {
		DoubleVector mutated = mutation.produce(individual, random);
		return new DoubleVector(ArrayUtils.normalized(mutated.toArray()));
	}
}
