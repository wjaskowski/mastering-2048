package put.ci.cevo.framework.operators.mutation;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

public class AdaptedUniformMutation<T> implements MutationOperator<T> {

	private final MutationOperator<T> mutation;

	@AccessedViaReflection
	public AdaptedUniformMutation(double min, double max, double probability, IndividualAdapter<T, DoubleVector> adapter) {
		this(min, max, probability, NEGATIVE_INFINITY, POSITIVE_INFINITY, adapter);
	}

	@AccessedViaReflection
	public AdaptedUniformMutation(double min, double max, double probability, double lowerBound, double upperBound,
			IndividualAdapter<T, DoubleVector> adapter) {
		this.mutation = new MutationAdapter<>(new ClampedMutation(
			new UniformMutation(min, max, probability), lowerBound, upperBound), adapter);
	}

	@Override
	public String toString() {
		return "AdaptedUniformMutation [mutation=" + mutation + "]";
	}

	@Override
	public T produce(T individual, RandomDataGenerator random) {
		return mutation.produce(individual, random);
	}
}
