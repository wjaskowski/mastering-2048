package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;

public class CartPoleEnvironmentMutation implements MutationOperator<CartPoleEnvironment> {

	private double poleLengthMutation;
	private double poleMassMutation;
	private double cartMassMutation;
	private double gravityMutation;

	private double maxPoleLength;
	private double maxPoleMass;
	private double maxCartMass;
	private double maxGravity;

	public CartPoleEnvironmentMutation(double poleLengthMutation, double maxPoleLength, double poleMassMutation,
			double maxPoleMass, double cartMassMutation, double maxCartMass, double gravityMutation, double maxGravity) {
		this.poleLengthMutation = poleLengthMutation;
		this.maxPoleLength = maxPoleLength;
		this.poleMassMutation = poleMassMutation;
		this.maxPoleMass = maxPoleMass;
		this.cartMassMutation = cartMassMutation;
		this.maxCartMass = maxCartMass;
		this.gravityMutation = gravityMutation;
		this.maxGravity = maxGravity;
	}

	@Override
	public CartPoleEnvironment produce(CartPoleEnvironment env, RandomDataGenerator random) {
		int numPoles = env.getNumPoles();

		double[] poleLengths = env.getPoleLengths();
		for (int p = 0; p < numPoles; p++) {
			poleLengths[p] += random.nextUniform(-poleLengthMutation, poleLengthMutation);
			poleLengths[p] = Math.min(maxPoleLength, Math.max(0, poleLengths[p]));
		}

		double[] poleMasses = env.getPoleMasses();
		for (int p = 0; p < numPoles; p++) {
			poleMasses[p] += random.nextUniform(-poleMassMutation, poleMassMutation);
			poleMasses[p] = Math.min(maxPoleMass, Math.max(0, poleMasses[p]));
		}

		double cartMass = env.getCartMass();
		cartMass += random.nextUniform(-cartMassMutation, cartMassMutation);
		cartMass = Math.min(maxCartMass, Math.max(0, cartMass));
				
		double gravity = env.getGravity();
		gravity += random.nextUniform(-gravityMutation, gravityMutation);
		gravity = Math.min(maxGravity, Math.max(0, gravity));

		return new CartPoleEnvironment(numPoles, poleLengths, poleMasses, cartMass, gravity);
	}
}
