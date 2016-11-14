package put.ci.cevo.framework.algorithms.differential;

import com.google.common.base.Stopwatch;
import put.ci.cevo.framework.PopulationUtils;
import put.ci.cevo.framework.algorithms.AbstractGenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.IndividualEvaluator;
import put.ci.cevo.framework.operators.crossover.CrossoverOperator;
import put.ci.cevo.framework.operators.mutation.differential.PopulationMutation;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;

import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Basic differential evolution optimizer.
 */
//TODO: It is possible to combine this code with the code of OnePopulationEvoluationryAlgorithm. It is not trivial, but
//	possible. Now, e.g., species.evolutionModel is not used
//TODO: Add PopulationEvaluator, so that I could do DifferentialCoevolution. Currently this is not so easy, because of
// how evolvePopulation works here
public final class DifferentialEvolutionAlgorithm<S> extends AbstractGenerationalOptimizationAlgorithm {

	private final Species<S> species;

	private final PopulationMutation<S> mutation;
	private final CrossoverOperator<S> crossover;

	private final IndividualEvaluator<S> evaluator;

	private final Stopwatch timer;

	@AccessedViaReflection
	public DifferentialEvolutionAlgorithm(Species<S> species, PopulationMutation<S> mutation,
			CrossoverOperator<S> crossover, IndividualEvaluator<S> evaluator) {
		this.species = species;
		this.mutation = mutation;
		this.crossover = crossover;
		this.evaluator = evaluator;
		this.timer = Stopwatch.createUnstarted();
	}

	@Override
	public void evolve(EvolutionTarget target, final ThreadedContext context) {
		timer.start();
		EvolutionState state = OnePopulationEvolutionState.<S> initialEvolutionState();

		List<S> population = species.createInitialPopulation(context.getRandomForThread());

		List<EvaluatedIndividual<S>> evaluatedPopulation = evaluatePopulation(population, context);
		state = nextGeneration(state, evaluatedPopulation, target);
		fireNextGenerationEvent(state);

		while (!state.targetAchieved(target)) {
			evaluatedPopulation = evolvePopulation(state.<S> getEvaluatedSolutions(), context);
			state = nextGeneration(state, evaluatedPopulation, target);
			fireNextGenerationEvent(state);
		}

		fireLastGenerationEvent(state);
		timer.stop();
	}

	private List<EvaluatedIndividual<S>> evaluatePopulation(List<S> population, ThreadedContext context) {
		List<EvaluatedIndividual<S>> evaluated = new ArrayList<>(population.size());
		for (S solution : population) {
			evaluated.add(evaluator.evaluate(solution, context));
		}
		return evaluated;
	}

	private EvolutionState nextGeneration(EvolutionState state, List<EvaluatedIndividual<S>> population,
			EvolutionTarget target) {
		int generation = state.getGeneration() + 1;
		long effort = PopulationUtils.sumarizedEffort(population);
		long elapsed = timer.elapsed(MILLISECONDS);

		return new OnePopulationEvolutionState<>(
			elapsed, generation, state.getTotalEffort() + effort, population, target);
	}

	private List<EvaluatedIndividual<S>> evolvePopulation(final List<EvaluatedIndividual<S>> population,
			ThreadedContext context) {
		return context.invoke(new Worker<EvaluatedIndividual<S>, EvaluatedIndividual<S>>() {
			@Override
			public EvaluatedIndividual<S> process(EvaluatedIndividual<S> parent, ThreadedContext context) {
				S mutated = mutation.produce(parent.getIndividual(), population, context.getRandomForThread());
				S offspring = crossover.produce(Pair.create(mutated, parent.getIndividual()), context.getRandomForThread());
				EvaluatedIndividual<S> evaluatedOffspring = evaluator.evaluate(offspring, context);
				if (evaluatedOffspring.getFitness() >= parent.getFitness()) {
					return evaluatedOffspring;
				}
				return parent;
			}
		}, population).toList();

	}

}
