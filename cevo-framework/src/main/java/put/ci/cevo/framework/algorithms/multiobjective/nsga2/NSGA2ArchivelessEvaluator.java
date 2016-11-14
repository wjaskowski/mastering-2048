package put.ci.cevo.framework.algorithms.multiobjective.nsga2;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;
import java.util.Map;

import static put.ci.cevo.framework.algorithms.multiobjective.MultiobjectiveUtils.assignRanks;
import static put.ci.cevo.framework.algorithms.multiobjective.MultiobjectiveUtils.assignSparsity;
import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;

public class NSGA2ArchivelessEvaluator<S> implements PopulationEvaluator<S> {

	private final InteractionScheme<S, S> scheme;
	private final FitnessAggregate aggregate;

	public NSGA2ArchivelessEvaluator(InteractionScheme<S, S> scheme, FitnessAggregate aggregate) {
		this.scheme = scheme;
		this.aggregate = aggregate;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {
		InteractionTable<S, S> table = scheme.interact(population, population, context);

		Map<S, Fitness> fitness = aggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		List<EvaluatedIndividual<S>> evaluated = assignFitness(population, generation, table.getEfforts(), fitness);

		for (List<EvaluatedIndividual<S>> front : assignRanks(evaluated)) {
			assignSparsity(front);
		}

		return new EvaluatedPopulation<>(evaluated);
	}

}
