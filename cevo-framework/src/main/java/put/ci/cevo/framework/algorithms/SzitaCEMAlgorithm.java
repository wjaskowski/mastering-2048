package put.ci.cevo.framework.algorithms;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
public class SzitaCEMAlgorithm<S> extends AbstractGenerationalOptimizationAlgorithm {

	public static final double RHO = 0.01;
	public static final double ALPHA = 0.6;

	private final PopulationEvaluator<S> populationEvaluator;
	private final S initialGuess;
	private final IndividualAdapter<S, DoubleVector> doubleVectorAdapter;
	private final int populationSize;

	private final Stopwatch timer;
	private EvolutionState state;

	private final int nFeatures;
	private final double[] S;
	private final double[] M;
	private final double[] Snew;
	private final double[] Mnew;

	public SzitaCEMAlgorithm(int populationSize, PopulationEvaluator<S> populationEvaluator, S initialGuess,
			IndividualAdapter<S, DoubleVector> doubleVectorAdapter) {
		this.populationEvaluator = populationEvaluator;
		this.initialGuess = initialGuess;
		this.doubleVectorAdapter = doubleVectorAdapter;
		this.populationSize = populationSize;
		this.timer = Stopwatch.createUnstarted();

		this.nFeatures = doubleVectorAdapter.from(initialGuess).size();
		this.S = new double[nFeatures];
		for (int i = 0; i < nFeatures; ++i)
			this.S[i] = 100.0;
		this.M = new double[nFeatures];
		this.Snew = new double[nFeatures];
		this.Mnew = new double[nFeatures];
	}

	@Override
	public void evolve(EvolutionTarget target, ThreadedContext context) {
		Preconditions.checkArgument(target instanceof GenerationsTarget, "Other targets, currently not implemented");
		timer.start();

		state = OnePopulationEvolutionState.initialEvolutionState();

		while (!target.isAchieved(state)) {

			EvaluatedPopulation<S> evaluated = nextGeneration(context);
			updateState(evaluated);
			fireNextGenerationEvent(state);
		}

		fireLastGenerationEvent(state);

		timer.stop();
	}

	private EvaluatedPopulation<S> nextGeneration(ThreadedContext context) {

		List<DoubleVector> genotypes = new ArrayList<>();
		List<S> population = new ArrayList<>();

		for (int it = 0; it < populationSize; it++) {
			double[] genotype = new double[nFeatures];
			for (int i = 0; i < nFeatures; i++) {
				genotype[i] = Math.sqrt(S[i]) * context.getRandomForThread().nextGaussian(0, 1) + M[i];
			}
			genotypes.add(DoubleVector.of(genotype));
			population.add(doubleVectorAdapter.from(DoubleVector.of(genotype), initialGuess));
		}

		EvaluatedPopulation<S> evaluated = populationEvaluator.evaluate(population, 0, context);

		List<Double> sortedScores = evaluated.getPopulation().stream().map(EvaluatedIndividual::getFitness).collect(
				Collectors.toList());
		Collections.sort(sortedScores, Collections.reverseOrder());

		int ELITES = (int) (populationSize * RHO);
		double GAMMA = sortedScores.get(ELITES - 1);
		for (int i = 0; i < nFeatures; i++) {
			Mnew[i] = 0;
			for (int it = 0; it < populationSize; it++)
				if (evaluated.getPopulation().get(it).getFitness() >= GAMMA)
					Mnew[i] += genotypes.get(it).get(i);
			Mnew[i] /= ELITES;

			Snew[i] = 0;
			for (int it = 0; it < populationSize; it++)
				if (evaluated.getPopulation().get(it).getFitness() >= GAMMA)
					Snew[i] += (genotypes.get(it).get(i) - Mnew[i]) * (genotypes.get(it).get(i) - Mnew[i]);
			Snew[i] /= ELITES;

			M[i] = (1 - ALPHA) * M[i] + ALPHA * Mnew[i];
			S[i] = (1 - ALPHA) * S[i] + ALPHA * Snew[i];
		}

		return evaluated;
	}

	private void updateState(EvaluatedPopulation<S> evaluated) {

		int generation = state.getGeneration() + 1;
		long elapsed = timer.elapsed(MILLISECONDS);

		state = new OnePopulationEvolutionState<>(elapsed, generation,
				state.getTotalEffort() + evaluated.getTotalEffort(), evaluated.getPopulation(), state.getTarget());
	}
}
