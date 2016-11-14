package put.ci.cevo.ml.clustering.meanshift.seed;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;

import java.util.List;

public interface SeedingStrategy {

	public List<ClusterableVector> getSeeds(List<ClusterableVector> points, double bandwidth, RandomDataGenerator random);

}
