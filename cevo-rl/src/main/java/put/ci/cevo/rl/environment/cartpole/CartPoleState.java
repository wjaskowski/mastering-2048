package put.ci.cevo.rl.environment.cartpole;

import java.io.Serializable;

import put.ci.cevo.rl.environment.State;

public class CartPoleState implements State, Serializable {

	private static final long serialVersionUID = 6000165909306987433L;

	private double cartPosition;
	private double cartVelocity;

	private double[] poleAngles;
	private double[] poleVelocities;

	public CartPoleState(int numPoles) {
		poleAngles = new double[numPoles];
		poleVelocities = new double[numPoles];
	}

	public CartPoleState(double cartPosition, double cartVelocity, double[] poleAngle, double[] poleVelocity) {
		this.cartPosition = cartPosition;
		this.cartVelocity = cartVelocity;
		this.poleAngles = poleAngle.clone();
		this.poleVelocities = poleVelocity.clone();
	}

	public CartPoleState(double[] features) {
		cartPosition = features[0];
		cartVelocity = features[1];

		poleAngles = new double[(features.length / 2) - 1];
		poleVelocities = new double[(features.length / 2) - 1];
		for (int i = 2; i < features.length; i += 2) {
			poleAngles[(i / 2) - 1] = features[i];
			poleVelocities[(i / 2) - 1] = features[i + 1];
		}
	}

	@Override
	public double[] getFeatures() {
		double[] features = new double[poleAngles.length * 2 + 2];
		features[0] = cartPosition;
		features[1] = cartVelocity;
		for (int i = 0; i < poleAngles.length; i++) {
			features[2 + i * 2] = poleAngles[i];
			features[3 + i * 2] = poleVelocities[i];
		}

		return features;
	}

	public double getCartPosition() {
		return cartPosition;
	}

	public double getCartVelocity() {
		return cartVelocity;
	}

	public double getPoleAngle(int pole) {
		return poleAngles[pole];
	}

	public double getPoleVelocity(int pole) {
		return poleVelocities[pole];
	}

	public double[] getPoleVelocities() {
		return poleVelocities.clone();
	}
}
