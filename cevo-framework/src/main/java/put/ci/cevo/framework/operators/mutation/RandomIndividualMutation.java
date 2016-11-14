package put.ci.cevo.framework.operators.mutation;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class RandomIndividualMutation<T> implements MutationOperator<T> {

	private final IndividualFactory<T> factory;

	@AccessedViaReflection
	public RandomIndividualMutation(IndividualFactory<T> factory) {
		this.factory = factory;
	}

	@Override
	public T produce(T individual, RandomDataGenerator random) {
		return factory.createRandomIndividual(random);
	}
}
