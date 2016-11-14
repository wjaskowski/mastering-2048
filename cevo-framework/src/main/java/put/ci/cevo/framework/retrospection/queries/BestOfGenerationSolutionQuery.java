package put.ci.cevo.framework.retrospection.queries;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.sequence.Sequence;

public class BestOfGenerationSolutionQuery<S> implements EvolutionQuery<S> {

	@Override
	public Sequence<EvaluatedIndividual<S>> perform(EvolutionHistory history) {
		EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history);
		return processor.bestSolutionFromEachGeneration();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
