package put.ci.cevo.framework.algorithms.multiobjective.nsga2;

import put.ci.cevo.framework.PopulationUtils;
import put.ci.cevo.framework.algorithms.archives.Archive;
import put.ci.cevo.framework.algorithms.archives.SimpleArchive;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static put.ci.cevo.framework.algorithms.multiobjective.MultiobjectiveUtils.*;
import static put.ci.cevo.framework.algorithms.multiobjective.nsga2.NSGA2Fitness.assignFitness;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class NSGA2PopulationEvaluator<S> implements PopulationEvaluator<S> {

	private final Archive<EvaluatedIndividual<S>> archive;

	private final InteractionScheme<S, S> scheme;
	private final FitnessAggregate aggregate;

	public NSGA2PopulationEvaluator(InteractionScheme<S, S> scheme, FitnessAggregate aggregate) {
		this(scheme, aggregate, new SimpleArchive<EvaluatedIndividual<S>>());
	}

	public NSGA2PopulationEvaluator(InteractionScheme<S, S> scheme, FitnessAggregate aggregate,
			Archive<EvaluatedIndividual<S>> archive) {
		this.scheme = scheme;
		this.archive = archive;
		this.aggregate = aggregate;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {
		// merge it with the archive
		List<S> indviduals = new ArrayList<>(population);
		List<EvaluatedIndividual<S>> archived = archive.getArchived();

		indviduals.addAll(seq(archived).map(EvaluatedIndividual.<S> toIndividual()).toList());
		archived.clear();

		// evaluate new population
		InteractionTable<S, S> table = scheme.interact(indviduals, indviduals, context);
		Map<S, Fitness> fitness = aggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		List<EvaluatedIndividual<S>> evaluated = assignFitness(indviduals, generation, table.getEfforts(), fitness);

		// compute pareto front ranks
		for (List<EvaluatedIndividual<S>> front : assignRanks(evaluated)) {
			assignSparsity(front);
			if (archived.size() + front.size() > population.size()) {
				archived.addAll(sparsest(front, population.size() - archive.size()));
				break;
			}
			archived.addAll(front);
		}
		return new EvaluatedPopulation<>(archived, PopulationUtils.sumarizedEffort(evaluated));
	}

}
