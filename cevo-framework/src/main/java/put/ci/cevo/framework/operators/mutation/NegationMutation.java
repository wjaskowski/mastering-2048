package put.ci.cevo.framework.operators.mutation;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

import static com.google.common.base.Objects.toStringHelper;

public class NegationMutation implements MutationOperator<DoubleVector> {

	private final double probability;

	@AccessedViaReflection
	public NegationMutation(double probability) {
		Preconditions.checkArgument(probability >= 0 && probability <= 1, "Mutation probability must be between [0,1]");
		this.probability = probability;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, RandomDataGenerator random) {
		double[] child = individual.toArray();
		for (int i = 0; i < individual.size(); i++) {
			if (random.nextUniform(0, 1) < probability) {
				child[i] = -child[i];
			}
		}
		return new DoubleVector(child);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("p", probability).toString();
	}
}
