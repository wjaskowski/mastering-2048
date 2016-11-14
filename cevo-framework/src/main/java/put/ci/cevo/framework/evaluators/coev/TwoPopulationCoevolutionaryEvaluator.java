package put.ci.cevo.framework.evaluators.coev;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;
import java.util.Map;

import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;

/**
 * @deprecated Because defined on genotypes. Use {@link TwoPopulationCoevolutionEvaluator}
 */
@Deprecated
public class TwoPopulationCoevolutionaryEvaluator<S, T> implements TwoPopulationEvaluator<S, T> {

	private final FitnessAggregate solutionsAggregate;
	private final FitnessAggregate testsAggregate;

	private final InteractionScheme<S, T> scheme;

	@AccessedViaReflection
	public TwoPopulationCoevolutionaryEvaluator(FitnessAggregate aggregate, InteractionScheme<S, T> scheme) {
		this(aggregate, aggregate, scheme);
	}

	@AccessedViaReflection
	public TwoPopulationCoevolutionaryEvaluator(FitnessAggregate solutionsAggregate, FitnessAggregate testsAggregate,
			InteractionScheme<S, T> scheme) {
		this.solutionsAggregate = solutionsAggregate;
		this.testsAggregate = testsAggregate;
		this.scheme = scheme;
	}

	@Override
	public Pair<EvaluatedPopulation<S>, EvaluatedPopulation<T>> evaluate(List<S> solutions, List<T> tests,
			int generation, ThreadedContext context) {
		InteractionTable<S, T> table = scheme.interact(solutions, tests, context);

		Map<S, Fitness> solutionsFitness = solutionsAggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		EvaluatedPopulation<S> evaluatedSolutions = new EvaluatedPopulation<>(assignFitness(solutions, generation,
				table.getEfforts(), solutionsFitness));

		//TODO: assigneFitness is overly-complicated because of map
		Map<T, Fitness> testsFitness = testsAggregate.aggregateFitness(table.getTestsPayoffs(), context);
		EvaluatedPopulation<T> evaluatedTests = new EvaluatedPopulation<>(assignFitness(tests, generation,
				table.getEfforts().transpose(), testsFitness));

		//TODO: Hmm. Actually, giving totalEffort both to test population and solution population is weird.
		return Pair.create(evaluatedSolutions, evaluatedTests);
	}

}
