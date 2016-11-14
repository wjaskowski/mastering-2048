package put.ci.cevo.experiments.wpc;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.ClampedMutation;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.operators.mutation.UniformMutation;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCUniformMutation implements MutationOperator<WPC> {

	private final MutationOperator<WPC> mutation;

	@AccessedViaReflection
	public WPCUniformMutation(double min, double max, double probability) {
		this(min, max, probability, NEGATIVE_INFINITY, POSITIVE_INFINITY);
	}

	@AccessedViaReflection
	public WPCUniformMutation(double min, double max, double probability, double lowerBound, double upperBound) {
		this.mutation = new MutationAdapter<>(new ClampedMutation(
			new UniformMutation(min, max, probability), lowerBound, upperBound), new WPCDoubleVectorAdapter());
	}

	@Override
	public WPC produce(WPC individual, RandomDataGenerator random) {
		return mutation.produce(individual, random);
	}

	@Override
	public String toString() {
		return "WPCUniformMutation [mutation=" + mutation + "]";
	}
}
