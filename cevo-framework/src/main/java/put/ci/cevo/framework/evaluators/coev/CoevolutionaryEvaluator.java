package put.ci.cevo.framework.evaluators.coev;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
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

/**
 * @deprecated Because {@link put.ci.cevo.framework.algorithms.coevolution.OnePopulationCompetitiveCoevolution} is deprecated
 */

@Deprecated
public class CoevolutionaryEvaluator<S> implements PopulationEvaluator<S> {

	private final InteractionScheme<S, S> scheme;
	private final FitnessAggregate aggregate;

	@AccessedViaReflection
	public CoevolutionaryEvaluator(InteractionScheme<S, S> scheme, FitnessAggregate aggregate) {
		this.scheme = scheme;
		this.aggregate = aggregate;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation,
			ThreadedContext context) {
		InteractionTable<S, S> table = scheme.interact(population, population, context);
		Map<S, Fitness> fitness = getAggregate().aggregateFitness(table.getSolutionsPayoffs(), context);
		return new EvaluatedPopulation<>(assignFitness(population, generation, table.getEfforts(), fitness));
	}

	public FitnessAggregate getAggregate() {
		return aggregate;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("scheme", scheme).add("aggregate", aggregate).toString();
	}
}
