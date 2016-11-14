package put.ci.cevo.framework.operators.mutation;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

/** Weight is mutated according to its value, i.e. w = w + ratio * uniform[-w,+w] **/
public class AdaptiveUniformMutation implements MutationOperator<DoubleVector> {

	private final double probability;
	private final double ratio;

	@AccessedViaReflection
	public AdaptiveUniformMutation(double ratio, double probability) {
		Preconditions.checkArgument(probability >= 0 && probability <= 1, "Mutation probability must belong to [0,1]");
		this.ratio = ratio;
		this.probability = probability;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, RandomDataGenerator random) {
		double[] child = individual.toArray();
		for (int i = 0; i < individual.size(); i++) {
			if (random.nextUniform(0, 1) < probability) {
				double x = Math.abs(ratio * individual.get(i));
				double noise = RandomUtils.nextUniform(-x, x, random);
				child[i] = individual.get(i) + noise;
			}
		}
		return new DoubleVector(child);
	}
}
