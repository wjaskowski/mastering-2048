package put.ci.cevo.ml.clustering;

import put.ci.cevo.ml.clustering.clusterable.Clusterable;

import java.util.Arrays;

import static com.google.common.base.Objects.toStringHelper;

public class MedoidCluster<T extends Clusterable> extends Cluster<T> {

	private final T medoid;

	public MedoidCluster(T medoid) {
		this.medoid = medoid;
	}

	public T getMedoid() {
		return medoid;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("medoid", Arrays.toString(medoid.getPoint())).toString();
	}
}
