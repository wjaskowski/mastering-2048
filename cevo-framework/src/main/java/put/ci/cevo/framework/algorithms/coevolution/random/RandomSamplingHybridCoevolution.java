package put.ci.cevo.framework.algorithms.coevolution.random;

import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.sampling.SamplingHybridEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public final class RandomSamplingHybridCoevolution<S> extends OnePopulationEvolutionaryAlgorithm<S> {

	@AccessedViaReflection
	public RandomSamplingHybridCoevolution(Species<S> species, InteractionScheme<S, S> interaction,
			FitnessAggregate aggregate, PopulationFactory<S> factory, int sampleSize, int populationSampleSize) {
		this(species, new SamplingHybridEvaluator<>(interaction, aggregate, factory, sampleSize, populationSampleSize));
	}

	@AccessedViaReflection
	public RandomSamplingHybridCoevolution(Species<S> species, SamplingHybridEvaluator<S> evaluator) {
		super(species, evaluator);
	}

}
