package put.ci.cevo.ml.clustering.clusterable;

import put.ci.cevo.util.vectors.DoubleVector;

public class ClusterableVector implements Clusterable {

	private final DoubleVector vector;

	public ClusterableVector(double[] vector) {
		this(new DoubleVector(vector));
	}

	private ClusterableVector(DoubleVector vector) {
		this.vector = vector;
	}

	public DoubleVector getVector() {
		return vector;
	}

	@Override
	public double[] getPoint() {
		return vector.toArray();
	}

	@Override
	public int hashCode() {
		return vector.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClusterableVector other = (ClusterableVector) obj;
		return vector.equals(other.vector);
	}

	@Override
	public String toString() {
		return vector.toString();
	}

	public static ClusterableVector wrap(DoubleVector vector) {
		return new ClusterableVector(vector);
	}
}
