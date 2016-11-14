package put.ci.cevo.experiments.dct;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.vectors.IntegerVector;
import put.ci.cevo.games.dct.CARule;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class CARuleMutation implements MutationOperator<CARule> {

	private final double probability;

	@AccessedViaReflection
	public CARuleMutation(double probability) {
		this.probability = probability;
	}

	@Override
	public CARule produce(CARule individual, RandomDataGenerator random) {
		BitStringMutation mutation = new BitStringMutation(probability);
		IntegerVector child = mutation.produce(new IntegerVector(individual.toArray()), random);
		return new CARule(child.getVector());
	}
}
