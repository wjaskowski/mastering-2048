package put.ci.cevo.framework.algorithms.multiobjective.nsga2;

import put.ci.cevo.framework.PopulationUtils;
import put.ci.cevo.framework.algorithms.archives.Archive;
import put.ci.cevo.framework.algorithms.archives.SimpleArchive;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static put.ci.cevo.framework.algorithms.multiobjective.MultiobjectiveUtils.*;
import static put.ci.cevo.framework.algorithms.multiobjective.nsga2.NSGA2Fitness.assignFitness;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class NSGA2SamplingEvaluator<S, T> implements PopulationEvaluator<S> {

	private final Archive<EvaluatedIndividual<S>> archive = new SimpleArchive<EvaluatedIndividual<S>>();

	private final InteractionScheme<S, T> scheme;
	private final FitnessAggregate aggregate;
	private final PopulationFactory<T> factory;

	private final int sampleSize;

	@AccessedViaReflection
	public NSGA2SamplingEvaluator(InteractionScheme<S, T> scheme, FitnessAggregate aggregate,
			PopulationFactory<T> factory, int sampleSize) {
		this.scheme = scheme;
		this.aggregate = aggregate;
		this.factory = factory;
		this.sampleSize = sampleSize;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {
		// merge it with the archive
		List<S> individuals = new ArrayList<>(population);
		List<EvaluatedIndividual<S>> archived = archive.getArchived();

		individuals.addAll(seq(archived).map(EvaluatedIndividual.<S> toIndividual()).toList());
		archived.clear();

		// sample tests
		List<T> tests = factory.createPopulation(sampleSize, context.getRandomForThread());

		// evaluate new population
		InteractionTable<S, T> table = scheme.interact(individuals, tests, context);
		Map<S, Fitness> solutionsFitness = aggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		List<EvaluatedIndividual<S>> evaluated = assignFitness(individuals, generation, table.getEfforts(),
			solutionsFitness);

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
