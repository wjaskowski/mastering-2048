package put.ci.cevo.ml.clustering.meanshift.seed;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;

/**
 * Initial kernel locations are the location of the discretized version of points, where points are binned onto
 * a grid whose coarseness corresponds to the bandwidth. This may speed up the MeanShift algorithm because fewer
 * seeds will be initialized.
 */
public class BinSeed implements SeedingStrategy {

	@Override
	public List<ClusterableVector> getSeeds(List<ClusterableVector> points, double bandwidth, RandomDataGenerator random) {
		// TODO: implement seeding
		throw new NotImplementedException();
	}

}
