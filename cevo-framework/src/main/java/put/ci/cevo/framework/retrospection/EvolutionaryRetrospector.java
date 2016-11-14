package put.ci.cevo.framework.retrospection;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.retrospection.queries.EvolutionQuery;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;
import put.ci.cevo.util.sequence.Sequence;

public class EvolutionaryRetrospector implements Retrospector {

	private final EvolutionHistory history;

	public EvolutionaryRetrospector(EvolutionHistory history) {
		this.history = history;
	}

	@Override
	public <V> Sequence<EvaluatedIndividual<V>> inquire(EvolutionQuery<V> query, final PerformanceMeasure<V> measure,
			ThreadedContext context) {
		return context.invoke(new Worker<EvaluatedIndividual<V>, EvaluatedIndividual<V>>() {
			@Override
			public EvaluatedIndividual<V> process(EvaluatedIndividual<V> subject, ThreadedContext context) {
				double performance = measure.measure(subject.getIndividual(), context).stats().getMean();
				return subject.withObjectiveFitness(performance);
			}
		}, query.perform(history).toList());
	}

}
