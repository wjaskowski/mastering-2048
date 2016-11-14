package put.ci.cevo.framework.evaluators.sampling;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.StagedPopulationFactory;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.toStringHelper;
import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;

public class StagedPopulationEvaluator<S, T> implements PopulationEvaluator<S> {

	private final InteractionScheme<S, T> scheme;
	private final FitnessAggregate aggregate;
	private final StagedPopulationFactory<S, T> factory;

	private final int sampleSize;

	@AccessedViaReflection
	public StagedPopulationEvaluator(InteractionScheme<S, T> scheme, FitnessAggregate aggregate,
			StagedPopulationFactory<S, T> factory, int sampleSize) {
		this.scheme = scheme;
		this.aggregate = aggregate;
		this.factory = factory;
		this.sampleSize = sampleSize;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {
		List<T> tests = factory.createPopulation(generation, population, sampleSize, context);
		InteractionTable<S, T> table = scheme.interact(population, tests, context);
		Map<S, Fitness> fitness = aggregate.aggregateFitness(table.getSolutionsPayoffs(), context);

		return new EvaluatedPopulation<>(assignFitness(population, generation, table.getEfforts(), fitness));
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("scheme", scheme).add("aggregate", aggregate).add("factory", factory)
			.add("sample", sampleSize).toString();
	}

}
