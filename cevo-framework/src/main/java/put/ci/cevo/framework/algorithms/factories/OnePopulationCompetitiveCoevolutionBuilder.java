package put.ci.cevo.framework.algorithms.factories;

import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.coevolution.OnePopulationCompetitiveCoevolution;

@Build(target = OnePopulationCompetitiveCoevolution.class)
public class OnePopulationCompetitiveCoevolutionBuilder<S> extends
		GenerationalAlgorithmBuilder<S, S, OnePopulationCompetitiveCoevolutionBuilder<S>> {

	@Override
	protected OnePopulationCompetitiveCoevolutionBuilder<S> getBuilder() {
		return this;
	}

	@Override
	public GenerationalOptimizationAlgorithm build() {
		nullchecks();
		return new OnePopulationCompetitiveCoevolution<>(createSpecies(), getInteractionScheme(), getFitnessAggregate());
	}
}
