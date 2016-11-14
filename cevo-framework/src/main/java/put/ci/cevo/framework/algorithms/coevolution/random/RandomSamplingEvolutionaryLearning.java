package put.ci.cevo.framework.algorithms.coevolution.random;

import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.sampling.GenotypicSamplingEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public final class RandomSamplingEvolutionaryLearning<S, T> extends OnePopulationEvolutionaryAlgorithm<S> {

	@AccessedViaReflection
	public RandomSamplingEvolutionaryLearning(Species<S> species, InteractionScheme<S, T> interaction,
			FitnessAggregate aggregate, PopulationFactory<T> randomSampleFactory, int randomSampleSize) {
		this(species, new GenotypicSamplingEvaluator<>(interaction, aggregate, randomSampleFactory, randomSampleSize));
	}

	@AccessedViaReflection
	public RandomSamplingEvolutionaryLearning(Species<S> species, GenotypicSamplingEvaluator<S, T> evaluator) {
		super(species, evaluator);
	}

}