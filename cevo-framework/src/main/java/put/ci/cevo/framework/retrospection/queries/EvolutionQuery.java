package put.ci.cevo.framework.retrospection.queries;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.sequence.Sequence;

public interface EvolutionQuery<V> {

	public Sequence<EvaluatedIndividual<V>> perform(EvolutionHistory history);

}
