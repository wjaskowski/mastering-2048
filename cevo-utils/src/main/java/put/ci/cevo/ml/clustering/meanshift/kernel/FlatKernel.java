package put.ci.cevo.ml.clustering.meanshift.kernel;

import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.List;

/**
 * Simple kernel which computes mean of all points.
 */
public class FlatKernel implements Kernel {

	@Override
	public ClusterableVector computeKernel(ClusterableVector x, List<ClusterableVector> points, double bandwidth) {
		DoubleVector aggregate = points.get(0).getVector();
		for (int i = 1; i < points.size(); i++) {
			aggregate = aggregate.add(points.get(i).getVector());
		}
		return ClusterableVector.wrap(aggregate.divide(points.size()));
	}

}
