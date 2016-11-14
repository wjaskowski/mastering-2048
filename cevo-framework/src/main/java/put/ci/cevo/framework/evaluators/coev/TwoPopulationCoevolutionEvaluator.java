package put.ci.cevo.framework.evaluators.coev;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.PhenotypeMappingMachine;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;
import java.util.Map;

import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;

/**
 * Evaluates a populations in two-populations coevolution setting
 */
public class TwoPopulationCoevolutionEvaluator<S, T, X, Y> implements TwoPopulationEvaluator<S, T> {
	private final GenotypePhenotypeMapper<S, X> solutionsMapper;
	private final GenotypePhenotypeMapper<T, Y> testsMapper;
	private final FitnessAggregate fitnessAggregate;
	private final InteractionScheme<X, Y> interactionScheme;

	@AccessedViaReflection
	public TwoPopulationCoevolutionEvaluator(InteractionScheme<X, Y> interactionScheme,
			FitnessAggregate fitnessAggregate, GenotypePhenotypeMapper<S, X> solutionsMapper,
			GenotypePhenotypeMapper<T, Y> testsMapper) {
		this.solutionsMapper = solutionsMapper;
		this.testsMapper = testsMapper;
		this.fitnessAggregate = fitnessAggregate;
		this.interactionScheme = interactionScheme;
	}

	/**
	 * With default {@link put.ci.cevo.framework.fitness.SimpleAverageFitness} and {@link
	 * put.ci.cevo.framework.interactions.RoundRobinInteractionScheme}
	 */
	@AccessedViaReflection
	public TwoPopulationCoevolutionEvaluator(InteractionDomain<X, Y> domain,
			GenotypePhenotypeMapper<S, X> solutionsMapper,
			GenotypePhenotypeMapper<T, Y> testsMapper) {
		this(new RoundRobinInteractionScheme<>(domain), new SimpleAverageFitness(), solutionsMapper, testsMapper);
	}

	@Override
	public Pair<EvaluatedPopulation<S>, EvaluatedPopulation<T>> evaluate(List<S> solutions, List<T> tests,
			int generation, ThreadedContext context) {
		PhenotypeMappingMachine<S, X> solutionsMachine = new PhenotypeMappingMachine<>(solutions, solutionsMapper,
				context);
		PhenotypeMappingMachine<T, Y> testsMachine = new PhenotypeMappingMachine<>(tests, testsMapper, context);

		InteractionTable<X, Y> table = interactionScheme.interact(solutionsMachine.phenotypes(),
				testsMachine.phenotypes(), context);

		Map<X, Fitness> solutionsFitness = fitnessAggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		Map<Y, Fitness> testsFitness = fitnessAggregate.aggregateFitness(table.getTestsPayoffs(), context);

		EvaluatedPopulation<S> evaluatedSolutions = solutionsMachine.getEvaluatedGenotypes(assignFitness(
				solutionsMachine.phenotypes(), generation, table.getEfforts(), solutionsFitness));

		EvaluatedPopulation<T> evaluatedTests = testsMachine.getEvaluatedGenotypes(assignFitness(
				testsMachine.phenotypes(), generation, table.getEfforts().transpose(), testsFitness));

		return Pair.create(evaluatedSolutions, evaluatedTests);
	}
}
