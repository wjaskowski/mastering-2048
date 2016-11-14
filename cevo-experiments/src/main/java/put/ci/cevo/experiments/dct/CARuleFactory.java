package put.ci.cevo.experiments.dct;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.dct.CARule;
import put.ci.cevo.util.MathUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class CARuleFactory implements IndividualFactory<CARule> {

	private final int radius;

	@AccessedViaReflection
	public CARuleFactory(int radius) {
		this.radius = radius;
	}

	@Override
	public CARule createRandomIndividual(RandomDataGenerator random) {
		int genomeLength = MathUtils.ipow(2, 2 * radius + 1);
		int[] vector = new int[genomeLength];
		for (int i = 0; i < genomeLength; ++i) {
			vector[i] = random.nextBinomial(1, 0.5);
		}
		return new CARule(vector);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("size", radius * radius + 1).toString();
	}

}
