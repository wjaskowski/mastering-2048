package put.ci.cevo.framework.factories;

import org.apache.commons.collections15.Factory;
import org.apache.commons.math3.random.RandomDataGenerator;

public class IndividualFactoryFromFactory<T> implements IndividualFactory<T> {

	private final Factory<T> factory;

	public IndividualFactoryFromFactory(Factory<T> factory) {
		this.factory = factory;
	}

	@Override
	public T createRandomIndividual(RandomDataGenerator random) {
		return factory.create();
	}

}
