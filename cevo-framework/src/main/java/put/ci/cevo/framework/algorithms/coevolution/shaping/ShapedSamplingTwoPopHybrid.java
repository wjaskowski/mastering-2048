package put.ci.cevo.framework.algorithms.coevolution.shaping;

import put.ci.cevo.framework.algorithms.TwoPopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.sampling.TwoPopulationSamplingEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.ShapedSamplePopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.individuals.loaders.FileIndividualLoader;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.io.IOException;

public final class ShapedSamplingTwoPopHybrid<S, T> extends TwoPopulationEvolutionaryAlgorithm<S, T> {

	@AccessedViaReflection
	public ShapedSamplingTwoPopHybrid(Species<S> solutionsSpecies, Species<T> testsSpecies, FitnessAggregate aggregate,
			InteractionScheme<S, T> scheme, FileIndividualLoader<T> loader, String file, int shapingSampleSize)
			throws IOException {
		this(solutionsSpecies, testsSpecies, aggregate, aggregate, scheme, new ShapedSamplePopulationFactory<>(
			loader, file), shapingSampleSize);
	}

	@AccessedViaReflection
	public ShapedSamplingTwoPopHybrid(Species<S> solutionsSpecies, Species<T> testsSpecies,
			FitnessAggregate solutionsAggregate, FitnessAggregate testsAggregate, InteractionScheme<S, T> scheme,
			PopulationFactory<T> factory, int sampleSize) {
		this(solutionsSpecies, testsSpecies, new TwoPopulationSamplingEvaluator<>(
			solutionsAggregate, testsAggregate, scheme, factory, sampleSize));
	}

	@AccessedViaReflection
	public ShapedSamplingTwoPopHybrid(Species<S> solutionsSpecies, Species<T> testsSpecies,
			TwoPopulationSamplingEvaluator<S, T> evaluator) {
		super(solutionsSpecies, testsSpecies, evaluator);
	}
}
