package put.ci.cevo.ml.clustering.algorithms;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.ml.clustering.MedoidCluster;
import put.ci.cevo.ml.neighbors.NearestNeighbors;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.ArrayIndexComparator;
import put.ci.cevo.util.DoubleTable;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static put.ci.cevo.ml.clustering.ClusteringUtils.centroid;
import static put.ci.cevo.util.DoubleTable.DoubleTableBuilder;
import static put.ci.cevo.util.RandomUtils.pickRandom;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.doubleAdd;

/**
 * Fast and efficient implementation of partitioning around medoids (k-medoids). Based on
 * "A simple and fast algorithm for K-medoids clustering" by Hae-Sang Park and Chi-Hyuck Jun.
 */
public class KMedoidsClusterer<T extends Clusterable> implements Clusterer<T> {

	public static enum SeedingStrategy {
		/**
		 * Random selection of initial medoids
		 */
		RANDOM {
			@Override
			public <T extends Clusterable> List<MedoidCluster<T>> chooseInitialCenters(int k, List<T> pointList,
					DoubleTable<T, T> distances, RandomDataGenerator random) {
				final List<MedoidCluster<T>> centroids = new ArrayList<>(k);
				for (int i = 0; i < k; i++) {
					centroids.add(new MedoidCluster<>(pointList.get(random.nextInt(0, pointList.size() - 1))));
				}
				return centroids;
			}
		},

		/**
		 * This selection procedure is detailed in "A simple and fast algorithm for K-medoids clustering"
		 */
		PARKJUN {
			@Override
			public <T extends Clusterable> List<MedoidCluster<T>> chooseInitialCenters(int k, List<T> pointList,
					DoubleTable<T, T> distances, RandomDataGenerator random) {
				Double[] sums = new Double[pointList.size()];
				for (int i = 0; i < sums.length; i++) {
					sums[i] = distances.rowValues(pointList.get(i)).aggregate(doubleAdd());
				}

				Double[] values = new Double[pointList.size()];
				for (int i = 0; i < pointList.size(); i++) {
					values[i] = 0.0;
					for (int j = 0; j < pointList.size(); j++) {
						values[i] += distances.get(pointList.get(i), pointList.get(j)) / sums[j];
					}
				}

				ArrayIndexComparator<Double> comparator = new ArrayIndexComparator<>(values);
				Integer[] indices = comparator.createIndexArray();
				Arrays.sort(indices, comparator);

				final List<MedoidCluster<T>> medoids = new ArrayList<>(k);
				for (int i = 0; i < k; i++) {
					medoids.add(new MedoidCluster<>(pointList.get(indices[i])));
				}

				return medoids;
			}
		};

		public abstract <T extends Clusterable> List<MedoidCluster<T>> chooseInitialCenters(int k, List<T> pointList,
				DoubleTable<T, T> distances, RandomDataGenerator random);
	}

	private final int k;
	private final int maxIterations;

	private final DistanceMetric metric;
	private final SeedingStrategy strategy;

	public KMedoidsClusterer(int k) {
		this(k, -1, new EuclideanDistance(), SeedingStrategy.PARKJUN);
	}

	public KMedoidsClusterer(int k, int maxIterations, DistanceMetric metric, SeedingStrategy strategy) {
		this.k = k;
		this.maxIterations = maxIterations;
		this.metric = metric;
		this.strategy = strategy;
	}

	public DistanceMetric getMeasure() {
		return metric;
	}

	@Override
	public List<MedoidCluster<T>> cluster(Collection<T> points, ThreadedContext context) {
		// initialize some useful structures
		final List<T> pointsList = new ArrayList<>(points);
		final RandomDataGenerator random = context.getRandomForThread();

		final DoubleTable<T, T> distances = precomputeDistances(pointsList);
		// final NearestNeighbors<T> neighbors = new NearestNeighbors<>(pointsList, metric);

		// initialize clustering process
		List<MedoidCluster<T>> medoids = strategy.chooseInitialCenters(k, pointsList, distances, random);

		// create an array containing the latest assignment of a point to a cluster
		int[] assignments = new int[pointsList.size()];
		assignPointsToClusters(medoids, pointsList, distances, assignments);

		final int max = (maxIterations < 0) ? Integer.MAX_VALUE : maxIterations;
		for (int count = 0; count < max; count++) {
			medoids = recalculateMedoids(pointsList, null, medoids, random);
			int changes = assignPointsToClusters(medoids, pointsList, distances, assignments);
			if (changes == 0) {
				return medoids;
			}
		}
		return medoids;
	}

	/**
	 * Distance between every pair of points is computed only once and stored in a {@link DoubleTable}.
	 */
	private DoubleTable<T, T> precomputeDistances(List<T> points) {
		DoubleTableBuilder<T, T> builder = DoubleTable.create(points, points);
		for (int i = 0; i < points.size(); i++) {
			T p1 = points.get(i);
			for (int j = i; j < points.size(); j++) {
				T p2 = points.get(j);
				double distance = (i != j) ? metric.distance(p1.getPoint(), p2.getPoint()) : 0;
				builder.put(p1, p2, distance);
				builder.put(p2, p1, distance);
			}
		}
		return builder.build();
	}

	private int assignPointsToClusters(final List<MedoidCluster<T>> clusters, final Collection<T> points,
			DoubleTable<T, T> distances, final int[] assignments) {
		int assignedDifferently = 0;
		int pointIndex = 0;

		for (T p : points) {
			int clusterIndex = getNearestCluster(clusters, p, distances);
			if (clusterIndex != assignments[pointIndex]) {
				assignedDifferently++;
			}
			clusters.get(clusterIndex).addPoint(p);
			assignments[pointIndex++] = clusterIndex;
		}

		return assignedDifferently;
	}

	private List<MedoidCluster<T>> recalculateMedoids(List<T> pointsList, NearestNeighbors<T> neighbors,
			List<MedoidCluster<T>> medoids, RandomDataGenerator random) {
		List<MedoidCluster<T>> newMedoids = new ArrayList<>(k);
		for (MedoidCluster<T> cluster : medoids) {
			if (cluster.getPoints().size() == 0) {
				newMedoids.add(new MedoidCluster<>(pickRandom(pointsList, random)));
			} else {
				Clusterable centroid = centroid(cluster.getPoints(), cluster.getMedoid().getPoint().length);
				// T nearest = neighbors.nearest(centroid.getPoint(), 1).getFirst();
				T nearest = nearest(centroid.getPoint(), pointsList);
				newMedoids.add(new MedoidCluster<>(nearest));
			}
		}
		return newMedoids;
	}

	private int getNearestCluster(Collection<MedoidCluster<T>> clusters, T point, DoubleTable<T, T> distances) {
		double minDistance = Double.MAX_VALUE;
		int clusterIndex = 0;
		int minCluster = 0;
		for (MedoidCluster<T> c : clusters) {
			final double distance = distances.get(point, c.getMedoid());
			if (distance < minDistance) {
				minDistance = distance;
				minCluster = clusterIndex;
			}
			clusterIndex++;
		}
		return minCluster;
	}

	// TODO: this could be implemented much nicer using kd-trees. However, current implementation seems to be wrong...
	private T nearest(double[] centroid, List<T> pointsList) {
		T nearest = null;
		double distance = Double.MAX_VALUE;
		for (T point : pointsList) {
			double currentDistance = metric.distance(centroid, point.getPoint());
			if (currentDistance < distance) {
				nearest = point;
				distance = currentDistance;
			}
		}
		return nearest;
	}

}
