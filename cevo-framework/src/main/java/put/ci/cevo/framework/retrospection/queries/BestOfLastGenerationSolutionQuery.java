package put.ci.cevo.framework.retrospection.queries;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.sequence.Sequence;

import static put.ci.cevo.util.sequence.Sequences.seq;

public class BestOfLastGenerationSolutionQuery<S> implements EvolutionQuery<S> {

	/** Returns just one individual: the subjectively best solution of the last generation */
	@Override
	public Sequence<EvaluatedIndividual<S>> perform(EvolutionHistory history) {
		EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history);
		return seq(processor.<S> bestSolutionOfLastGeneration());
	}
}
