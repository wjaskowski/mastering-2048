package put.ci.cevo.ml.clustering.meanshift.seed;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.ml.neighbors.NearestNeighbors;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Performs one pass over the dataset to determine the first centers that are within the bandwidth range.
 */
public class MeanSeed implements SeedingStrategy {

	public List<ClusterableVector> getSeeds(List<ClusterableVector> points, double bandwidth, RandomDataGenerator random) {
		final Set<ClusterableVector> assigned = new HashSet<>();
		final List<ClusterableVector> seeds = new ArrayList<>();

		final NearestNeighbors<ClusterableVector> nbrs = new NearestNeighbors<>(points, new EuclideanDistance());
		for (int i = 0; i < points.size(); i++) {
			ClusterableVector vector = points.get(i);
			if (assigned.contains(vector)) {
				continue;
			}
			DoubleVector center = DoubleVector.zeros(vector.getVector().size());
			int added = 0;
			for (ClusterableVector neighbor : nbrs.nearest(vector, bandwidth)) {
				DoubleVector neighborVector = neighbor.getVector();
				if (!assigned.contains(neighbor)) {
					center = center.add(neighborVector);
					assigned.add(neighbor);
					added++;
				}
			}
			if (added != 0) {
				seeds.add(ClusterableVector.wrap(center.divide(added)));
			}
			assigned.add(ClusterableVector.wrap(vector.getVector()));
		}
		return seeds;
	}
}
