package put.ci.cevo.framework.retrospection.queries;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.sequence.Sequence;

public class BestOfEveryKGenerationSolutionQuery<S> implements EvolutionQuery<S> {

	private final int k;

	@AccessedViaReflection
	public BestOfEveryKGenerationSolutionQuery(int k) {
		this.k = k;
	}

	@Override
	public Sequence<EvaluatedIndividual<S>> perform(EvolutionHistory history) {
		EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history.getEvolutionHistory());
		Sequence<EvaluatedIndividual<S>> individuals = processor.bestSolutionFromEveryKGeneration(k);
		return individuals.add(processor.<S> bestSolutionOfLastGeneration());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
