package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.ml.pca.PCA;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaivePCAFitnessAggregate implements FitnessAggregate {

	private final int variance;

	public NaivePCAFitnessAggregate(int variance) {
		this.variance = variance;
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoffs, ThreadedContext context) {
		PCA pca = new PCA();
		double[][] fitnessMatrix = pca.reduce(payoffs.toArray(), variance);

		Map<S, Fitness> fitness = new HashMap<>();
		List<S> solutions = payoffs.solutions().toList();
		for (int i = 0; i < solutions.size(); i++) {
			fitness.put(solutions.get(i), new MultiobjectiveFitness(fitnessMatrix[i]));
		}
		return fitness;
	}

}
