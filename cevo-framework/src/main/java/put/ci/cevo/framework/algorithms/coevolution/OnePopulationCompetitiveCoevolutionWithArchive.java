package put.ci.cevo.framework.algorithms.coevolution;

import com.google.common.base.Stopwatch;
import put.ci.cevo.framework.PopulationUtils;
import put.ci.cevo.framework.algorithms.AbstractGenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.archives.Archive;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transforms;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static put.ci.cevo.util.sequence.Sequences.seq;

/**
 * In addition to an evolution state maintains an archive which is used as a kind-of-second population, to evaluate
 * the first population (that is why we have here TwoPopulationEvaluator instead of PopulationEvaluator)
 * Note: archive is actually also part of the evolution state, but to it nicely I would have to make EvolutionState
 * a generic parameter.
 */
public class OnePopulationCompetitiveCoevolutionWithArchive<S> extends AbstractGenerationalOptimizationAlgorithm {

	private final Species<S> species;
	private final TwoPopulationEvaluator<S, S> evaluator;
	private final Archive<S> archive;

	private final Stopwatch timer;

	//Warning: archive has state
	public OnePopulationCompetitiveCoevolutionWithArchive(Species<S> species, TwoPopulationEvaluator<S, S> evaluator,
			Archive<S> archive) {
		this.species = species;
		this.evaluator = evaluator;
		this.archive = archive;
		this.timer = Stopwatch.createUnstarted();
	}

	@Override
	public void evolve(EvolutionTarget target, ThreadedContext context) {
		timer.start();

		// The second population is the archive
		EvolutionState state = OnePopulationEvolutionState.<S>initialEvolutionState();

		List<S> population = species.createInitialPopulation(context.getRandomForThread());
		state = nextGeneration(state, population, target, context);
		fireNextGenerationEvent(state);

		while (!state.targetAchieved(target)) {
			population = species.evolvePopulation(state.<S>getEvaluatedSolutions(), context);
			state = nextGeneration(state, population, target, context);
			fireNextGenerationEvent(state);
		}
		fireLastGenerationEvent(state);
		timer.stop();
	}

	private EvolutionState nextGeneration(EvolutionState state, List<S> population, EvolutionTarget target,
			ThreadedContext context) {
		int generation = state.getGeneration() + 1;

		Pair<EvaluatedPopulation<S>, EvaluatedPopulation<S>> evaluated = evaluator.evaluate(
				population, archive.getArchived(), generation, context);

		EvaluatedPopulation<S> evaluatedPopulation = evaluated.first();

		EvaluatedIndividual<S> bestSolution = seq(evaluatedPopulation.getPopulation()).reduce(Transforms.<EvaluatedIndividual<S>>max());

		archive.submit(bestSolution.getIndividual());

		long effort = PopulationUtils.sumarizedEffort(evaluatedPopulation.getPopulation());
		long elapsed = timer.elapsed(MILLISECONDS);

		return new OnePopulationEvolutionState<>(
				elapsed, generation, state.getTotalEffort() + effort, evaluatedPopulation.getPopulation(), target);
	}
}
