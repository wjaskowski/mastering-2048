package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.ml.clustering.algorithms.JSATKMeansClusterer;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

public class JSATKMeansFitnessAggregate implements FitnessAggregate {

	private final ClusteringFitnessAggregate aggregate;

	public JSATKMeansFitnessAggregate(int k) {
		this.aggregate = new ClusteringFitnessAggregate(new JSATKMeansClusterer(k));
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return aggregate.aggregateFitness(payoff, context);
	}

}
