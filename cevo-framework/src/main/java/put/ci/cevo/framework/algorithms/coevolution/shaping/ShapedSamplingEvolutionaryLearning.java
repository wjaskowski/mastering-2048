package put.ci.cevo.framework.algorithms.coevolution.shaping;

import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.sampling.GenotypicSamplingEvaluator;
import put.ci.cevo.framework.factories.ShapedSamplePopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.individuals.loaders.FileIndividualLoader;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.io.IOException;
import java.util.List;

public final class ShapedSamplingEvolutionaryLearning<S, T> extends OnePopulationEvolutionaryAlgorithm<S> {

	@AccessedViaReflection
	public ShapedSamplingEvolutionaryLearning(Species<S> species, InteractionScheme<S, T> scheme,
			FitnessAggregate aggregate, FileIndividualLoader<T> loader, String file, int shapingSampleSize)
			throws IOException {
		this(species, new GenotypicSamplingEvaluator<>(
			scheme, aggregate, new ShapedSamplePopulationFactory<>(loader, file), shapingSampleSize));
	}

	@AccessedViaReflection
	public ShapedSamplingEvolutionaryLearning(Species<S> species, InteractionScheme<S, T> scheme,
			FitnessAggregate aggregate, List<T> pool, int shapingSampleSize) {
		this(species, new GenotypicSamplingEvaluator<>(
			scheme, aggregate, new ShapedSamplePopulationFactory<>(pool), shapingSampleSize));
	}

	@AccessedViaReflection
	public ShapedSamplingEvolutionaryLearning(Species<S> species, GenotypicSamplingEvaluator<S, T> evaluator) {
		super(species, evaluator);
	}

}
