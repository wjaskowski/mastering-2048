package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.ml.clustering.algorithms.KMeansPlusPlusClusterer;
import put.ci.cevo.ml.clustering.algorithms.MultiClusterer;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

public class MultipleKMeansFitnessAggregate implements FitnessAggregate {

	private static final int DEFAULT_TRIALS = 3;

	private final ClusteringFitnessAggregate aggregate;

	public MultipleKMeansFitnessAggregate(int k) {
		this(k, DEFAULT_TRIALS);
	}

	public MultipleKMeansFitnessAggregate(int k, int numTrials) {
		this(k, numTrials, new EuclideanDistance());
	}

	public MultipleKMeansFitnessAggregate(int k, int numTrials, DistanceMetric distance) {
		this.aggregate = new ClusteringFitnessAggregate(new MultiClusterer<>(new KMeansPlusPlusClusterer<>(
			k, 1000, distance), numTrials));
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return aggregate.aggregateFitness(payoff, context);
	}

}
