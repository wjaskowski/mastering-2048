package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.ml.clustering.meanshift.MeanShift;
import put.ci.cevo.ml.clustering.meanshift.kernel.FlatKernel;
import put.ci.cevo.ml.clustering.meanshift.kernel.Kernel;
import put.ci.cevo.ml.clustering.meanshift.seed.SeedingStrategy;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

public class MeanShiftFitnessClustering implements FitnessAggregate {

	private final ClusteringFitnessAggregate aggregate;

	@AccessedViaReflection
	public MeanShiftFitnessClustering() {
		this.aggregate = new ClusteringFitnessAggregate(new MeanShift<>(new FlatKernel(), new EuclideanDistance()));
	}

	@AccessedViaReflection
	public MeanShiftFitnessClustering(double bandwidth) {
		this(bandwidth, new FlatKernel(), new EuclideanDistance());
	}

	@AccessedViaReflection
	public MeanShiftFitnessClustering(Kernel kernel, DistanceMetric metric) {
		this.aggregate = new ClusteringFitnessAggregate(new MeanShift<>(kernel, metric));
	}

	@AccessedViaReflection
	public MeanShiftFitnessClustering(Kernel kernel, DistanceMetric metric, SeedingStrategy seed) {
		this.aggregate = new ClusteringFitnessAggregate(new MeanShift<>(kernel, metric, seed));
	}

	@AccessedViaReflection
	public MeanShiftFitnessClustering(double bandwidth, Kernel kernel, DistanceMetric metric) {
		this.aggregate = new ClusteringFitnessAggregate(new MeanShift<>(kernel, metric, bandwidth));
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return aggregate.aggregateFitness(payoff, context);
	}
}
