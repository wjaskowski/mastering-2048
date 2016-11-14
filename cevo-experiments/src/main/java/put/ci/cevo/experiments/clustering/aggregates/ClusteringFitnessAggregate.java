package put.ci.cevo.experiments.clustering.aggregates;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.ml.clustering.Cluster;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.filter.AbstractFilter;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.stats.EventsLogger;
import put.ci.cevo.util.stats.Handler;
import put.ci.cevo.util.stats.TableEventHandler;
import uk.ac.starlink.table.StarTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.primitives.Doubles.toArray;
import static java.lang.Math.pow;
import static org.apache.commons.lang3.ArrayUtils.toPrimitive;
import static org.apache.commons.math3.linear.MatrixUtils.createRealVector;
import static put.ci.cevo.ml.clustering.ClusteringUtils.centroid;
import static put.ci.cevo.util.TextUtils.format;
import static put.ci.cevo.util.TypeUtils.genericCast;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class ClusteringFitnessAggregate implements FitnessAggregate {

	@Handler(targetEvent = ClusteringStats.class)
	public static class ClusteringStats implements TableEventHandler<List<Cluster<?>>> {

		private final TableBuilder builder = new TableBuilder();

		private int cols;

		@Override
		public void log(List<Cluster<?>> clusters) {
			int rowLength = clusters.size();
			if (rowLength > cols) {
				cols = rowLength;
			}
			builder.addRow(seq(clusters).map(new Transform<Cluster<?>, Integer>() {
				@Override
				public Integer transform(Cluster<?> object) {
					return object.getPoints().size();
				}
			}).addFirst(builder.getRowsCount()));
		}

		@Override
		public StarTable getTable() {
			return !builder.isEmpty() ? builder.setHeaders(createHeaders()).buildAlignedWith(0) : null;
		}

		private List<String> createHeaders() {
			List<String> headers = new ArrayList<>(cols + 1);
			headers.add("gen");
			for (int i = 0; i < cols; i++) {
				headers.add("cluster" + i);
			}
			return headers;
		}
	}

	@Handler(targetEvent = ClustersCorrelation.class)
	public static class ClustersCorrelation implements TableEventHandler<Double> {

		private final List<Double> correlations = new ArrayList<>();

		@Override public StarTable getTable() {
			if (correlations.isEmpty()) {
				return null;
			}
			TableBuilder builder = new TableBuilder("gen", "corr");
			for (int i = 0; i < correlations.size(); i++) {
				builder.addRow(i, format(correlations.get(i)));
			}
			return builder.build();
		}

		@Override public void log(Double correlation) {
			correlations.add(correlation);
		}
	}

	@Handler(targetEvent = ClustersVariance.class)
	public static class ClustersVariance implements TableEventHandler<Pair<Double, Double>> {

		private final List<Double> minVariances = new ArrayList<>();
		private final List<Double> maxVariances = new ArrayList<>();

		@Override public StarTable getTable() {
			if (minVariances.isEmpty() || maxVariances.isEmpty()) {
				return null;
			}
			TableBuilder builder = new TableBuilder("gen", "min-variance", "max-variance");
			for (int i = 0; i < minVariances.size(); i++) {
				builder.addRow(i, format(minVariances.get(i)), format(maxVariances.get(i)));
			}
			return builder.build();
		}

		@Override public void log(Pair<Double, Double> variances) {
			minVariances.add(variances.first());
			maxVariances.add(variances.second());
		}
	}

	@Handler(targetEvent = ClustersCohesionSepration.class)
	public static class ClustersCohesionSepration implements TableEventHandler<Pair<Double, Double>> {

		private final List<Double> cohesion = new ArrayList<>();
		private final List<Double> separation = new ArrayList<>();

		@Override public StarTable getTable() {
			if (cohesion.isEmpty() || separation.isEmpty()) {
				return null;
			}
			TableBuilder builder = new TableBuilder("gen", "cohesion", "separation");
			for (int i = 0; i < cohesion.size(); i++) {
				builder.addRow(i, format(cohesion.get(i)), format(separation.get(i)));
			}
			return builder.build();
		}

		@Override public void log(Pair<Double, Double> data) {
			cohesion.add(data.first());
			separation.add(data.second());
		}
	}

	/**
	 * Defines the strategy behind merging members of cluster
	 */
	public static enum DimensionsMergeStrategy {

		MEAN {
			@Override
			public List<Double> merge(List<? extends Clusterable> clusterPoints) {
				RealVector aggregate = createRealVector(clusterPoints.get(0).getPoint());
				for (int i = 1; i < clusterPoints.size(); i++) {
					aggregate = aggregate.add(createRealVector(clusterPoints.get(i).getPoint()));
				}
				return new DoubleArrayList(aggregate.mapDivide(clusterPoints.size()).toArray());
			}
		},

		SUM {
			@Override
			public List<Double> merge(List<? extends Clusterable> clusterPoints) {
				RealVector aggregate = createRealVector(clusterPoints.get(0).getPoint());
				for (int i = 1; i < clusterPoints.size(); i++) {
					aggregate = aggregate.add(createRealVector(clusterPoints.get(i).getPoint()));
				}
				return new DoubleArrayList(aggregate.toArray());
			}
		},

		WEIGHTED_SUM {
			@Override
			public List<Double> merge(List<? extends Clusterable> clusterPoints) {
				RealVector aggregate = createRealVector(clusterPoints.get(0).getPoint());
				for (int i = 1; i < clusterPoints.size(); i++) {
					aggregate = aggregate.add(createRealVector(clusterPoints.get(i).getPoint()));
				}
				return new DoubleArrayList(aggregate.toArray());
			}
		};

		public abstract List<Double> merge(List<? extends Clusterable> clusterPoints);

	}

	private final Clusterer<Clusterable> clusterer;
	private final DimensionsMergeStrategy strategy;

	public ClusteringFitnessAggregate(Clusterer<Clusterable> clusterer) {
		this(clusterer, DimensionsMergeStrategy.MEAN);
	}

	public ClusteringFitnessAggregate(Clusterer<Clusterable> clusterer, DimensionsMergeStrategy strategy) {
		this.clusterer = clusterer;
		this.strategy = strategy;
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(final PayoffTable<S, T> payoff, ThreadedContext context) {
//		final PayoffTable<S,T> payoff = PayoffTable.applyFitnessSharing(payoffTable);
//		final PayoffTable<S,T> payoff = PayoffTable.meanNormalize(payoffTable);
		Collection<Clusterable> tests = payoff.tests().map(new Transform<T, Clusterable>() {
			@Override
			public Clusterable transform(T test) {
				return new ClusterableVector(toPrimitive(payoff.testPayoffs(test).toArray(Double.class)));
			}
		}).asCollection();

		List<Cluster<Clusterable>> clusters = genericCast(clusterer.cluster(tests, context));
		// filter empty clusters
		clusters = seq(clusters).filter(new AbstractFilter<Cluster<Clusterable>>() {
			@Override public boolean qualifies(Cluster<Clusterable> cluster) {
				return cluster.getPoints().size() > 0;
			}
		}).toList();
		// log cluster sizes
		context.getEventsLogger().log(ClusteringStats.class, clusters);

		PayoffTableBuilder<S, Cluster<Clusterable>> builder = PayoffTable.create(payoff.solutions(), clusters);
		for (Cluster<Clusterable> cluster : clusters) {
			List<Double> objective = strategy.merge(cluster.getPoints());
			for (Pair<Integer, S> s : payoff.solutions().enumerate()) {
				builder.put(s.second(), cluster, objective.get(s.first()));
			}
		}

		final PayoffTable<S, Cluster<Clusterable>> clusteringPayoff = builder.build();
		context.getEventsLogger().log(ClustersCorrelation.class, computeClustersCorrelation(clusteringPayoff));
		logClustersVariance(clusters, context.getEventsLogger());
		logCohesionSeparation(clusters, new ArrayList<>(tests), context.getEventsLogger());
		return payoff.solutions().keysToMap(new Transform<S, Fitness>() {
			@Override
			public Fitness transform(S solution) {
				return new MultiobjectiveFitness(clusteringPayoff.solutionPayoffs(solution).toList());
			}
		});
	}

	private double computeClustersCorrelation(PayoffTable<?, Cluster<Clusterable>> clusterTable) {
		final PearsonsCorrelation pc = new PearsonsCorrelation();
		final ICombinatoricsVector<Cluster<Clusterable>> clusters = Factory.createVector(clusterTable.tests().toList());

		SummaryStatistics stats = new SummaryStatistics();
		Generator<Cluster<Clusterable>> combinations = Factory.createSimpleCombinationGenerator(clusters, 2);
		for (ICombinatoricsVector<Cluster<Clusterable>> combination : combinations) {
			List<Double> first = clusterTable.testPayoffs(combination.getValue(0)).toList();
			List<Double> second = clusterTable.testPayoffs(combination.getValue(1)).toList();
			double correlation = pc.correlation(toArray(first), toArray(second));
			if (!Double.isNaN(correlation)) {
				stats.addValue(correlation);
			} else {
				// I assume perfect correlation for all-zero rows
				stats.addValue(1);
			}
		}

		return stats.getMean();
	}

	private void logClustersVariance(List<Cluster<Clusterable>> clusters, EventsLogger logger) {
		Variance variance = new Variance();
		EuclideanDistance metric = new EuclideanDistance();
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (Cluster<Clusterable> cluster : clusters) {
			List<Double> distances = new ArrayList<>();
			double[] centroid = centroid(cluster.getPoints()).getPoint();
			for (Clusterable point : cluster.getPoints()) {
				distances.add(metric.distance(centroid, point.getPoint()));
			}
			double var = variance.evaluate(toArray(distances));
			if (var > max) {
				max = var;
			}
			if (var < min) {
				min = var;
			}
		}
		logger.log(ClustersVariance.class, Pair.create(min, max));
	}

	private void logCohesionSeparation(List<Cluster<Clusterable>> clusters, List<Clusterable> tests, EventsLogger logger) {
		final EuclideanDistance metric = new EuclideanDistance();
		final double[] mean = centroid(tests).getPoint();

		double cohesion = 0;
		double sepration = 0;
		for (Cluster<Clusterable> cluster : clusters) {
			final double[] centroid = centroid(cluster.getPoints()).getPoint();
			cohesion += cluster.getPoints().stream().mapToDouble(p -> pow(metric.distance(centroid, p.getPoint()), 2)).sum();
			sepration += pow(metric.distance(centroid, mean), 2) * cluster.getPoints().size();
		}

		logger.log(ClustersCohesionSepration.class, Pair.create(cohesion, sepration));
	}


}
