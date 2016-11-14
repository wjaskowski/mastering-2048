package put.ci.cevo.ml.clustering.algorithms;

import org.apache.commons.math3.stat.descriptive.moment.Variance;
import put.ci.cevo.ml.clustering.CentroidCluster;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Collection;
import java.util.List;

/**
 * A wrapper around a k-means++ clustering algorithm which performs multiple trials and returns the best solution.
 */
//TODO: rename to MultiKMeansClusterer
public class MultiClusterer<T extends Clusterable> implements Clusterer<T> {

	private final KMeansPlusPlusClusterer<T> clusterer;

	private final int numTrials;

	public MultiClusterer(KMeansPlusPlusClusterer<T> clusterer, int numTrials) {
		this.clusterer = clusterer;
		this.numTrials = numTrials;
	}

	@Override
	public List<CentroidCluster<T>> cluster(final Collection<T> points, ThreadedContext context) {

		List<CentroidCluster<T>> best = null;
		double bestVarianceSum = Double.POSITIVE_INFINITY;

		for (int i = 0; i < numTrials; ++i) {

			// compute a clusters list
			List<CentroidCluster<T>> clusters = clusterer.cluster(points, context);

			// compute the variance of the current list
			double varianceSum = 0.0;
			for (final CentroidCluster<T> cluster : clusters) {
				if (!cluster.getPoints().isEmpty()) {

					// compute the distance variance of the current cluster
					final Clusterable center = cluster.getCenter();
					final Variance stat = new Variance();
					for (final T point : cluster.getPoints()) {
						stat.increment(clusterer.getMeasure().distance(point.getPoint(), center.getPoint()));
					}
					varianceSum += stat.getResult();
				}
			}

			if (varianceSum <= bestVarianceSum) {
				best = clusters;
				bestVarianceSum = varianceSum;
			}
		}

		// return the best clusters list found
		if (best == null) {
			return best;
		}
		return best;
	}

}
