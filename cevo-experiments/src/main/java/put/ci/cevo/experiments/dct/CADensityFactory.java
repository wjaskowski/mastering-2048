package put.ci.cevo.experiments.dct;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.dct.CADensity;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import static com.google.common.base.Objects.toStringHelper;

public class CADensityFactory implements IndividualFactory<CADensity> {

	private final int testLength;

	private final double lower;
	private final double upper;

	@AccessedViaReflection
	public CADensityFactory(int testLength) {
		this(testLength, 0, 1);
	}

	public CADensityFactory(int testLength, double lower, double upper) {
		this.testLength = testLength;
		this.lower = lower;
		this.upper = upper;
	}

	@Override
	public CADensity createRandomIndividual(RandomDataGenerator random) {
		double density = random.nextUniform(lower, upper);
		return new CADensity(density, testLength);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("testLength", testLength).toString();
	}

	public static CADensityFactory difficultTestsFactory(int testLength) {
		return new CADensityFactory(testLength, 0.4, 0.6);
	}
}
