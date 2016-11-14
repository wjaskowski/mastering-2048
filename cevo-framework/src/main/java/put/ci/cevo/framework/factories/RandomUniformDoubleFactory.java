package put.ci.cevo.framework.factories;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.RandomFactory;

public final class RandomUniformDoubleFactory implements RandomFactory<Double> {

	private final double min;
	private final double max;

	public RandomUniformDoubleFactory() {
		this(0.0, 1.0);
	}

	public RandomUniformDoubleFactory(double min, double max) {
		this.min = min;
		this.max = max;
		Preconditions.checkArgument(min < max);
	}

	@Override
	public Double create(RandomDataGenerator random) {
		return random.nextUniform(min, max);
	}
}
