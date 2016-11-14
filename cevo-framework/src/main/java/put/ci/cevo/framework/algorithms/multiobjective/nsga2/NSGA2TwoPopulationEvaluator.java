package put.ci.cevo.framework.algorithms.multiobjective.nsga2;

import put.ci.cevo.framework.PopulationUtils;
import put.ci.cevo.framework.algorithms.archives.Archive;
import put.ci.cevo.framework.algorithms.archives.SimpleArchive;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.EvaluatorUtils;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static put.ci.cevo.framework.algorithms.multiobjective.MultiobjectiveUtils.*;
import static put.ci.cevo.framework.algorithms.multiobjective.nsga2.NSGA2Fitness.assignFitness;
import static put.ci.cevo.util.Pair.create;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class NSGA2TwoPopulationEvaluator<S, T> implements TwoPopulationEvaluator<S, T> {

	private final Archive<EvaluatedIndividual<S>> archive;

	private final InteractionScheme<S, T> scheme;

	private final FitnessAggregate solutionsAggregate;
	private final FitnessAggregate testsAggregate;

	public NSGA2TwoPopulationEvaluator(InteractionScheme<S, T> scheme, FitnessAggregate aggregate,
			FitnessAggregate testsAggregate) {
		this(scheme, aggregate, testsAggregate, new SimpleArchive<EvaluatedIndividual<S>>());
	}

	public NSGA2TwoPopulationEvaluator(InteractionScheme<S, T> scheme, FitnessAggregate aggregate,
			FitnessAggregate testsAggregate, Archive<EvaluatedIndividual<S>> archive) {
		this.scheme = scheme;
		this.solutionsAggregate = aggregate;
		this.testsAggregate = testsAggregate;
		this.archive = archive;
	}

	@Override
	public Pair<EvaluatedPopulation<S>, EvaluatedPopulation<T>> evaluate(List<S> solutions, List<T> tests,
			int generation, ThreadedContext context) {
		// merge it with the archive
		List<S> individuals = new ArrayList<>(solutions);
		List<EvaluatedIndividual<S>> archived = archive.getArchived();

		individuals.addAll(seq(archived).map(EvaluatedIndividual.<S>toIndividual()).toList());
		archived.clear();

		// evaluate new population
		InteractionTable<S, T> table = scheme.interact(individuals, tests, context);
		Map<S, Fitness> solutionsFitness = solutionsAggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		List<EvaluatedIndividual<S>> evaluated = assignFitness(individuals, generation, table.getEfforts(),
				solutionsFitness);

		// compute pareto front ranks TODO: I should compute ranks and sparsity *before* assigning fitness. In this way I can make the implementation prettier.
		for (List<EvaluatedIndividual<S>> front : assignRanks(evaluated)) {
			assignSparsity(front);
			if (archived.size() + front.size() > solutions.size()) {
				archived.addAll(sparsest(front, solutions.size() - archive.size()));
				break;
			}
			archived.addAll(front);
		}

		EvaluatedPopulation<S> evaluatedPopulation = new EvaluatedPopulation<>(archived,
				PopulationUtils.sumarizedEffort(evaluated));

		Map<T, Fitness> testsFitness = testsAggregate.aggregateFitness(table.getTestsPayoffs(), context);
		EvaluatedPopulation<T> evaluatedTests = new EvaluatedPopulation<>(
				EvaluatorUtils.assignFitness(tests, generation, table.getEfforts().transpose(), testsFitness));

		return create(evaluatedPopulation, evaluatedTests);
	}

}
