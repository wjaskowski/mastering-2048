package put.ci.cevo.framework.algorithms.coevolution.random;

import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.sampling.StagedPopulationEvaluator;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public final class RandomSamplingStagedEvolutionaryLearning<S, T> extends OnePopulationEvolutionaryAlgorithm<S> {

	@AccessedViaReflection
	public RandomSamplingStagedEvolutionaryLearning(Species<S> species, StagedPopulationEvaluator<S, T> evaluator) {
		super(species, evaluator);
	}

}
