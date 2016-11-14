package put.ci.cevo.framework.state;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import static com.google.common.base.Objects.toStringHelper;

public class MeasuredIndividual<T> implements Comparable<MeasuredIndividual<T>>, Serializable {

	private static final long serialVersionUID = 6617758048817961059L;

	private final double performance;
	private final T individual;

	public static <T> MeasuredIndividual<T> createNull() {
		return new MeasuredIndividual<>(null, Double.NEGATIVE_INFINITY);
	}

	public MeasuredIndividual(T individual, double performance) {
		this.individual = individual;
		this.performance = performance;
	}

	public T getIndividual() {
		return individual;
	}

	public double getPerformance() {
		return performance;
	}

	public boolean isBetterThan(MeasuredIndividual<T> other) {
		return performance > other.performance;
	}

	@Override
	public int compareTo(MeasuredIndividual<T> evaluatedIndividual) {
		return Double.compare(performance, evaluatedIndividual.getPerformance());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		MeasuredIndividual other = (MeasuredIndividual) obj;
		return new EqualsBuilder().append(performance, other.performance).append(individual, other.individual).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(performance).append(individual).build();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("Individual", individual).add("Performance", performance).toString();
	}
}
