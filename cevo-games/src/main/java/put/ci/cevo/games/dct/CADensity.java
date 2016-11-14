package put.ci.cevo.games.dct;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CADensity implements CATest {

	private final double density;
	private final int testLength;

	public CADensity(double density, int testLength) {
		this.density = density;
		this.testLength = testLength;
	}

	@Override
	public double getDensity() {
		return density;
	}

	public int getTestLength() {
		return testLength;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(density).append(testLength).hashCode();
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
		CADensity other = (CADensity) obj;
		return new EqualsBuilder().append(density, other.density).append(testLength, other.testLength).isEquals();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("density", density).add("len", testLength).toString();
	}

}
