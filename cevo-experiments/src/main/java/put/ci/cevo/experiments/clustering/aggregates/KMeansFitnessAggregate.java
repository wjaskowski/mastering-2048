package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.ml.clustering.algorithms.KMeansPlusPlusClusterer;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

public class KMeansFitnessAggregate implements FitnessAggregate {

	private final ClusteringFitnessAggregate aggregate;

	public KMeansFitnessAggregate(int k) {
		this.aggregate = new ClusteringFitnessAggregate(new KMeansPlusPlusClusterer<>(k, 1000, new EuclideanDistance()));
	}

//	public KMeansFitnessAggregate(int k, boolean simpleMeans) {
//		this.aggregate = new ClusteringFitnessAggregate(new BinaryKMeansPlusPlusClusterer<>(k, simpleMeans));
//	}

	public KMeansFitnessAggregate(int k, DistanceMetric distance) {
		this.aggregate = new ClusteringFitnessAggregate(new KMeansPlusPlusClusterer<>(k, 1000, distance));
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return aggregate.aggregateFitness(payoff, context);
	}

}
