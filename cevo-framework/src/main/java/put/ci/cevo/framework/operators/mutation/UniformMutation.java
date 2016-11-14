package put.ci.cevo.framework.operators.mutation;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

import static com.google.common.base.Objects.toStringHelper;

public class UniformMutation implements MutationOperator<DoubleVector> {

	private final double probability;

	private final double min;
	private final double max;

	@AccessedViaReflection
	public UniformMutation(double min, double max, double probability) {
		Preconditions.checkArgument(probability >= 0 && probability <= 1, "Mutation probability must belong to [0,1]");
		Preconditions.checkArgument(min < max, "max must be greater than min!");
		this.probability = probability;
		this.min = min;
		this.max = max;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, RandomDataGenerator random) {
		double[] child = individual.toArray();
		for (int i = 0; i < individual.size(); i++) {
			if (random.nextUniform(0, 1) < probability) {
				double noise = random.nextUniform(min, max);
				child[i] = individual.get(i) + noise;
			}
		}
		return new DoubleVector(child);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("uniform", "(" + min + ", " + max + "]").add("p", probability).toString();
	}
}
