package put.ci.cevo.framework.retrospection.queries;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.sequence.Sequence;

public class BestOfGenerationTestQuery<T> implements EvolutionQuery<T> {

	@Override
	public Sequence<EvaluatedIndividual<T>> perform(EvolutionHistory history) {
		EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history);
		return processor.bestTestFromEachGeneration();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
