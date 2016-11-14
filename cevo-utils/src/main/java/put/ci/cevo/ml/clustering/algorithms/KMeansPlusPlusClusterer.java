package put.ci.cevo.ml.clustering.algorithms;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import put.ci.cevo.ml.clustering.*;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static put.ci.cevo.ml.clustering.ClusteringUtils.assignPointsToClusters;
import static put.ci.cevo.ml.clustering.ClusteringUtils.centroid;

/**
 * Clustering algorithm based on David Arthur and Sergei Vassilvitski k-means++ algorithm.
 */
public class KMeansPlusPlusClusterer<T extends Clusterable> implements Clusterer<T> {

	private final int k;
	private final int maxIterations;
	/**
	 * Keep in mind that using a different distance function other than {@link EuclideanDistance} may stop
	 * the algorithm from converging! If you intend to do so, consider using {@link KMedoidsClusterer} instead
	 */
	private DistanceMetric measure;

	public KMeansPlusPlusClusterer(final int k) {
		this(k, -1);
	}

	public KMeansPlusPlusClusterer(final int k, final int maxIterations) {
		this(k, maxIterations, new EuclideanDistance());
	}

	public KMeansPlusPlusClusterer(final int k, final int maxIterations, final DistanceMetric measure) {
		this.k = k;
		this.maxIterations = maxIterations;
		this.measure = measure;
	}

	public DistanceMetric getMeasure() {
		return measure;
	}

	@Override
	public List<CentroidCluster<T>> cluster(final Collection<T> points, ThreadedContext context) {
		if (points.size() < k) {
			throw new IllegalArgumentException(
					"Number of clusters has to be smaller or equal the number of data points");
		}
		RandomDataGenerator random = context.getRandomForThread();

		List<CentroidCluster<T>> clusters = chooseInitialCenters(points, random);

		// create an array containing the latest assignment of a point to a cluster
		// no need to initialize the array, as it will be filled with the first assignment
		int[] assignments = new int[points.size()];
		assignPointsToClusters(clusters, points, assignments, measure);

		// iterate through updating the centers until we're done
		final int max = (maxIterations < 0) ? Integer.MAX_VALUE : maxIterations;
		for (int count = 0; count < max; count++) {
			clusters = recalculateMeans(clusters, random);
			int changes = assignPointsToClusters(clusters, points, assignments, measure);

			if (changes == 0) {
				return clusters;
			}
		}
		return clusters;
	}

	private List<CentroidCluster<T>> chooseInitialCenters(final Collection<T> points, RandomDataGenerator random) {
		final List<T> pointList = Collections.unmodifiableList(new ArrayList<>(points));

		final int numPoints = pointList.size();
		final boolean[] taken = new boolean[numPoints];
		final List<CentroidCluster<T>> resultSet = new ArrayList<>();
		final int firstPointIndex = random.getRandomGenerator().nextInt(numPoints);

		final T firstPoint = pointList.get(firstPointIndex);

		resultSet.add(new CentroidCluster<T>(firstPoint));
		taken[firstPointIndex] = true;

		final double[] minDistSquared = new double[numPoints];

		for (int i = 0; i < numPoints; i++) {
			if (i != firstPointIndex) { // That point isn't considered
				double d = measure.distance(firstPoint.getPoint(), pointList.get(i).getPoint());
				minDistSquared[i] = d * d;
			}
		}
		while (resultSet.size() < k) {
			double distSqSum = 0.0;
			for (int i = 0; i < numPoints; i++) {
				if (!taken[i]) {
					distSqSum += minDistSquared[i];
				}
			}
			double r = random.getRandomGenerator().nextDouble() * distSqSum;
			int nextPointIndex = -1;
			double sum = 0.0;
			for (int i = 0; i < numPoints; i++) {
				if (!taken[i]) {
					sum += minDistSquared[i];
					if (sum >= r) {
						nextPointIndex = i;
						break;
					}
				}
			}
			if (nextPointIndex == -1) {
				for (int i = numPoints - 1; i >= 0; i--) {
					if (!taken[i]) {
						nextPointIndex = i;
						break;
					}
				}
			}
			if (nextPointIndex >= 0) {
				final T p = pointList.get(nextPointIndex);
				resultSet.add(new CentroidCluster<T>(p));
				taken[nextPointIndex] = true;
				if (resultSet.size() < k) {
					for (int j = 0; j < numPoints; j++) {
						if (!taken[j]) {
							double d = measure.distance(p.getPoint(), pointList.get(j).getPoint());
							double d2 = d * d;
							if (d2 < minDistSquared[j]) {
								minDistSquared[j] = d2;
							}
						}
					}
				}
			} else {
				break;
			}
		}

		return resultSet;
	}

	private List<CentroidCluster<T>> recalculateMeans(List<CentroidCluster<T>> clusters, RandomDataGenerator random) {
		List<CentroidCluster<T>> newClusters = new ArrayList<>(k);
		for (final CentroidCluster<T> cluster : clusters) {
			final Clusterable newCenter;
			if (cluster.getPoints().isEmpty()) {
				newCenter = getPointFromLargestVarianceCluster(clusters, random);
			} else {
				newCenter = centroid(cluster.getPoints(), cluster.getCenter().getPoint().length);
			}
			newClusters.add(new CentroidCluster<T>(newCenter));
		}
		return newClusters;
	}

	private T getPointFromLargestVarianceCluster(Collection<CentroidCluster<T>> clusters, RandomDataGenerator random) {
		double maxVariance = Double.NEGATIVE_INFINITY;
		Cluster<T> selected = null;
		for (final CentroidCluster<T> cluster : clusters) {
			if (!cluster.getPoints().isEmpty()) {

				final Clusterable center = cluster.getCenter();
				final Variance stat = new Variance();
				for (final T point : cluster.getPoints()) {
					stat.increment(measure.distance(point.getPoint(), center.getPoint()));
				}
				final double variance = stat.getResult();

				if (variance > maxVariance) {
					maxVariance = variance;
					selected = cluster;
				}

			}
		}

		if (selected == null) {
			throw new IllegalStateException("Empty cluster! Algorithm was unable to converge");
		}

		final List<T> selectedPoints = selected.getPoints();
		return selectedPoints.remove(random.getRandomGenerator().nextInt(selectedPoints.size()));

	}

}
