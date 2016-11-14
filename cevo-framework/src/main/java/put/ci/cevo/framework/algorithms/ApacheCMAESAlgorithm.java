package put.ci.cevo.framework.algorithms;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.vectors.DoubleVector;

@AccessedViaReflection
public class ApacheCMAESAlgorithm<S> extends AbstractGenerationalOptimizationAlgorithm
		implements ApacheCMAES.NextIteractionListener {

	public static final double DEFAULT_SIGMA = 1.0;
	public static final boolean DEFAULT_ONLY_DIAGONAL_COVARIANCE_MATRIX = false;

	private final PopulationEvaluator<S> populationEvaluator;
	private final S initialGuess;
	private final IndividualAdapter<S, DoubleVector> doubleVectorAdapter;
	private final int populationSize;
	private final double sigma;

	private final Stopwatch timer;
	private final ApacheCMAES cmaes;
	private EvolutionState state;

	/**
	 * @param sigma                        Initial standard deviation (kind of mutation)
	 * @param onlyDiagonalCovarianceMatrix According to Hansen, prefered when dimensions > 100. Makes the algorithm
	 *                                     quicker. The algorithm works better, but only for ca. 100 generations. Than
	 *                                     it stagnates. This is probably published as (Ros & Hansen, 2008: A Simple
	 */
	public ApacheCMAESAlgorithm(int populationSize, PopulationEvaluator<S> populationEvaluator, S initialGuess,
			IndividualAdapter<S, DoubleVector> doubleVectorAdapter, double sigma,
			boolean onlyDiagonalCovarianceMatrix) {
		this.populationEvaluator = populationEvaluator;
		this.initialGuess = initialGuess;
		this.doubleVectorAdapter = doubleVectorAdapter;
		this.populationSize = populationSize;
		this.timer = Stopwatch.createUnstarted();
		this.sigma = sigma;

		this.cmaes = new ApacheCMAES(0, true, onlyDiagonalCovarianceMatrix ? Integer.MAX_VALUE : 0, 0,
				false, (iteration, previous, current) -> false);
	}

	public ApacheCMAESAlgorithm(int populationSize, PopulationEvaluator<S> populationEvaluator, S initialGuess,
			IndividualAdapter<S, DoubleVector> doubleVectorAdapter) {
		this(populationSize, populationEvaluator, initialGuess, doubleVectorAdapter, DEFAULT_SIGMA,
				DEFAULT_ONLY_DIAGONAL_COVARIANCE_MATRIX);
	}

	@Override
	public void evolve(EvolutionTarget target, ThreadedContext context) {
		Preconditions.checkArgument(target instanceof GenerationsTarget, "Other targets, currently not implemented");
		timer.start();

		cmaes.setNextIterationListener(this);

		cmaes.setRandom(context);

		state = OnePopulationEvolutionState.initialEvolutionState();

		cmaes.optimize(target, populationSize, sigma, doubleVectorAdapter.from(initialGuess).toArray(),
				new PopulationEvaluator<double[]>() {
					@Override
					public EvaluatedPopulation<double[]> evaluate(List<double[]> rawPopulation, int generation,
							ThreadedContext context) {

						List<S> population = rawPopulationToPopulation(rawPopulation);

						EvaluatedPopulation<S> evaluatedPopulation = populationEvaluator.evaluate(population,
								generation, context);

						return evaluatedPopulationToRawEvaluatedPopulation(evaluatedPopulation);
					}
				});

		timer.stop();
	}

	private EvaluatedPopulation<double[]> evaluatedPopulationToRawEvaluatedPopulation(
			EvaluatedPopulation<S> evaluated) {
		List<EvaluatedIndividual<double[]>> evaluatedPop = new ArrayList<>(populationSize);
		for (EvaluatedIndividual<S> individual : evaluated) {
			evaluatedPop.add(new EvaluatedIndividual<>(doubleVectorAdapter.from(
					individual.getIndividual()).toArray(), individual.getFitness(),
					individual.getGeneration(), individual.getEffort()));
		}
		return new EvaluatedPopulation<>(evaluatedPop, evaluated.getTotalEffort());
	}

	private List<S> rawPopulationToPopulation(List<double[]> population) {
		List<S> pop = new ArrayList<>(populationSize);
		for (double[] doubles : population) {
			pop.add(doubleVectorAdapter.from(new DoubleVector(doubles), initialGuess));
		}
		return pop;
	}

	private EvaluatedPopulation<S> rawEvaluatedPopulationToEvaluatedPopulation(
			EvaluatedPopulation<double[]> rawEvaluatedPopulation) {
		List<EvaluatedIndividual<S>> pop = new ArrayList<>(populationSize);
		for (EvaluatedIndividual<double[]> doubles : rawEvaluatedPopulation) {
			pop.add(new EvaluatedIndividual<>(doubleVectorAdapter.from(new DoubleVector(doubles.getIndividual()),
					initialGuess), doubles.fitness()));
		}
		return new EvaluatedPopulation<>(pop, rawEvaluatedPopulation.getTotalEffort());
	}

	@Override
	public void onNextIteraction(EvaluatedPopulation<double[]> rawEvaluatedPopulation) {
		updateState(rawEvaluatedPopulation);
		fireNextGenerationEvent(state);
	}

	@Override
	public void onLastIteraction(EvaluatedPopulation<double[]> rawEvaluatedPopulation) {
		fireLastGenerationEvent(state);
	}


	private void updateState(EvaluatedPopulation<double[]> rawEvaluatedPopulation) {
		EvaluatedPopulation<S> population = rawEvaluatedPopulationToEvaluatedPopulation(rawEvaluatedPopulation);

		int generation = state.getGeneration() + 1;
		long elapsed = timer.elapsed(MILLISECONDS);

		state = new OnePopulationEvolutionState<>(elapsed, generation,
				state.getTotalEffort() + population.getTotalEffort(), population.getPopulation(), state.getTarget());
	}
}
