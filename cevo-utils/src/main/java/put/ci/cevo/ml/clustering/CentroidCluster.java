package put.ci.cevo.ml.clustering;

import put.ci.cevo.ml.clustering.clusterable.Clusterable;

import java.util.Arrays;

import static com.google.common.base.Objects.toStringHelper;

public class CentroidCluster<T extends Clusterable> extends Cluster<T> {

	private final Clusterable center;

	public CentroidCluster(final Clusterable center) {
		this.center = center;
	}

	public Clusterable getCenter() {
		return center;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("center", Arrays.toString(center.getPoint())).toString();
	}

}
