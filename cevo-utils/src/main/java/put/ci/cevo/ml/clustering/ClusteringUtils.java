package put.ci.cevo.ml.clustering;

import com.google.common.base.Preconditions;
import jsat.DataSet;
import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.vectors.DoubleVector;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static put.ci.cevo.ml.clustering.clusterable.ClusterableVector.wrap;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class ClusteringUtils {

	public static <T extends Clusterable> Clusterable centroid(List<T> points) {
		Preconditions.checkArgument(!points.isEmpty());
		double[] centroid = points.get(0).getPoint();
		for (int i = 1; i < points.size(); i++) {
			T p = points.get(i);
			double[] point = p.getPoint();
			for (int j = 0; j < centroid.length; j++) {
				centroid[j] += point[j];
			}
		}
		for (int i = 0; i < centroid.length; i++) {
			centroid[i] /= points.size();
		}
		return new ClusterableVector(centroid);

	}
	public static <T extends Clusterable> Clusterable centroid(Collection<T> points, int dimension) {
		double[] centroid = new double[dimension];
		for (T p : points) {
			double[] point = p.getPoint();
			for (int i = 0; i < centroid.length; i++) {
				centroid[i] += point[i];
			}
		}
		for (int i = 0; i < centroid.length; i++) {
			centroid[i] /= points.size();
		}
		return new ClusterableVector(centroid);
	}

	public static <T extends Clusterable> int assignPointsToClusters(List<CentroidCluster<T>> clusters,
			Collection<T> points, int[] assignments, DistanceMetric measure) {
		int assignedDifferently = 0;
		int pointIndex = 0;

		for (final T p : points) {
			int clusterIndex = getNearestCluster(clusters, p, measure);
			if (clusterIndex != assignments[pointIndex]) {
				assignedDifferently++;
			}
			clusters.get(clusterIndex).addPoint(p);
			assignments[pointIndex++] = clusterIndex;
		}

		return assignedDifferently;
	}

	public static <T extends Clusterable> int getNearestCluster(Collection<CentroidCluster<T>> clusters, T point,
			DistanceMetric measure) {
		double minDistance = Double.MAX_VALUE;
		int clusterIndex = 0;
		int minCluster = 0;
		for (final CentroidCluster<T> c : clusters) {
			final double distance = measure.distance(point.getPoint(), c.getCenter().getPoint());
			if (distance < minDistance) {
				minDistance = distance;
				minCluster = clusterIndex;
			}
			clusterIndex++;
		}
		return minCluster;
	}

	public static  <T extends Clusterable> DataSet createJSATDataset(Collection<T> points) {
		Preconditions.checkArgument(!points.isEmpty(), "Collection of data points cannot be empty");
		List<DataPoint> dataPoints = new ArrayList<>(points.size());
		for (Clusterable point : points) {
			dataPoints.add(new DataPoint(new DenseVector(point.getPoint()), null, null));
		}
		return new SimpleDataSet(dataPoints);
	}

	public static  <T extends Clusterable> List<Vec> pointsToVecs(Collection<T> points) {
		return seq(points).map(new Transform<T, Vec>() {
			@Override
			public Vec transform(T object) {
				return new DenseVector(object.getPoint());
			}
		}).toList();

	}

	public static List<CentroidCluster<Clusterable>> pointsToClusters(List<List<DataPoint>> clusteredPoints) {
		List<CentroidCluster<Clusterable>> clusters = new ArrayList<>(clusteredPoints.size());
		for (List<DataPoint> dataPoints : clusteredPoints) {
			if (!dataPoints.isEmpty()) {
				List<ClusterableVector> clusterPoints = pointsToVectors(dataPoints);

				CentroidCluster<Clusterable> cluster = new CentroidCluster<>(mean(clusterPoints));
				for (ClusterableVector clusterPoint : clusterPoints) {
					cluster.addPoint(clusterPoint);
				}

				clusters.add(cluster);
			}
		}
		return clusters;
	}

	private static List<ClusterableVector> pointsToVectors(List<DataPoint> dataPoints) {
		return seq(dataPoints).map(new Transform<DataPoint, ClusterableVector>() {
			@Override
			public ClusterableVector transform(DataPoint dataPoint) {
				return new ClusterableVector(dataPoint.getNumericalValues().arrayCopy());
			}
		}).toList();
	}

	public static ClusterableVector mean(List<ClusterableVector> points) {
		DoubleVector vector = points.get(0).getVector();
		for (int i = 1; i < points.size(); i++) {
			vector = vector.add(points.get(i).getVector());
		}
		return wrap(vector.divide(points.size()));
	}

	public static <T extends Clusterable> Instances pointsToInstances(List<T> points) {
		final int length = points.get(0).getPoint().length;
		ArrayList<Attribute> attrs = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			attrs.add(new Attribute("atr"+i, i));
		}
		Instances instances = new Instances("", attrs, points.size());
		for (T point : points) {
			instances.add(new DenseInstance(1.0, point.getPoint()));
		}
		return instances;
	}



}
