package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.ml.clustering.algorithms.XMeansClusterer;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

public class XMeansFitnessAggregate implements FitnessAggregate {

	private final ClusteringFitnessAggregate aggregate;

	public XMeansFitnessAggregate(int minK, int maxK) {
		this.aggregate = new ClusteringFitnessAggregate(new XMeansClusterer<>(minK, maxK));
	}

	public XMeansFitnessAggregate(int minK, int maxK, boolean bin) {
		this.aggregate = new ClusteringFitnessAggregate(new XMeansClusterer<>(minK, maxK, bin));
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return aggregate.aggregateFitness(payoff, context);
	}
}
