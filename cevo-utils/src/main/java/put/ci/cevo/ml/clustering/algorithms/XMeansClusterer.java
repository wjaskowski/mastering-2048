package put.ci.cevo.ml.clustering.algorithms;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.util.FastMath;
import put.ci.cevo.ml.clustering.CentroidCluster;
import put.ci.cevo.ml.clustering.Cluster;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.ml.clustering.ClusteringUtils;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.stats.Handler;
import put.ci.cevo.util.stats.TableEventHandler;
import uk.ac.starlink.table.StarTable;
import weka.clusterers.XMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static put.ci.cevo.util.ArrayUtils.mean;
import static put.ci.cevo.util.TextUtils.format;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class XMeansClusterer<T extends Clusterable> implements Clusterer<T> {

	private static class BinDistance extends EuclideanDistance {

		@Override
		public double distance(Instance first, Instance second) {
			double[] point = first.toDoubleArray();
			double[] centroid = second.toDoubleArray();

			double sum = 0;
			for (int i = 0; i < point.length; i++) {
				final double dp = point[i] - (centroid[i] < 0.5 ? 0 : 1);
				sum += dp * dp;
			}

			return FastMath.sqrt(sum);
		}
	}

	private static class BinAvgDistance extends EuclideanDistance {

		private double[] means;

		@Override
		public void setInstances(Instances insts) {
			super.setInstances(insts);
			int rows = insts.size();
			int cols = insts.get(0).toDoubleArray().length;
			double[][] matrix = new double[cols][rows];
			for (int i = 0; i < rows; i++) {
				double[] p = insts.get(i).toDoubleArray();
				for (int j = 0; j < cols; j++) {
					matrix[j][i] = p[j];
				}
			}

			means = new double[cols];
			for (int j = 0; j < cols; j++) {
				means[j] = mean(matrix[j]);
			}
		}

		@Override
		public double distance(Instance first, Instance second) {
			double[] point = first.toDoubleArray();
			double[] centroid = second.toDoubleArray();

			double sum = 0;
			for (int i = 0; i < point.length; i++) {
				final double dp = point[i] - (centroid[i] < means[i] ? 0 : 1);
				sum += dp * dp;
			}

			return FastMath.sqrt(sum);
		}
	}

	@Handler(targetEvent = NumClusters.class)
	public static class NumClusters implements TableEventHandler<Integer> {

		private final List<Integer> clusters = new ArrayList<>();

		@Override
		public StarTable getTable() {
			if (clusters.isEmpty()) {
				return null;
			}
			TableUtil.TableBuilder builder = new TableUtil.TableBuilder("gen", "num-clusters");
			for (int i = 0; i < clusters.size(); i++) {
				builder.addRow(i, format(clusters.get(i)));
			}
			return builder.build();
		}

		@Override
		public void log(Integer numClusters) {
			clusters.add(numClusters);
		}
	}

	private final int minK;
	private final int maxK;

	private final DistanceFunction distanceFunction;

	public XMeansClusterer(int minK, int maxK) {
		this(minK, maxK, new EuclideanDistance());
	}

	public XMeansClusterer(int minK, int maxK, boolean bin) {
		this(minK, maxK, bin ? new BinDistance() : new BinAvgDistance());
	}

	public XMeansClusterer(int minK, int maxK, DistanceFunction distanceFunction) {
		this.minK = minK;
		this.maxK = maxK;
		this.distanceFunction = distanceFunction;
	}

	@Override
	public List<? extends Cluster<T>> cluster(Collection<T> points, ThreadedContext context) {
		XMeans xMeans = new XMeans();
		xMeans.getCapabilities().setTestWithFailAlwaysSucceeds(true);
		xMeans.setMinNumClusters(minK);
		xMeans.setMaxNumClusters(maxK);
//		xMeans.setUseKDTree(true);
		xMeans.setDistanceF(distanceFunction);
		xMeans.setSeed(context.getRandomForThread().getRandomGenerator().nextInt());

		final List<T> pts = ImmutableList.copyOf(points);
		final Instances instances = ClusteringUtils.pointsToInstances(pts);
		try {
			xMeans.buildClusterer(instances);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		context.getEventsLogger().log(NumClusters.class, xMeans.getClusterCenters().size());
		List<CentroidCluster<T>> clusterCenters = seq(xMeans.getClusterCenters()).map(
			new Transform<Instance, CentroidCluster<T>>() {
				@Override
				public CentroidCluster<T> transform(Instance object) {
					return new CentroidCluster<>(new ClusterableVector(object.toDoubleArray()));
				}
			}).toList();

		for (Instance instance : instances) {
			try {
				int idx = xMeans.clusterInstance(instance);
				clusterCenters.get(idx).addPoint(pts.get(instances.indexOf(instance)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return clusterCenters;
	}
}
