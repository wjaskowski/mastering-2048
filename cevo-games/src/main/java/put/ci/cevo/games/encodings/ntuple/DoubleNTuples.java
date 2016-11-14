package put.ci.cevo.games.encodings.ntuple;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents two NTuples. One used by the first player, second for the second one.
 */
public final class DoubleNTuples implements Serializable {

	private static final long serialVersionUID = -6064217321930887567L;
	private final NTuples first;
	private final NTuples second;

	public DoubleNTuples(NTuples first, NTuples second) {
		this.first = first;
		this.second = second;
	}

	public NTuples first() {
		return first;
	}

	public NTuples second() {
		return second;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DoubleNTuples that = (DoubleNTuples) o;

		return first.equals(that.first) && second.equals(that.second);
	}

	@Override
	public int hashCode() {
		int result = first.hashCode();
		result = 31 * result + second.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public int totalWeights() {
		return first.totalWeights() + second.totalWeights();
	}
}
