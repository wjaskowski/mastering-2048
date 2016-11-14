package put.ci.cevo.framework.algorithms.factories;

import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.random.RandomSamplingEvolutionaryLearning;
import put.ci.cevo.framework.factories.PopulationFactory;

@Build(target = RandomSamplingEvolutionaryLearning.class)
public class RandomSamplingEvolutionaryLearningBuilder<S> extends
		GenerationalAlgorithmBuilder<S, S, RandomSamplingEvolutionaryLearningBuilder<S>> {

	private PopulationFactory<S> randomSampleFactory;

	private int randomSampleSize;

	public PopulationFactory<S> getRandomSampleFactory() {
		return randomSampleFactory;
	}

	public RandomSamplingEvolutionaryLearningBuilder<S> setRandomSampleFactory(PopulationFactory<S> randomSampleFactory) {
		this.randomSampleFactory = randomSampleFactory;
		return this;
	}

	public int getRandomSampleSize() {
		return randomSampleSize;
	}

	public RandomSamplingEvolutionaryLearningBuilder<S> setRandomSampleSize(int randomSampleSize) {
		this.randomSampleSize = randomSampleSize;
		return this;
	}

	@Override
	protected RandomSamplingEvolutionaryLearningBuilder<S> getBuilder() {
		return this;
	}

	@Override
	public GenerationalOptimizationAlgorithm build() {
		nullchecks();
		return new RandomSamplingEvolutionaryLearning<S, S>(
			createSpecies(), getInteractionScheme(), getFitnessAggregate(), getRandomSampleFactory(),
			getRandomSampleSize());
	}
}