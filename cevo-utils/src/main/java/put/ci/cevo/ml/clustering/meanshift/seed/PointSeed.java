package put.ci.cevo.ml.clustering.meanshift.seed;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;

import java.util.List;

/**
 * Initial kernel locations are simply locations of all the points.
 */
public class PointSeed implements SeedingStrategy {

	@Override
	public List<ClusterableVector> getSeeds(List<ClusterableVector> points, double bandwidth, RandomDataGenerator random) {
		return ImmutableList.copyOf(points);
	}

}
