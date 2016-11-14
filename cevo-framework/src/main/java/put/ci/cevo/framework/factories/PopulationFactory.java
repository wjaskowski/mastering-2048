package put.ci.cevo.framework.factories;

import org.apache.commons.collections15.Factory;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.List;

public interface PopulationFactory<T> {

	public List<T> createPopulation(int populationSize, RandomDataGenerator random);
}
