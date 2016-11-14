package put.ci.cevo.experiments.runs.othello.wj_cig2014rework;

import com.google.common.base.Stopwatch;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;
import org.apache.commons.lang.ArrayUtils;
import put.ci.cevo.framework.algorithms.AbstractGenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.archives.Archive;
import put.ci.cevo.framework.algorithms.archives.HallOfFameArchive;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.OnePopulationEvolutionState;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static put.ci.cevo.util.sequence.Sequences.seq;

//TODO: We can actually merge it with the normal CMAESAlgorithm
@AccessedViaReflection
public class HybridCMAESAlgorithm<S> extends AbstractGenerationalOptimizationAlgorithm {

	private final int archiveSize;
	private final TwoPopulationEvaluator<S, S> twoPopulationEvaluator;
	private final S initialGuess;
	private final IndividualAdapter<S, DoubleVector> doubleVectorAdapter;
	private final int populationSize;
	private final double sigma;

	private final Stopwatch timer;

	/**
	 * @param sigma Initial standard deviation (kind of mutation)
	 */
	public HybridCMAESAlgorithm(int populationSize, int archiveSize, double sigma,
			TwoPopulationEvaluator<S, S> twoPopulationEvaluator,
			S initialGuess, IndividualAdapter<S, DoubleVector> doubleVectorAdapter) {
		this.archiveSize = archiveSize;
		this.twoPopulationEvaluator = twoPopulationEvaluator;
		this.initialGuess = initialGuess;
		this.doubleVectorAdapter = doubleVectorAdapter;
		this.populationSize = populationSize;
		this.sigma = sigma;
		this.timer = Stopwatch.createUnstarted();
	}

	@Override
	public void evolve(EvolutionTarget target, ThreadedContext context) {
		timer.start();

		CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
		cma.setInitialX(doubleVectorAdapter.from(initialGuess).toArray());
		cma.setInitialStandardDeviation(sigma);
		cma.parameters.setPopulationSize(populationSize);
		cma.setSeed(context.getRandomForThread().getRandomGenerator().nextLong());
		cma.init();

		EvolutionState state = OnePopulationEvolutionState.initialEvolutionState();

		Archive<S> archive = new HallOfFameArchive<>(archiveSize);

		while (!target.isAchieved(state)) {
			double[][] rawPopulation = cma.samplePopulation();
			List<S> population = getPopulationFromRawPopulation(rawPopulation);

			Pair<EvaluatedPopulation<S>, EvaluatedPopulation<S>> evaluated = twoPopulationEvaluator.evaluate(population,
					archive.getArchived(), state.getGeneration(), context);

			archive.submit(population);

			cma.updateDistribution(getNegatedFitnessArray(evaluated.first()));

			state = nextGeneration(state, evaluated.first(), target);
			fireNextGenerationEvent(state);
		}
		fireLastGenerationEvent(state);
		timer.stop();
	}

	private List<S> getPopulationFromRawPopulation(double[][] rawPopulation) {
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
