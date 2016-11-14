package put.ci.cevo.experiments.ipd;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class IPDVectorIndividualFactory implements IndividualFactory<IPDVector> {

	private final int choices;

	@AccessedViaReflection
	public IPDVectorIndividualFactory(int choices) {
		this.choices = choices;
	}

	@Override
	public IPDVector createRandomIndividual(RandomDataGenerator random) {
		int genomeLength = choices * choices + 1;
		int[] vector = new int[genomeLength];
		for (int i = 0; i < genomeLength; ++i) {
			vector[i] = random.nextInt(0, choices - 1);
		}
		return new IPDVector(vector);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("size", choices * choices + 1).toString();
	}

}
