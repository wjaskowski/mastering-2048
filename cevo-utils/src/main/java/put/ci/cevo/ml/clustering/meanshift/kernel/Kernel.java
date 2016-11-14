package put.ci.cevo.ml.clustering.meanshift.kernel;

import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;

import java.util.List;

public interface Kernel {

	public ClusterableVector computeKernel(ClusterableVector x, List<ClusterableVector> points, double bandwidth);

}
