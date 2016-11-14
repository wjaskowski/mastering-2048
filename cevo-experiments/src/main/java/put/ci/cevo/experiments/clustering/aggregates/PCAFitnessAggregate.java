package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.ml.pca.PCA;
import put.ci.cevo.util.ArrayIndexComparator;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections15.ComparatorUtils.reversedComparator;
import static put.ci.cevo.util.DoubleArrays.ones;
import static put.ci.cevo.util.DoubleArrays.zeros;

public class PCAFitnessAggregate implements FitnessAggregate {

	private final double variance;

	public PCAFitnessAggregate(double variance) {
		this.variance = variance;
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoffs, ThreadedContext context) {
		// perform a PCA transformation
		PCA pca = new PCA();
		double[][] payoff = payoffs.toArray();
		double[][] fitnessMatrix = pca.reduce(payoff, variance);

		// find ideal and anti-ideal solution representation in the space induced by PCA
		double[] ideal = pca.sampleToEigenSpace(ones(payoff[0].length));
		double[] worst = pca.sampleToEigenSpace(zeros(payoff[0].length));

		// compute distances between the extremes on each objective
		Double[] diffs = new Double[ideal.length];
		for (int i = 0; i < ideal.length; i++) {
			diffs[i] = ideal[i] - worst[i];
		}

		// get the descending order according to the computed differences
		ArrayIndexComparator<Double> comparator = new ArrayIndexComparator<Double>(diffs);
		Integer[] indices = comparator.createIndexArray();
		java.util.Arrays.sort(indices, reversedComparator(comparator));

		// find the most relevant principal components according to the objective preferences
		int lastRelevant = 1;
		while (diffs[indices[lastRelevant]] > 0.1) {
			lastRelevant++;
		}

		// finally, compute the fitness for each candidate solution
		Map<S, Fitness> fitness = new HashMap<>();
		List<S> solutions = payoffs.solutions().toList();
		for (int i = 0; i < solutions.size(); i++) {
			double[] f = new double[lastRelevant];
			for (int m = 0; m < lastRelevant; m++) {
				// TODO: I should scale it to 0-1
				f[m] = ideal[indices[m]] - fitnessMatrix[i][indices[m]];
			}
			fitness.put(solutions.get(i), new MultiobjectiveFitness(f));
		}
		return fitness;
	}
}
