package put.ci.cevo.framework.algorithms.coevolution;

import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.coev.CoevolutionaryEvaluator;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * @deprecated Use {@link OnePopulationEvolutionaryAlgorithm} with
 * 				{@link put.ci.cevo.framework.evaluators.coev.OnePopulationCoevolutionEvaluator}
 * 			or other appropriate population evaluator instead
 */

@Deprecated
public class OnePopulationCompetitiveCoevolution<S> extends OnePopulationEvolutionaryAlgorithm<S> {

	@AccessedViaReflection
	public OnePopulationCompetitiveCoevolution(Species<S> species, InteractionScheme<S, S> interaction,
			FitnessAggregate aggregate) {
		this(species, new CoevolutionaryEvaluator<>(interaction, aggregate));
	}

	@AccessedViaReflection
	public OnePopulationCompetitiveCoevolution(Species<S> species, CoevolutionaryEvaluator<S> evaluator) {
		super(species, evaluator);
	}

}
