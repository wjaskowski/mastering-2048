package put.ci.cevo.experiments.profiles.generators;

import static put.ci.cevo.util.RandomUtils.pickRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import put.ci.cevo.framework.algorithms.stateful.StatefulEvolutionaryAlgorithm;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import com.google.common.base.Preconditions;

public class RSELStrategyGenerator<T> implements StrategyGenerator<T> {

	private final ArrayList<StatefulEvolutionaryAlgorithm<T>> algorithms;

	private int currentGeneratorNumber = 0;
	private boolean nextJustInitializePopulation;

	@AccessedViaReflection
	public RSELStrategyGenerator(StatefulEvolutionaryAlgorithm<T> algorithm) {
		this(Arrays.asList(algorithm));
	}

	/** Multiple RSELS are executed in a round-robin fashion */
	public RSELStrategyGenerator(List<StatefulEvolutionaryAlgorithm<T>> algorithms) {
		Preconditions.checkArgument(algorithms.size() > 0);

		this.currentGeneratorNumber = 0;
		this.algorithms = new ArrayList<StatefulEvolutionaryAlgorithm<T>>(algorithms);
		this.nextJustInitializePopulation = true;
	}

	@Override
	public T createNext(ThreadedContext context) {
		StatefulEvolutionaryAlgorithm<T> rsel = algorithms.get(currentGeneratorNumber);

		if (this.nextJustInitializePopulation) {
			rsel.initializePopulation(context.getRandomForThread());
			this.nextJustInitializePopulation = false;
		} else {
			rsel.nextEvolutionStep(context);
		}

		return pickRandom(rsel.getCurrentPopulation(), context.getRandomForThread());
	}

	@Override
	public void reset() {
		currentGeneratorNumber = moveToNextGenerator();
		this.nextJustInitializePopulation = true;
	}

	private int moveToNextGenerator() {
		return (currentGeneratorNumber + 1) % algorithms.size();
	}
}
