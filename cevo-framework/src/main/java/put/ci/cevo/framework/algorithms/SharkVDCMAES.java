package put.ci.cevo.framework.algorithms;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Logger;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.vectors.DoubleVector;

@AccessedViaReflection
public class SharkVDCMAES<S> extends AbstractGenerationalOptimizationAlgorithm {
	private final Logger logger = Logger.getLogger(SharkVDCMAES.class);

	private final PopulationEvaluator<S> populationEvaluator;
	private final S initialGuess;
	private final IndividualAdapter<S, DoubleVector> doubleVectorAdapter;
	private final int populationSize;
	private final double sigma;
	private final Stopwatch timer;

	/**
	 * @param initialSigma Initial standard deviation (kind of mutation)
	 */
	public SharkVDCMAES(int populationSize, PopulationEvaluator<S> populationEvaluator, S initialGuess,
			IndividualAdapter<S, DoubleVector> doubleVectorAdapter, double initialSigma) {
		this.populationEvaluator = populationEvaluator;
		this.initialGuess = initialGuess;
		this.doubleVectorAdapter = doubleVectorAdapter;
		this.populationSize = populationSize;
		this.sigma = initialSigma;
		this.timer = Stopwatch.createUnstarted();
	}

	@Override
	public void evolve(EvolutionTarget target, ThreadedContext context) {
		timer.start();

		EvolutionState state = OnePopulationEvolutionState.initialEvolutionState();

		SharkVDCMAESInternal shark = new SharkVDCMAESInternal(doubleVectorAdapter.from(initialGuess).toArray(),
				populationSize, SharkVDCMAESInternal.suggestMu(populationSize), sigma, context.getRandomForThread());

		while (!target.isAchieved(state)) {
			Stopwatch stopwatch = Stopwatch.createStarted();

			List<SharkVDCMAESInternal.Individual> offspring = shark.createSamples(context.getRandomForThread(), populationSize);

			double elapsedBreeding = stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0;
			stopwatch.reset().start();

			EvaluatedPopulation<S> evaluated = populationEvaluator.evaluate(populationFromSharkIndividuals(offspring),
					state.getGeneration(), context);

			double elapsedEvaluation = stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0;
			stopwatch.reset().start();

			shark.nextGeneration(offspring, getNegatedFitness(evaluated));

			double elapsedUpdate = stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0;
			stopwatch.reset().start();

			state = nextGeneration(state, evaluated, target);
			fireNextGenerationEvent(state);

			double elapsedListener = stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0;

			logger.info(String.format("Breeding: %.2f, Eval: %.2f, Update: %.2f, Listener: %.2f", elapsedBreeding,
					elapsedEvaluation, elapsedUpdate, elapsedListener));
		}
		fireLastGenerationEvent(state);
		timer.stop();
	}

	private List<S> populationFromSharkIndividuals(List<SharkVDCMAESInternal.Individual> individuals) {
		return individuals.stream().map(ind -> doubleVectorAdapter.from(new DoubleVector(DoubleVector.of(
				ind.searchPoint().toArray())), initialGuess)).collect(Collectors.toList());

	}

	private List<Double> getNegatedFitness(EvaluatedPopulation<S> evaluatedPopulation) {
		return evaluatedPopulation.getPopulation().stream().map(i -> -i.getFitness()).collect(Collectors.toList());
	}

	private EvolutionState nextGeneration(EvolutionState state, EvaluatedPopulation<S> population,
			EvolutionTarget target) {

		int generation = state.getGeneration() + 1;
		long elapsed = timer.elapsed(MILLISECONDS);
		return new OnePopulationEvolutionState<>(elapsed, generation,
				state.getTotalEffort() + population.getTotalEffort(), population.getPopulation(), target);
	}
}
