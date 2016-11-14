package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.ml.clustering.meanshift.JSATMeanShiftAdapter;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

public class JSATMeanShiftFitnessClustering implements FitnessAggregate {

	private final ClusteringFitnessAggregate aggregate;

	@AccessedViaReflection
	public JSATMeanShiftFitnessClustering() {
		this.aggregate = new ClusteringFitnessAggregate(new JSATMeanShiftAdapter());
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return aggregate.aggregateFitness(payoff, context);
	}
}
