package put.ci.cevo.framework.algorithms.coevolution.random;

import put.ci.cevo.framework.algorithms.TwoPopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.sampling.TwoPopulationSamplingEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public final class RandomSamplingTwoPopHybrid<S, T> extends TwoPopulationEvolutionaryAlgorithm<S, T> {

	@AccessedViaReflection
	public RandomSamplingTwoPopHybrid(Species<S> solutionsSpecies, Species<T> testsSpecies, FitnessAggregate aggregate,
			InteractionScheme<S, T> interaction, PopulationFactory<T> factory, int sampleSize) {
		this(solutionsSpecies, testsSpecies, aggregate, aggregate, interaction, factory, sampleSize);
	}

	@AccessedViaReflection
	public RandomSamplingTwoPopHybrid(Species<S> solutionsSpecies, Species<T> testsSpecies,
			FitnessAggregate solutionsAggregate, FitnessAggregate testsAggregate, InteractionScheme<S, T> interaction,
			PopulationFactory<T> factory, int sampleSize) {
		this(solutionsSpecies, testsSpecies, new TwoPopulationSamplingEvaluator<>(
			solutionsAggregate, testsAggregate, interaction, factory, sampleSize));
	}

	@AccessedViaReflection
	public RandomSamplingTwoPopHybrid(Species<S> solutionsSpecies, Species<T> testsSpecies,
			TwoPopulationSamplingEvaluator<S, T> evaluator) {
		super(solutionsSpecies, testsSpecies, evaluator);
	}
}
