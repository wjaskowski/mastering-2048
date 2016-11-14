package put.ci.cevo.framework.vectors;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

import static put.ci.cevo.util.RandomUtils.randomDoubleVector;

public class DoubleVectorFactory implements IndividualFactory<DoubleVector> {

	private final int numElements;

	private final double minValue;
	private final double maxValue;

	@AccessedViaReflection
	public DoubleVectorFactory(int numElements, double minValue, double maxValue) {
		this.numElements = numElements;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public DoubleVector createRandomIndividual(RandomDataGenerator random) {
		return new DoubleVector(randomDoubleVector(numElements, minValue, maxValue, random));
	}
}
