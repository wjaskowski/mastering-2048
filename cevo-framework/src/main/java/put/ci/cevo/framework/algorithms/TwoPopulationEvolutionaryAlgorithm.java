package put.ci.cevo.framework.algorithms;

import com.google.common.base.Stopwatch;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.TwoPopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TwoPopulationEvolutionaryAlgorithm<S, T> extends AbstractGenerationalOptimizationAlgorithm {

	private final Species<S> solutionsSpecies;
	private final Species<T> testsSpecies;

	private final TwoPopulationEvaluator<S, T> evaluator;

	private final Stopwatch timer;

	@AccessedViaReflection
	public TwoPopulationEvolutionaryAlgorithm(Species<S> solutionsSpecies, Species<T> testsSpecies,
			TwoPopulationEvaluator<S, T> evaluator) {
		this.solutionsSpecies = solutionsSpecies;
		this.testsSpecies = testsSpecies;
		this.evaluator = evaluator;
		this.timer = Stopwatch.createUnstarted();
	}

	@Override
	public void evolve(EvolutionTarget target, final ThreadedContext context) {
		timer.start();

		EvolutionState state = TwoPopulationEvolutionState.initialEvolutionState();

		List<S> solutions = solutionsSpecies.createInitialPopulation(context.getRandomForThread());
		List<T> tests = testsSpecies.createInitialPopulation(context.getRandomForThread());

		state = nextEvolutionState(state, solutions, tests, target, context);
		fireNextGenerationEvent(state);

		while (!state.targetAchieved(target)) {
			solutions = solutionsSpecies.evolvePopulation(state.<S>getEvaluatedSolutions(), context);
			tests = testsSpecies.evolvePopulation(state.<T>getEvaluatedTests(), context);
			state = nextEvolutionState(state, solutions, tests, target, context);
			fireNextGenerationEvent(state);
		}
		fireLastGenerationEvent(state);
		timer.stop();

	}

	private TwoPopulationEvolutionState<S, T> nextEvolutionState(EvolutionState state, List<S> solutions, List<T> tests,
			EvolutionTarget target, ThreadedContext context) {
		final int generation = state.getGeneration() + 1;

		Pair<EvaluatedPopulation<S>, EvaluatedPopulation<T>> pair = evaluate(solutions, tests, generation, context);

		EvaluatedPopulation<S> evaluatedSolutions = pair.first();
		EvaluatedPopulation<T> evaluatedTests = pair.second();

		long elapsed = timer.elapsed(MILLISECONDS);

		long totalEffort = state.getTotalEffort() + evaluatedSolutions.getTotalEffort();
		return new TwoPopulationEvolutionState<>(elapsed, generation, totalEffort,
				evaluatedSolutions.getPopulation(), evaluatedTests.getPopulation(), target);
	}

	protected Pair<EvaluatedPopulation<S>, EvaluatedPopulation<T>> evaluate(List<S> solutions, List<T> tests,
			int generation, ThreadedContext context) {
		return evaluator.evaluate(solutions, tests, generation, context);
	}
}