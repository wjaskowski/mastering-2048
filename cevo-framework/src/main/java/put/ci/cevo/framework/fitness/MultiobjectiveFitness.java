package put.ci.cevo.framework.fitness;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static com.google.common.primitives.Doubles.asList;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.transforms.Transforms.negativeValue;

public class MultiobjectiveFitness implements Fitness {

	private final ImmutableList<Double> fitness;

	public MultiobjectiveFitness(double[] fitness) {
		this(asList(fitness));
	}

	public MultiobjectiveFitness(List<Double> fitness) {
		this.fitness = ImmutableList.copyOf(fitness);
	}

	public List<Double> getObjectives() {
		return fitness;
	}

	public double getObjective(int idx) {
		return fitness.get(idx);
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
		MultiobjectiveFitness other = (MultiobjectiveFitness) obj;
		return new EqualsBuilder().append(fitness, other.fitness).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(fitness).hashCode();
	}

	@Override
	public int compareTo(Fitness o) {
		MultiobjectiveFitness otherFitness = (MultiobjectiveFitness) o;
		if (paretoDominates(getObjectives(), otherFitness.getObjectives())) {
			return 1;
		} else if (paretoDominates(otherFitness.getObjectives(), getObjectives())) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean betterThan(Fitness other) {
		return paretoDominates((MultiobjectiveFitness) other);
	}

	@Override
	public double fitness() {
//		throw new NotImplementedException();
		return getObjective(0);
	}

	public int compareOnObjective(MultiobjectiveFitness o, int objective) {
		return Doubles.compare(getObjective(objective), o.getObjective(objective));
	}

	public boolean paretoDominates(MultiobjectiveFitness other) {
		return paretoDominates(getObjectives(), other.getObjectives());
	}

	public static boolean paretoDominates(List<Double> first, List<Double> second) {
		Preconditions.checkArgument(first.size() == second.size(),
			"Unable to compare individuals evaluated on different objectives");
		boolean dominates = false;
		for (int i = 0; i < first.size(); i++) {
			if (first.get(i) > second.get(i)) {
				dominates = true;
			} else if (first.get(i) < second.get(i)) {
				return false;
			}
		}
		return dominates;
	}

	@Override
	public Fitness negate() {
		return new MultiobjectiveFitness(seq(fitness).map(negativeValue()).toList());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("fit", StringUtils.join(fitness, " ")).toString();
	}

}
