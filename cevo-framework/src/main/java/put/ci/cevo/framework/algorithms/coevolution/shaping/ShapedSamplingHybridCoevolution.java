package put.ci.cevo.framework.algorithms.coevolution.shaping;

import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.sampling.SamplingHybridEvaluator;
import put.ci.cevo.framework.factories.ShapedSamplePopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.individuals.loaders.FileIndividualLoader;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.io.IOException;
import java.util.List;

public final class ShapedSamplingHybridCoevolution<S> extends OnePopulationEvolutionaryAlgorithm<S> {

	@AccessedViaReflection
	public ShapedSamplingHybridCoevolution(Species<S> species, InteractionScheme<S, S> scheme,
			FitnessAggregate aggregate, FileIndividualLoader<S> loader, String file, int shapingSampleSize,
			int populationSampleSize) throws IOException {
		this(species, new SamplingHybridEvaluator<>(
			scheme, aggregate, new ShapedSamplePopulationFactory<>(loader, file), shapingSampleSize,
			populationSampleSize));
	}

	@AccessedViaReflection
	public ShapedSamplingHybridCoevolution(Species<S> species, InteractionScheme<S, S> scheme,
			FitnessAggregate aggregate, List<S> pool, int shapingSampleSize, int populationSampleSize) {
		this(species, new SamplingHybridEvaluator<>(
			scheme, aggregate, new ShapedSamplePopulationFactory<>(pool), shapingSampleSize, populationSampleSize));
	}

	@AccessedViaReflection
	public ShapedSamplingHybridCoevolution(Species<S> species, SamplingHybridEvaluator<S> evaluator) {
		super(species, evaluator);
	}

}
