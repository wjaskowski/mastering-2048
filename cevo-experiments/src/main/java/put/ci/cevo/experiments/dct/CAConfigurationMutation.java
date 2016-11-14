package put.ci.cevo.experiments.dct;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.vectors.IntegerVector;
import put.ci.cevo.games.dct.CAConfiguration;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class CAConfigurationMutation implements MutationOperator<CAConfiguration> {

	private final double probability;

	@AccessedViaReflection
	public CAConfigurationMutation(double probability) {
		this.probability = probability;
	}

	@Override
	public CAConfiguration produce(CAConfiguration individual, RandomDataGenerator random) {
		BitStringMutation mutation = new BitStringMutation(probability);
		IntegerVector child = mutation.produce(new IntegerVector(individual.toArray()), random);
		return new CAConfiguration(child.getVector());
	}
}
