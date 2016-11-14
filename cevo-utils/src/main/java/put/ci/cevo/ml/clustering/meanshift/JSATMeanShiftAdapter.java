package put.ci.cevo.ml.clustering.meanshift;

import jsat.classifiers.DataPoint;
import jsat.clustering.MeanShift;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import put.ci.cevo.ml.clustering.CentroidCluster;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Collection;
import java.util.List;

import static put.ci.cevo.ml.clustering.ClusteringUtils.createJSATDataset;
import static put.ci.cevo.ml.clustering.ClusteringUtils.pointsToClusters;

public class JSATMeanShiftAdapter implements Clusterer<Clusterable> {

	private final DistanceMetric measure;

	public JSATMeanShiftAdapter() {
		this(new EuclideanDistance());
	}

	public JSATMeanShiftAdapter(DistanceMetric measure) {
		this.measure = measure;
	}

	@Override
	public List<CentroidCluster<Clusterable>> cluster(Collection<Clusterable> points, ThreadedContext context) {
		final jsat.clustering.MeanShift meanShift = new MeanShift(measure);
		// there seems to be a deadlock when I use ExecutorService with less than 8 threads, probably a bug in JSAT.
		// List<List<DataPoint>> clusteredPoints = meanShift.cluster(createJSATDataset(points), context.getExecutor());
		List<List<DataPoint>> clusteredPoints = meanShift.cluster(createJSATDataset(points));
		return pointsToClusters(clusteredPoints);
	}

}
