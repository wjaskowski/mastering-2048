package put.ci.cevo.experiments.wpc;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.GaussianMutation;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCGaussianMutation implements MutationOperator<WPC> {

	private final MutationAdapter<WPC, DoubleVector> mutation;

	@AccessedViaReflection
	public WPCGaussianMutation(double probability, double sigma) {
		this(probability, sigma, 0.0);
	}

	@AccessedViaReflection
	public WPCGaussianMutation(double probability, double sigma, double mu) {
		this.mutation = new MutationAdapter<>(
			new GaussianMutation(probability, sigma, mu), new WPCDoubleVectorAdapter());
	}

	@Override
	public WPC produce(WPC individual, RandomDataGenerator random) {
		return mutation.produce(individual, random);
	}

	@Override
	public String toString() {
		return "WPCGaussianMutation [mutation=" + mutation + "]";
	}
}
