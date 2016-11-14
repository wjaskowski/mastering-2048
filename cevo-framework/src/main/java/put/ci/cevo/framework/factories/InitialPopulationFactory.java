package put.ci.cevo.framework.factories;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.List;

/**
 * Population factory with known population size.
 * 
 */
public class InitialPopulationFactory<T> {

	private final PopulationFactory<T> factory;
	private final int populationSize;

	@AccessedViaReflection
	public InitialPopulationFactory(PopulationFactory<T> factory, int populationSize) {
		this.factory = factory;
		this.populationSize = populationSize;
	}

	public List<T> createPopulation(RandomDataGenerator random) {
		return factory.createPopulation(populationSize, random);
	}

	public int getPopulationSize() {
		return populationSize;
	}

}
