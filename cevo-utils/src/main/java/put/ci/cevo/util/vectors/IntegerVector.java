package put.ci.cevo.util.vectors;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import com.google.common.primitives.Ints;

public class IntegerVector implements Serializable {

	private static final long serialVersionUID = 20130809002516L;

	private final int[] vector;

	public IntegerVector(int[] vector) {
		this.vector = vector;
	}

	public IntegerVector(Collection<Integer> vector) {
		this.vector = Ints.toArray(vector);
	}

	public double get(int idx) {
		return vector[idx];
	}

	public int[] getVector() {
		return vector;
	}

	public int getSize() {
		return vector.length;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(vector);
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
		IntegerVector other = (IntegerVector) obj;
		if (!Arrays.equals(vector, other.vector)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(vector);
	}
}
