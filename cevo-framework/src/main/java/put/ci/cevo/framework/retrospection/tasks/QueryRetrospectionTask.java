package put.ci.cevo.framework.retrospection.tasks;

import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.retrospection.RetrospectionResult;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.queries.EvolutionQuery;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;

import static com.google.common.base.Objects.toStringHelper;
import static put.ci.cevo.util.Pair.create;

public class QueryRetrospectionTask<V> implements RetrospectionTask {

	private final EvolutionQuery<V> query;
	private final PerformanceMeasure<V> measure;
	private final String name;

	@AccessedViaReflection
	public QueryRetrospectionTask(PerformanceMeasure<V> context, EvolutionQuery<V> query) {
		this(context, query, QueryRetrospectionTask.class.getSimpleName());
	}

	@AccessedViaReflection
	public QueryRetrospectionTask(PerformanceMeasure<V> context, EvolutionQuery<V> query, String name) {
		this.query = query;
		this.measure = context;
		this.name = name;
	}

	@Override
	public RetrospectionResult retrospect(Retrospector retrospector, ThreadedContext context) {
		return RetrospectionResult.fromMap(retrospector.inquire(getQuery(), getContext(), context)
			.toMultiMap(new Transform<EvaluatedIndividual<V>, Pair<Integer, Double>>() {
				@Override
				public Pair<Integer, Double> transform(EvaluatedIndividual<V> object) {
					return create(object.getGeneration(), object.getFitness());
				}
			}).asMap());

	}

	public EvolutionQuery<V> getQuery() {
		return query;
	}

	public PerformanceMeasure<V> getContext() {
		return measure;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("query", getQuery()).add("context", getContext()).toString();
	}

	@Override
	public Description describe() {
		return new Description(name);
	}

}
