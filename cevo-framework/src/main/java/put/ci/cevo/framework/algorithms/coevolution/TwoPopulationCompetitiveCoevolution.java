package put.ci.cevo.framework.algorithms.coevolution;

import put.ci.cevo.framework.algorithms.TwoPopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.coev.TwoPopulationCoevolutionaryEvaluator;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class TwoPopulationCompetitiveCoevolution<S, T> extends TwoPopulationEvolutionaryAlgorithm<S, T> {

	@AccessedViaReflection
	public TwoPopulationCompetitiveCoevolution(Species<S> solutionsSpecies, Species<T> testsSpecies,
			FitnessAggregate aggregate, InteractionScheme<S, T> interaction) {
		this(solutionsSpecies, testsSpecies, new TwoPopulationCoevolutionaryEvaluator<>(aggregate, interaction));
	}

	@AccessedViaReflection
	public TwoPopulationCompetitiveCoevolution(Species<S> solutionsSpecies, Species<T> testsSpecies,
			FitnessAggregate solutionsAggregate, FitnessAggregate testsAggregate, InteractionScheme<S, T> interaction) {
		this(solutionsSpecies, testsSpecies, new TwoPopulationCoevolutionaryEvaluator<>(
			solutionsAggregate, testsAggregate, interaction));
	}

	@AccessedViaReflection
	public TwoPopulationCompetitiveCoevolution(Species<S> solutionsSpecies, Species<T> testsSpecies,
			TwoPopulationCoevolutionaryEvaluator<S, T> evaluator) {
		super(solutionsSpecies, testsSpecies, evaluator);
	}
}