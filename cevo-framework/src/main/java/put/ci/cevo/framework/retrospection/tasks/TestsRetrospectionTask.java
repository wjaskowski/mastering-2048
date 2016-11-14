package put.ci.cevo.framework.retrospection.tasks;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.retrospection.RetrospectionResult;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.queries.EvolutionQuery;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.Sequence;

import static com.google.common.base.Objects.toStringHelper;

public class TestsRetrospectionTask<T> implements RetrospectionTask {

	private static class TestsFromGenerationQuery<T> implements EvolutionQuery<T> {

		private final int generation;

		public TestsFromGenerationQuery(int generation) {
			this.generation = generation;
		}

		@Override
		public Sequence<EvaluatedIndividual<T>> perform(EvolutionHistory history) {
			EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history);
			return processor.getTestsPopulation(generation);
		}

	}

	private final PerformanceMeasure<T> measure;
	private final int generations;
	private final String name;

	@AccessedViaReflection
	public TestsRetrospectionTask(PerformanceMeasure<T> context, int generations) {
		this(context, generations, TestsRetrospectionTask.class.getSimpleName());
	}

	@AccessedViaReflection
	public TestsRetrospectionTask(PerformanceMeasure<T> context, int generations, String name) {
		this.measure = context;
		this.generations = generations;
		this.name = name;
	}

	@Override
	public RetrospectionResult retrospect(Retrospector retrospector, ThreadedContext context) {
		RetrospectionResult result = new RetrospectionResult();
		for (int generation = 0; generation < generations; generation++) {
			result.offer(retrospector.inquire(new TestsFromGenerationQuery<T>(generation), getContext(), context));
		}
		return result;
	}

	public PerformanceMeasure<T> getContext() {
		return measure;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("query", "TestsRetrospectionTask").add("context", getContext()).toString();
	}

	@Override
	public Description describe() {
		return new Description(name);
	}

}
