package put.ci.cevo.framework.algorithms.multiobjective.nsga2;

import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static put.ci.cevo.util.TextUtils.format;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.transforms.Transforms.formatDouble;

public class NSGA2Fitness extends MultiobjectiveFitness {

	public int rank;
	public double sparsity;

	public NSGA2Fitness(List<Double> fitness, int rank, double sparsity) {
		super(fitness);
		this.rank = rank;
		this.sparsity = sparsity;
	}
	
	public NSGA2Fitness(List<Double> fitness) {
		super(fitness);
	}

	public NSGA2Fitness(double[] fitness) {
		super(fitness);
	}

	@Override
	public int compareTo(Fitness o) {
		if (betterThan(o)) {
			return 1;
		} else if (o.betterThan(this)) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean betterThan(Fitness fitness) {
		NSGA2Fitness other = (NSGA2Fitness) fitness;
		if (rank < other.rank) {
			return true;
		} else if (rank > other.rank) {
			return false;
		}
		return sparsity > other.sparsity;
	}

	@Override
	public String toString() {
		return "[rank=" + rank + "; sparsity=" + format(sparsity) + "; obj="
			+ seq(getObjectives()).map(formatDouble()).toList() + "]";
	}

	public static <S> List<EvaluatedIndividual<S>> assignFitness(List<S> population, int generation,
			EffortTable<S, ?> efforts, Map<S, Fitness> fitness) {
		List<EvaluatedIndividual<S>> evaluated = new ArrayList<>(population.size());
		for (S individual : population) {
			evaluated.add(new EvaluatedIndividual<>(
				individual, NSGA2Fitness.wrap(fitness.get(individual)), generation, efforts.computeEffort(individual)));
		}
		return evaluated;
	}

	public static NSGA2Fitness wrap(Fitness fitness) {
		return new NSGA2Fitness(((MultiobjectiveFitness) fitness).getObjectives());
	}

}
