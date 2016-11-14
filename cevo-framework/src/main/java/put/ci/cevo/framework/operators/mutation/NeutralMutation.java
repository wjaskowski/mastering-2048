package put.ci.cevo.framework.operators.mutation;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * Mutation that returns the original individual. Be careful: this is the same object (the individual is not cloned).
 */
public class NeutralMutation<T> implements MutationOperator<T> {

	@Override
	public T produce(T individual, RandomDataGenerator random) {
		return individual;
	}

}
