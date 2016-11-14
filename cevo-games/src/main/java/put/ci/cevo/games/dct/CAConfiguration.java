package put.ci.cevo.games.dct;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.google.common.base.Objects.toStringHelper;

public class CAConfiguration implements CATest {

	private final int[] configuration;
	private final double density;

	public CAConfiguration(int[] configuration) {
		this(configuration, computeDensity(configuration));
	}

	private static double computeDensity(int[] configuration) {
		double cnt = 0;
		for (int i = 0; i < configuration.length; i++) {
			if (configuration[i] == 1) {
				cnt++;
			}
		}
		return cnt / configuration.length;
	}

	public CAConfiguration(int[] configuration, double density) {
		this.configuration = configuration;
		this.density = density;
	}

	@Override
	public double getDensity() {
		return density;
	}

	public int getTarget() {
		return density > 0.5 ? 1 : 0;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(density).append(configuration).hashCode();
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
		CAConfiguration other = (CAConfiguration) obj;
		return new EqualsBuilder().append(density, other.density).append(configuration, other.configuration).isEquals();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("len", configuration.length).add("dens", density).toString();
	}

	public int[] toArray() {
		return configuration.clone();
	}

}
