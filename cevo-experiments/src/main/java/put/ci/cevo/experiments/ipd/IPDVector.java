package put.ci.cevo.experiments.ipd;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import put.ci.cevo.util.vectors.IntegerVector;

/**
 * Wrapper for {@link IntegerVector}. Currently for convenience only.
 */
public class IPDVector implements Serializable {

	private static final long serialVersionUID = 6108492253723943720L;

	private final IntegerVector vector;

	public IPDVector(int[] vector) {
		this(new IntegerVector(vector));
	}

	public IPDVector(Collection<Integer> vector) {
		this(new IntegerVector(vector));
	}

	public IPDVector(IntegerVector vector) {
		this.vector = vector;
	}

	public IntegerVector getIntegerVector() {
		return vector;
	}

	public int[] getVector() {
		return vector.getVector();
	}

	public int getChoices() {
		return (int) Math.sqrt(vector.getSize() - 1);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(vector).toHashCode();
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
		IPDVector other = (IPDVector) obj;
		return new EqualsBuilder().append(vector, other.vector).isEquals();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("vector", vector).toString();
	}
}
