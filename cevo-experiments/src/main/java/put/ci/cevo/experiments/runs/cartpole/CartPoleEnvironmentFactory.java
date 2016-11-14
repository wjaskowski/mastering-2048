package put.ci.cevo.experiments.runs.cartpole;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class CartPoleEnvironmentFactory implements IndividualFactory<CartPoleEnvironment> {

	private int numPoles;
	private double maxPoleLength;
	private double maxPoleMass;
	private double maxCartMass;
	private double maxGravity;

	private boolean changeGravity;
	private CartPoleEnvironment environment = null;

	// FIXME(Marcin): numPoles parameter is not used. This might be a bug.
	@AccessedViaReflection
	public CartPoleEnvironmentFactory(int numPoles) {
		this.environment = new CartPoleEnvironment(2);
	}

	@AccessedViaReflection
	public CartPoleEnvironmentFactory(int numPoles, double maxPoleLength, double maxPoleMass, double maxCartMass) {
		this.numPoles = numPoles;
		this.maxPoleLength = maxPoleLength;
		this.maxPoleMass = maxPoleMass;
		this.maxCartMass = maxCartMass;

		this.changeGravity = false;
	}

	@AccessedViaReflection
	public CartPoleEnvironmentFactory(int numPoles, double maxPoleLength, double maxPoleMass, double maxCartMass,
			double maxGravity) {
		this.numPoles = numPoles;
		this.maxPoleLength = maxPoleLength;
		this.maxPoleMass = maxPoleMass;
		this.maxCartMass = maxCartMass;

		this.changeGravity = true;
		this.maxGravity = maxGravity;
	}

	@Override
	public CartPoleEnvironment createRandomIndividual(RandomDataGenerator random) {
		if (environment != null) {
			return environment;
		}

		double[] poleLengths = RandomUtils.randomDoubleVector(numPoles, 0, maxPoleLength, random);
		double[] poleMasses = RandomUtils.randomDoubleVector(numPoles, 0, maxPoleMass, random);
		double cartMass = random.nextUniform(0, maxCartMass);

		if (changeGravity) {
			double gravity = random.nextUniform(-maxGravity, 0);
			return new CartPoleEnvironment(numPoles, poleLengths, poleMasses, cartMass, gravity);
		} else {
			return new CartPoleEnvironment(numPoles, poleLengths, poleMasses, cartMass);
		}
	}
}
