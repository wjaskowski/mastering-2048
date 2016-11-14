package put.ci.cevo.framework.algorithms.stateful;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.evaluators.sampling.GenotypicSamplingEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

public class StatefulRSEL<T> implements StatefulEvolutionaryAlgorithm<T> {

	private final Species<T> species;
	private final PopulationEvaluator<T> evaluator;

	private List<T> population;

	private int generation = Integer.MIN_VALUE;
	private boolean populationWasInitialized;

	public StatefulRSEL(Species<T> species, InteractionScheme<T, T> scheme, FitnessAggregate aggregate,
			PopulationFactory<T> factory, int sampleSize) {
		this.species = species;
		this.evaluator = new GenotypicSamplingEvaluator<>(scheme, aggregate, factory, sampleSize);

		this.populationWasInitialized = false;
	}

	@Override
	public void initializePopulation(RandomDataGenerator random) {
		population = species.createInitialPopulation(random);
		generation = 0;
		populationWasInitialized = true;
	}

	@Override
	public void nextEvolutionStep(ThreadedContext context) {
		if (!populationWasInitialized) {
			throw new IllegalStateException("Call initializePopulation() first");
		}
		evaluateAndEvolvePopulation(context);
		generation++;
	}

	@Override
	public List<T> getCurrentPopulation() {
		if (!populationWasInitialized) {
			throw new IllegalStateException("Call initializePopulation() first");
		}
		return ImmutableList.copyOf(population);
	}

	private void evaluateAndEvolvePopulation(ThreadedContext context) {
		List<EvaluatedIndividual<T>> evaluatedPopulation = evaluatePopulation(population, context);
		population = species.evolvePopulation(evaluatedPopulation, context);
	}

	private List<EvaluatedIndividual<T>> evaluatePopulation(List<T> individuals, ThreadedContext context) {
		return evaluator.evaluate(individuals, generation, context).getPopulation();
	}
}
