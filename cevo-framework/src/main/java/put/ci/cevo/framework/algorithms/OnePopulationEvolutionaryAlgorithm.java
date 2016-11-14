package put.ci.cevo.framework.algorithms;

import com.google.common.base.Stopwatch;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.IndividualEvaluator;
import put.ci.cevo.framework.evaluators.OneByOnePopulationEvaluator;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class OnePopulationEvolutionaryAlgorithm<S> extends AbstractGenerationalOptimizationAlgorithm {

	private final Species<S> species;
	private final PopulationEvaluator<S> evaluator;

	private final Stopwatch timer;

	public OnePopulationEvolutionaryAlgorithm(Species<S> species, IndividualEvaluator<S> individualEvaluator) {
		this.species = species;
		this.evaluator = new OneByOnePopulationEvaluator<>(individualEvaluator);
		this.timer = Stopwatch.createUnstarted();
	}

	public OnePopulationEvolutionaryAlgorithm(Species<S> species, PopulationEvaluator<S> evaluator) {
		this.species = species;
		this.evaluator = evaluator;
		this.timer = Stopwatch.createUnstarted();
	}

	@Override
	public void evolve(EvolutionTarget target, final ThreadedContext context) {
		timer.start();

		EvolutionState state = OnePopulationEvolutionState.<S> initialEvolutionState();

		List<S> population = species.createInitialPopulation(context.getRandomForThread());
		state = nextGeneration(state, population, target, context);
		fireNextGenerationEvent(state);

		while (!state.targetAchieved(target)) {
			population = species.evolvePopulation(state.<S> getEvaluatedSolutions(), context);
			state = nextGeneration(state, population, target, context);
			fireNextGenerationEvent(state);
		}
		fireLastGenerationEvent(state);
		timer.stop();
	}

	private EvolutionState nextGeneration(EvolutionState state, List<S> population, EvolutionTarget target,
			ThreadedContext context) {
		int generation = state.getGeneration() + 1;

		EvaluatedPopulation<S> evaluated = evaluator.evaluate(population, generation, context);

		long elapsed = timer.elapsed(MILLISECONDS);

		return new OnePopulationEvolutionState<>(elapsed, generation,
				state.getTotalEffort() + evaluated.getTotalEffort(), evaluated.getPopulation(), target);
	}
}
