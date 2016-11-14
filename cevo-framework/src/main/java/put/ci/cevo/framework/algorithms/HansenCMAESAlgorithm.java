package put.ci.cevo.framework.algorithms;

import com.google.common.base.Stopwatch;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static put.ci.cevo.util.sequence.Sequences.seq;

@AccessedViaReflection
public class HansenCMAESAlgorithm<S> extends AbstractGenerationalOptimizationAlgorithm {
	private final Logger logger = Logger.getLogger(HansenCMAESAlgorithm.class);

	public static final double DEFAULT_SIGMA = 1.0;
	public static final boolean DEFAULT_ONLY_DIAGONAL_COVARIANCE_MATRIX = false;
	public static final boolean DEFAULT_CONSTANT_SIGMA = false;

	private final PopulationEvaluator<S> populationEvaluator;
	private final S initialGuess;
	private final IndividualAdapter<S, DoubleVector> doubleVectorAdapter;
	private final int populationSize;
	private final double sigma;
	private final boolean onlyDiagonalCovarianceMatrix;

	private final Stopwatch timer;
	private boolean constantSigma;

	/**
	 * @param sigma                        Initial standard deviation (kind of mutation)
	 * @param onlyDiagonalCovarianceMatrix According to Hansen, preferred when dimensions > 100. Makes the algorithm
	 *                                     quicker. The algorithm works better, but only for ca. 100 generations. Than
	 *                                     it stagnates (do not know why)
	 * @param constantSigma                Set if you do not want to use the sigma adaptation mechanism
	 */
	public HansenCMAESAlgorithm(int populationSize, PopulationEvaluator<S> populationEvaluator, S initialGuess,
			IndividualAdapter<S, DoubleVector> doubleVectorAdapter, double sigma,
			boolean onlyDiagonalCovarianceMatrix, boolean constantSigma) {
		this.populationEvaluator = populationEvaluator;
		this.initialGuess = initialGuess;
		this.doubleVectorAdapter = doubleVectorAdapter;
		this.populationSize = populationSize;
		this.sigma = sigma;
		this.onlyDiagonalCovarianceMatrix = onlyDiagonalCovarianceMatrix;
		this.constantSigma = constantSigma;
		this.timer = Stopwatch.createUnstarted();
	}

	public HansenCMAESAlgorithm(int populationSize, PopulationEvaluator<S> populationEvaluator, S initialGuess,
			IndividualAdapter<S, DoubleVector> doubleVectorAdapter) {
		this(populationSize, populationEvaluator, initialGuess, doubleVectorAdapter, DEFAULT_SIGMA,
				DEFAULT_ONLY_DIAGONAL_COVARIANCE_MATRIX, DEFAULT_CONSTANT_SIGMA);
	}

	@Override
	public void evolve(EvolutionTarget target, ThreadedContext context) {
		timer.start();

		CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
		cma.setInitialX(doubleVectorAdapter.from(initialGuess).toArray());
		cma.setInitialStandardDeviation(sigma);
		cma.parameters.setPopulationSize(populationSize);
		cma.options.constantSigma = this.constantSigma;
		cma.options.verbosity = -1;
		if (onlyDiagonalCovarianceMatrix) {
			cma.options.diagonalCovarianceMatrix = Integer.MAX_VALUE;
		}
		cma.setSeed(context.getRandomForThread().getRandomGenerator().nextLong());
		cma.init();

		EvolutionState state = OnePopulationEvolutionState.initialEvolutionState();

		boolean DEBUG = false;

		if (DEBUG) {
			cma.writeToDefaultFilesHeaders(0);
		}

		while (!target.isAchieved(state)) {
			Stopwatch stopwatch = Stopwatch.createStarted();

			List<S> population = breedNewPopulation(cma);
			assert population.size() == populationSize;

			double elapsedBreeding = stopwatch.elapsed(MILLISECONDS) / 1000.0;
			stopwatch.reset().start();

			EvaluatedPopulation<S> evaluated = populationEvaluator.evaluate(population, state.getGeneration(), context);

			double elapsedEvaluation = stopwatch.elapsed(MILLISECONDS) / 1000.0;
			stopwatch.reset().start();

			cma.updateDistribution(getNegatedFitnessArray(evaluated));

			double elapsedUpdate = stopwatch.elapsed(MILLISECONDS) / 1000.0;
			stopwatch.reset().start();

			state = nextGeneration(state, evaluated, target);
			fireNextGenerationEvent(state);

			double elapsedListener = stopwatch.elapsed(MILLISECONDS) / 1000.0;

			if (DEBUG) {
				cma.writeToDefaultFiles();
				//cma.printlnAnnotation();
				//cma.println();
			}

			logger.info(String.format("Breeding: %.2f, Eval: %.2f, Update: %.2f, Listerner: %.2f", elapsedBreeding,
					elapsedEvaluation, elapsedUpdate, elapsedListener));
		}
		if (DEBUG) {
			cma.writeToDefaultFiles(1);
			//cma.println();
		}
		fireLastGenerationEvent(state);
		timer.stop();
	}

	public List<S> breedNewPopulation(CMAEvolutionStrategy cma) {
		//TODO: Make it pararell. Use better eigenvalue method + sample the population in parallel
		double[][] rawPopulation = cma.samplePopulation();
		return populationFromRawPopulation(rawPopulation);
	}

	private List<S> populationFromRawPopulation(double[][] rawPopulation) {
		return seq(rawPopulation).map(new Transform<double[], S>() {
			@Override
			public S transform(double[] vector) {
				return doubleVectorAdapter.from(new DoubleVector(vector), initialGuess);
			}
		}).toList();
	}

	private double[] getNegatedFitnessArray(EvaluatedPopulation<S> evaluatedPopulation) {
		return ArrayUtils.toPrimitive(seq(evaluatedPopulation).map(EvaluatedIndividual.<S>toFitness()).map(
				Transforms.negativeValue()).toArray(Double.class));
	}

	private EvolutionState nextGeneration(EvolutionState state, EvaluatedPopulation<S> population,
			EvolutionTarget target) {

		int generation = state.getGeneration() + 1;
		long elapsed = timer.elapsed(MILLISECONDS);
		return new OnePopulationEvolutionState<>(elapsed, generation,
				state.getTotalEffort() + population.getTotalEffort(), population.getPopulation(), target);
	}
}
