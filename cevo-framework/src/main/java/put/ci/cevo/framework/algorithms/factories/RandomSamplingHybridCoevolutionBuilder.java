package put.ci.cevo.framework.algorithms.factories;

import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingHybridCoevolution;
import put.ci.cevo.framework.factories.PopulationFactory;

@Build(target = RandomSamplingHybridCoevolution.class)
public class RandomSamplingHybridCoevolutionBuilder<S> extends
		GenerationalAlgorithmBuilder<S, S, RandomSamplingHybridCoevolutionBuilder<S>> {

	private PopulationFactory<S> randomSampleFactory;

	private int populationSampleSize;
	private int randomSampleSize;

	public PopulationFactory<S> getRandomSampleFactory() {
		return randomSampleFactory;
	}

	public RandomSamplingHybridCoevolutionBuilder<S> setRandomSampleFactory(PopulationFactory<S> randomSampleFactory) {
		this.randomSampleFactory = randomSampleFactory;
		return this;
	}

	public int getPopulationSampleSize() {
		return populationSampleSize;
	}

	public RandomSamplingHybridCoevolutionBuilder<S> setPopulationSampleSize(int populationSampleSize) {
		this.populationSampleSize = populationSampleSize;
		return this;
	}

	public int getRandomSampleSize() {
		return randomSampleSize;
	}

	public RandomSamplingHybridCoevolutionBuilder<S> setRandomSampleSize(int randomSampleSize) {
		this.randomSampleSize = randomSampleSize;
		return this;
	}

	@Override
	protected RandomSamplingHybridCoevolutionBuilder<S> getBuilder() {
		return this;
	}

	@Override
	public GenerationalOptimizationAlgorithm build() {
		nullchecks();
		return new RandomSamplingHybridCoevolution<>(
			createSpecies(), getInteractionScheme(), getFitnessAggregate(), getRandomSampleFactory(),
			getPopulationSampleSize(), getRandomSampleSize());
	}

}
