package put.ci.cevo.experiments.clustering.aggregates;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;
import put.ci.cevo.experiments.clustering.aggregates.ClusteringFitnessAggregate.DimensionsMergeStrategy;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.ml.clustering.Cluster;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ArrayUtils.toPrimitive;
import static org.ejml.ops.CommonOps.identity;
import static org.ejml.ops.CommonOps.scale;
import static put.ci.cevo.util.TypeUtils.genericCast;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class SqueezingKMeansFitnessAggregate implements FitnessAggregate {

	private final Clusterer<Clusterable> clusterer;
	private final DimensionsMergeStrategy strategy;

	public SqueezingKMeansFitnessAggregate(Clusterer<Clusterable> clusterer) {
		this(clusterer, DimensionsMergeStrategy.MEAN);
	}

	public SqueezingKMeansFitnessAggregate(Clusterer<Clusterable> clusterer, DimensionsMergeStrategy strategy) {
		this.clusterer = clusterer;
		this.strategy = strategy;
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(final PayoffTable<S, T> payoff, ThreadedContext context) {
		DenseMatrix64F eye = identity(payoff.tests().size());
		scale(1.3, eye);

		DenseMatrix64F m = new DenseMatrix64F(payoff.tests().size(), payoff.tests().size());
		CommonOps.fill(m, -0.3);
		CommonOps.add(m, eye, m);

		DenseMatrix64F interactionMatrix = new DenseMatrix64F(payoff.toArray());
		DenseMatrix64F interactionMatrix2 = SimpleMatrix.wrap(new DenseMatrix64F(payoff.toArray())).transpose()
			.getMatrix();
		SimpleMatrix mult = SimpleMatrix.wrap(interactionMatrix).mult(SimpleMatrix.wrap(m));
		CommonOps.solve(mult.transpose().getMatrix(), SimpleMatrix.wrap(interactionMatrix).transpose().getMatrix(),
			interactionMatrix2);
		final SimpleMatrix transpose = SimpleMatrix.wrap(interactionMatrix2).transpose();

		SimpleMatrix res = transpose.mult(mult.extractVector(false, 0));

		PayoffTableBuilder<S, T> builder = PayoffTable.create(payoff.solutions(), payoff.tests());
		for (Pair<Integer, S> s : payoff.solutions().enumerate()) {
			for (Pair<Integer, T> t : payoff.tests().enumerate()) {
				builder.put(s.second(), t.second(), mult.get(s.first(), t.first()));
			}
		}
		final PayoffTable<S, T> payoffTable = builder.build();

		Collection<Clusterable> tests = payoffTable.tests().map(new Transform<T, Clusterable>() {
			@Override
			public Clusterable transform(T test) {
				return new ClusterableVector(toPrimitive(payoffTable.testPayoffs(test).toArray(Double.class)));
			}
		}).asCollection();

		List<Cluster<Clusterable>> clusters = genericCast(clusterer.cluster(tests, context));

		PayoffTableBuilder<S, Cluster<Clusterable>> builder2 = PayoffTable.create(payoffTable.solutions(), clusters);
		for (Cluster<Clusterable> cluster : clusters) {
			// transform back the points
			List<Clusterable> tf = seq(cluster.getPoints()).map(new Transform<Clusterable, Clusterable>() {
				@Override
				public Clusterable transform(Clusterable object) {
					double[] point = object.getPoint();
					return new ClusterableVector(transpose.mult(
						SimpleMatrix.wrap(DenseMatrix64F.wrap(point.length, 1, point))).getMatrix().data);
				}
			}).toList();
			List<Double> objective = strategy.merge(tf);
			for (Pair<Integer, S> s : payoffTable.solutions().enumerate()) {
				builder2.put(s.second(), cluster, objective.get(s.first()));
			}
		}

		final PayoffTable<S, Cluster<Clusterable>> kmeansPayoff = builder2.build();
		return payoffTable.solutions().keysToMap(new Transform<S, Fitness>() {
			@Override
			public Fitness transform(S solution) {
				return new MultiobjectiveFitness(kmeansPayoff.solutionPayoffs(solution).toList());
			}
		});
	}

}
