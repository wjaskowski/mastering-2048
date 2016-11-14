package put.ci.cevo.ml.clustering.algorithms;

import jsat.classifiers.DataPoint;
import jsat.clustering.kmeans.ElkanKMeans;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import put.ci.cevo.ml.clustering.CentroidCluster;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static jsat.clustering.SeedSelectionMethods.SeedSelection.FARTHEST_FIRST;
import static put.ci.cevo.ml.clustering.ClusteringUtils.createJSATDataset;
import static put.ci.cevo.ml.clustering.ClusteringUtils.pointsToClusters;

public class JSATKMeansClusterer implements Clusterer<Clusterable> {

	private final int k;
	private final DistanceMetric metric;

	public JSATKMeansClusterer(int k) {
		this(k, new EuclideanDistance());
	}

	public JSATKMeansClusterer(int k, DistanceMetric metric) {
		this.k = k;
		this.metric = metric;
	}

	@Override
	public List<CentroidCluster<Clusterable>> cluster(Collection<Clusterable> points, ThreadedContext context) {
		ElkanKMeans kmeans = new ElkanKMeans(metric,
				//			new Random(context.getRandomForThread().getRandomGenerator().nextLong()), SeedSelectionMethods.SeedSelection.RANDOM);
				new Random(context.getRandomForThread().getRandomGenerator().nextLong()), FARTHEST_FIRST);
		List<List<DataPoint>> clusters = kmeans.cluster(createJSATDataset(points), k, context.getExecutor());
		return pointsToClusters(clusters);
	}
}
