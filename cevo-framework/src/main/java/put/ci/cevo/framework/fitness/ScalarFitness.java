package put.ci.cevo.framework.fitness;

import com.google.common.primitives.Doubles;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.google.common.base.Objects.toStringHelper;

public class ScalarFitness implements Fitness {

	private final double fitness;

	public ScalarFitness(double fitness) {
		this.fitness = fitness;
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
		ScalarFitness other = (ScalarFitness) obj;
		return new EqualsBuilder().append(fitness, other.fitness).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(fitness).hashCode();
	}

	@Override
	public int compareTo(Fitness o) {
		return Doubles.compare(fitness(), o.fitness());
	}

	@Override
	public double fitness() {
		return fitness;
	}

	@Override
	public boolean betterThan(Fitness other) {
		return fitness > other.fitness();
	}

	@Override
	public Fitness negate() {
		return new ScalarFitness(-fitness);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("fitness", fitness).toString();
	}
}
