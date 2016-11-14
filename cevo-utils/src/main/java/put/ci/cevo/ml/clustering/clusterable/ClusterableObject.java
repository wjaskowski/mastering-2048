package put.ci.cevo.ml.clustering.clusterable;

import put.ci.cevo.util.vectors.DoubleVector;

import java.util.List;

import static com.google.common.primitives.Doubles.toArray;

/**
 * Generic container for an object to be included in a clustering process. Characterized by a vector of features.
 */
public class ClusterableObject<T> implements Clusterable {

	private final T object;
	private final DoubleVector features;

	public ClusterableObject(T object, List<Double> features) {
		this(object, new DoubleVector(toArray(features)));
	}

	public ClusterableObject(T object, double[] features) {
		this(object, new DoubleVector(features));
	}

	public ClusterableObject(T object, DoubleVector features) {
		this.object = object;
		this.features = features;
	}

	public T getObject() {
		return object;
	}

	public DoubleVector getFeatures() {
		return features;
	}

	@Override
	public double[] getPoint() {
		return getFeatures().toArray();
	}
}
