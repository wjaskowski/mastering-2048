package put.ci.cevo.experiments.dct;

import static com.google.common.base.Objects.toStringHelper;
import static java.lang.Math.round;
import static put.ci.cevo.util.RandomUtils.shuffleInts;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.dct.CAConfiguration;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class CAConfigurationFactory implements IndividualFactory<CAConfiguration> {

	private final int testLength;

	@AccessedViaReflection
	public CAConfigurationFactory(int testLength) {
		this.testLength = testLength;
	}

	@Override
	public CAConfiguration createRandomIndividual(RandomDataGenerator random) {
		final double density = random.nextUniform(0, 1);
		final long numOnes = round(density * testLength);

		int[] configuration = new int[testLength];
		for (int i = 0; i < numOnes; i++) {
			configuration[i] = 1;
		}
		return new CAConfiguration(shuffleInts(configuration, random), density);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("testLength", testLength).toString();
	}

	public static CAConfiguration fromBinomialDistribution(int testLength, RandomDataGenerator random) {
		final double density = random.nextUniform(0, 1);
		final int[] vector = new int[testLength];
		for (int i = 0; i < testLength; ++i) {
			vector[i] = random.nextBinomial(1, density);
		}
		return new CAConfiguration(vector, density);
	}

}