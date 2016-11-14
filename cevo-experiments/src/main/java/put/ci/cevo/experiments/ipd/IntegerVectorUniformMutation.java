package put.ci.cevo.experiments.ipd;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.vectors.IntegerVector;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import com.google.common.base.Preconditions;

public class IntegerVectorUniformMutation implements MutationOperator<IntegerVector> {

	private final double probability;

	private final int min;
	private final int max;

	private final boolean replace;

	@AccessedViaReflection
	public IntegerVectorUniformMutation(int min, int max, double probability) {
		this(min, max, probability, true);
	}

	@AccessedViaReflection
	public IntegerVectorUniformMutation(int min, int max, double probability, boolean replace) {
		Preconditions.checkArgument(min < max);
		this.probability = probability;
		this.min = min;
		this.max = max;
		this.replace = replace;
	}

	@Override
	public IntegerVector produce(IntegerVector individual, RandomDataGenerator random) {
		return replace ? mutateWithReplacement(individual, random) : mutateWithoutReplacement(individual, random);
	}

	private IntegerVector mutateWithReplacement(IntegerVector individual, RandomDataGenerator random) {
		int[] childVector = individual.getVector().clone();
		for (int i = 0; i < individual.getSize(); i++) {
			if (random.nextUniform(0, 1) < probability) {
				childVector[i] = random.nextInt(min, max);
			}
		}
		return new IntegerVector(childVector);
	}

	private IntegerVector mutateWithoutReplacement(IntegerVector individual, RandomDataGenerator random) {
		int[] child = individual.getVector().clone();
		for (int i = 0; i < individual.getSize(); i++) {
			if (random.nextUniform(0, 1) < probability) {
				int gene = RandomUtils.nextInt(min, max - 1, random);
				if (individual.get(i) == gene) {
					gene = max;
				}
				child[i] = gene;
			}
		}
		return new IntegerVector(child);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("uniform", "[" + min + ", " + max + "]").add("p", probability).toString();
	}
}
