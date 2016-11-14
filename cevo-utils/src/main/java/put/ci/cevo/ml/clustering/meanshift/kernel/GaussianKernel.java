package put.ci.cevo.ml.clustering.meanshift.kernel;

import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.List;

import static org.apache.commons.math3.util.FastMath.exp;
import static put.ci.cevo.ml.clustering.clusterable.ClusterableVector.wrap;
import static put.ci.cevo.util.sequence.Sequences.seq;

/**
 * Gaussian kernel, a common kernel in the literature, finds dense regions near the centres by weighting distances
 * between the points according to the Normal distribution. The bandwidth parameter acts as the standard deviation
 * for the kernel.
 */
public class GaussianKernel implements Kernel {

	@Override
	public ClusterableVector computeKernel(ClusterableVector x, List<ClusterableVector> points, double bandwidth) {
		List<Double> distances = computeDistances(x, points);
		List<Double> weights = computeWeights(distances, bandwidth);

		DoubleVector weightedSum = DoubleVector.zeros(x.getVector().size());
		double weightsSum = 0;
		for (int i = 0; i < points.size(); i++) {
			weightedSum = weightedSum.add(points.get(i).getVector().multiply(weights.get(i)));
			weightsSum += weights.get(i);
		}
		return wrap(weightedSum.divide(weightsSum));
	}

	private List<Double> computeDistances(final ClusterableVector x, List<ClusterableVector> points) {
		return seq(points).map(new Transform<ClusterableVector, Double>() {
			@Override
			public Double transform(ClusterableVector vector) {
				return x.getVector().distanceTo(vector.getVector(), new EuclideanDistance());
			}
		}).toList();
	}

	private List<Double> computeWeights(List<Double> distances, final double bandwidth) {
		return seq(distances).map(new Transform<Double, Double>() {
			@Override
			public Double transform(Double distance) {
				return exp(-1 * (distance * distance) / (bandwidth * bandwidth));
			}
		}).toList();
	}

}

