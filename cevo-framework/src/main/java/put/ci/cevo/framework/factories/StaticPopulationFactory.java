package put.ci.cevo.framework.factories;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.List;

/**
 * Returns immutable copy of population provided in the constructor. Thus it is static and deterministic.
 */
public class StaticPopulationFactory<T> implements PopulationFactory<T> {

	private final ImmutableList<T> population;

	public StaticPopulationFactory(IndividualFactory<T> individualFactory, int populationSize, int seed) {
		this(new UniformRandomPopulationFactory<>(individualFactory), populationSize, seed);
	}

	public StaticPopulationFactory(PopulationFactory<T> factory, int populationSize, int seed) {
		this(factory.createPopulation(populationSize, new RandomDataGenerator(new MersenneTwister(seed))));
	}

	public StaticPopulationFactory(PopulationFactory<T> factory, int populationSize, RandomDataGenerator random) {
		this(factory.createPopulation(populationSize, random));
	}

	public StaticPopulationFactory(Iterable<T> population) {
		this.population = ImmutableList.copyOf(population);
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		Preconditions.checkArgument(populationSize <= population.size());
		return population.subList(0, populationSize);
	}

	public int getPopulationSize() {
		return population.size();
	}
}
