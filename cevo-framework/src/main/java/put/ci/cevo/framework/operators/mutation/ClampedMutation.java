package put.ci.cevo.framework.operators.mutation;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

public final class ClampedMutation implements MutationOperator<DoubleVector> {

	private final MutationOperator<DoubleVector> mutation;

	private final double lowerBound;
	private final double upperBound;

	@AccessedViaReflection
	public ClampedMutation(MutationOperator<DoubleVector> mutation, double min, double max) {
		Preconditions.checkArgument(min < max);

		this.lowerBound = min;
		this.upperBound = max;
		this.mutation = mutation;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, RandomDataGenerator random) {
		DoubleVector produced = mutation.produce(individual, random);
		double[] child = produced.toArray();
		for (int i = 0; i < individual.size(); i++) {
			child[i] = clamp(child[i]);
		}
		return new DoubleVector(child);
	}

	private double clamp(double weight) {
		if (weight > upperBound) {
			weight = upperBound;
		} else if (weight < lowerBound) {
			weight = lowerBound;
		}
		return weight;
	}

	@Override
	public String toString() {
		return "ClampedMutation [min=" + lowerBound + ", max=" + upperBound + ", mutation=" + mutation + "]";
	}
}
