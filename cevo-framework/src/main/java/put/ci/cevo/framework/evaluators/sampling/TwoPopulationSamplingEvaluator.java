package put.ci.cevo.framework.evaluators.sampling;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections15.ListUtils.union;
import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;
import static put.ci.cevo.util.Pair.create;

/**
 * A candidate solution is evaluated against all tests from the second population + set of tests from the factory
 */
public class TwoPopulationSamplingEvaluator<S, T> implements TwoPopulationEvaluator<S, T> {

	private final FitnessAggregate solutionsAggregate;
	private final FitnessAggregate testsAggregate;

	private final InteractionScheme<S, T> scheme;

	private final PopulationFactory<T> factory;
	private final int sampleSize;

	@AccessedViaReflection
	public TwoPopulationSamplingEvaluator(FitnessAggregate aggregate, InteractionScheme<S, T> scheme,
			PopulationFactory<T> factory, int sampleSize) {
		this(aggregate, aggregate, scheme, factory, sampleSize);
	}

	@AccessedViaReflection
	public TwoPopulationSamplingEvaluator(FitnessAggregate solutionsAggregate, FitnessAggregate testsAggregate,
			InteractionScheme<S, T> scheme, PopulationFactory<T> factory, int sampleSize) {
		this.solutionsAggregate = solutionsAggregate;
		this.testsAggregate = testsAggregate;
		this.scheme = scheme;
		this.factory = factory;
		this.sampleSize = sampleSize;
	}

	@Override
	public Pair<EvaluatedPopulation<S>, EvaluatedPopulation<T>> evaluate(List<S> solutions, List<T> tests,
			int generation, ThreadedContext context) {
		List<T> distributionSample = factory.createPopulation(sampleSize, context.getRandomForThread());
		InteractionTable<S, T> table = scheme.interact(solutions, union(tests, distributionSample), context);

		Map<S, Fitness> solutionsFitness = solutionsAggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
		EvaluatedPopulation<S> evaluatedSolutions = new EvaluatedPopulation<>(assignFitness(solutions, generation,
				table.getEfforts(), solutionsFitness));

		Map<T, Fitness> testsFitness = testsAggregate.aggregateFitness(table.getTestsPayoffs(), context);
		EvaluatedPopulation<T> evaluatedTests = new EvaluatedPopulation<>(assignFitness(tests, generation,
				table.getEfforts().transpose(), testsFitness));

		return create(evaluatedSolutions, evaluatedTests);
	}

}
