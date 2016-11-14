package put.ci.cevo.framework.retrospection;

import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.retrospection.queries.EvolutionQuery;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.Sequence;

public interface Retrospector {

	public <V> Sequence<EvaluatedIndividual<V>> inquire(EvolutionQuery<V> query, PerformanceMeasure<V> measure,
			ThreadedContext context);

}
