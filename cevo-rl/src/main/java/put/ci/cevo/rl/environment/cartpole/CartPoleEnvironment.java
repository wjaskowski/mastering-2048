package put.ci.cevo.rl.environment.cartpole;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.environment.*;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

/**
 * 
 * Implementation of a cart pole balancing environment. The environment dynamics is described in the following paper:
 * Gomez, F., Schmidhuber, J., & Miikkulainen, R. (2008). Accelerated neural evolution through cooperatively coevolved
 * synapses.
 * 
 * This code is a port of Faustino Gomez's C++ code available at http://nn.cs.utexas.edu/?esp.
 */
public class CartPoleEnvironment implements Environment<CartPoleState, ContinuousAction>,
		EnvironmentEncoder<CartPoleState, ContinuousAction>, Serializable {

	private static final long serialVersionUID = 1682776092218913871L;

	public static final int NUM_INPUTS_ONE_POLE = 4;
	public static final int NUM_INPUTS_TWO_POLES = 6;

	@SuppressWarnings("unused")
	private static final double CART_FRICTION = 0.0005;

	private static final double FOUR_DEGREES = 0.07;
	private static final double TWELVE_DEGREES = 0.2094384;
	private static final double DEFAULT_TRACK_LENGTH = 2.4;
	private static final double POLE_FRICTION = 0.000002;
	private static final double CART_MASS = 1.0;
	private static final double GRAVITY = -9.8;
	private static final double TAU = 0.01;

	private static final double[] POLE_MASSES = new double[] { 0.1, 0.01 };
	private static final double[] POLE_LENGTHS = new double[] { 0.5, 0.05 };

	private static final double MAX_FORCE = 10;
	private static final double[] ACTION_VALUES = new double[] { 0.01, 0.025, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7,
		0.8, 0.9, 1.0 };

	private final List<ContinuousAction> possibleActions = new ArrayList<>();

	private final double failureAngle;
	private final double trackLength;

	private final int numPoles;
	private final double[] poleMasses;
	private final double[] poleLengths;
	private final double cartMass;
	private final double gravity;

	private final double[] ml;

	public CartPoleEnvironment(int numPoles) {
		this(numPoles, POLE_LENGTHS, POLE_MASSES, CART_MASS, GRAVITY);
	}

	public CartPoleEnvironment(int numPoles, double[] poleLengths, double[] poleMasses, double cartMass) {
		this(numPoles, poleLengths, poleMasses, cartMass, GRAVITY);
	}

	public CartPoleEnvironment(int numPoles, double[] poleLengths, double[] poleMasses, double cartMass, double gravity) {
		this.numPoles = numPoles;
		this.poleLengths = poleLengths.clone();
		this.poleMasses = poleMasses.clone();
		this.cartMass = cartMass;
		this.gravity = gravity;

		this.failureAngle = TWELVE_DEGREES;
		this.trackLength = DEFAULT_TRACK_LENGTH;
		for (double actionValue : ACTION_VALUES) {
			possibleActions.add(new ContinuousAction(-actionValue));
			possibleActions.add(new ContinuousAction(actionValue));
		}

		ml = new double[numPoles];
		for (int i = 0; i < numPoles; i++) {
			ml[i] = poleLengths[i] * poleMasses[i];
		}
	}

	public double[] getPoleMasses() {
		return poleMasses.clone();
	}

	public double[] getPoleLengths() {
		return poleLengths.clone();
	}

	public double getCartMass() {
		return cartMass;
	}

	public double getGravity() {
		return gravity;
	}

	public int getNumPoles() {
		return numPoles;
	}
	
	@Override
	public AgentTransition<CartPoleState, ContinuousAction> getAgentTransition(CartPoleState state,
			ContinuousAction action) {
		CartPoleState afterState = computeAfterState(state, action.getValue());
		return new AgentTransition<>(state, action, 0, afterState);
	}

	private CartPoleState computeAfterState(CartPoleState state, double action) {
		double force = action * MAX_FORCE;
		double[] variables = state.getFeatures();
		double[] derivatives = new double[2 + (numPoles * 2)];

		for (int i = 0; i < 2; i++) {
			for (int pole = 0; pole <= numPoles; pole++) {
				derivatives[2 * pole] = variables[2 * pole + 1];
			}
			step(force, variables, derivatives);
			RungeKutta4(force, variables, derivatives, variables);
		}

		return new CartPoleState(variables);
	}

	public void step(double force, double[] variables, double[] derivatives) {
		double[] costheta = new double[numPoles];
		double[] sintheta = new double[numPoles];
		double[] gsintheta = new double[numPoles];
		double[] temp = new double[numPoles];

		double sumFi = 0;
		double sumMi = 0;

		for (int pole = 0; pole < numPoles; pole++) {
			costheta[pole] = Math.cos(variables[2 + (2 * pole)]);
			sintheta[pole] = Math.sin(variables[2 + (2 * pole)]);
			gsintheta[pole] = gravity * sintheta[pole];
			temp[pole] = POLE_FRICTION * variables[3 + (2 * pole)] / ml[pole];

			double fi = (ml[pole] * variables[3 + (2 * pole)] * variables[3 + (2 * pole)] * sintheta[pole])
				+ (0.75 * poleMasses[pole] * costheta[pole] * (temp[pole] + gsintheta[pole]));
			double mi = poleMasses[pole] * (1 - (0.75 * Math.pow(costheta[pole], 2)));

			sumMi += mi;
			sumFi += fi;
		}

		derivatives[1] = (force + sumFi) / (sumMi + cartMass);
		for (int pole = 0; pole < numPoles; pole++) {
			derivatives[3 + (2 * pole)] = -0.75 * (derivatives[1] * costheta[pole] + gsintheta[pole] + temp[pole])
				/ poleLengths[pole];
		}
	}

	public void motionEquations(double force, double[] variables, double[] derivatives) {
		double sumEffectiveMasses = 0;
		double sumEffectiveForces = 0;

		for (int pole = 0; pole < numPoles; pole++) {
			double polAngle = variables[2 + (pole * 2)];
			double cosPoleAngle = Math.cos(polAngle);
			double effectiveMass = poleMasses[pole] * (1 - (0.75 * Math.pow(cosPoleAngle, 2)));

			double sinPoleAngle = Math.sin(polAngle);
			double ml = poleMasses[pole] * poleLengths[pole];
			double velocity = variables[3 + (pole * 2)];
			double temp = (POLE_FRICTION * velocity) / ml;
			double effectiveForce = (ml * Math.pow(velocity, 2) * sinPoleAngle)
				+ (0.75 * poleMasses[pole] * cosPoleAngle * (temp + (gravity * sinPoleAngle)));

			sumEffectiveForces += effectiveForce;
			sumEffectiveMasses += effectiveMass;
		}

		derivatives[1] = (force + sumEffectiveForces) / (sumEffectiveMasses + cartMass);
		for (int pole = 0; pole < numPoles; pole++) {
			double polAngle = variables[2 + (pole * 2)];
			double cosPoleAngle = Math.cos(polAngle);
			double sinPoleAngle = Math.sin(polAngle);
			double temp = (POLE_FRICTION * variables[3 + (pole * 2)]) / poleMasses[pole] * poleLengths[pole];
			derivatives[(pole * 2) + 3] = -0.75 * ((derivatives[1] * cosPoleAngle) + (gravity * sinPoleAngle) + temp)
				/ poleLengths[pole];
		}
	}

	public void RungeKutta4(double force, double[] y, double[] dydx, double[] yout) {
		double hh = TAU * 0.5;
		double h6 = TAU / 6.0;
		double[] dym = new double[6];
		double[] dyt = new double[6];
		double[] yt = new double[6];

		for (int i = 0; i < y.length; i++) {
			yt[i] = y[i] + hh * dydx[i];
		}

		step(force, yt, dyt);
		for (int pole = 0; pole <= numPoles; pole++) {
			dyt[2 * pole] = yt[2 * pole + 1];
		}

		for (int i = 0; i < y.length; i++) {
			yt[i] = y[i] + hh * dyt[i];
		}

		step(force, yt, dym);
		for (int pole = 0; pole <= numPoles; pole++) {
			dym[2 * pole] = yt[2 * pole + 1];
		}

		for (int i = 0; i < y.length; i++) {
			yt[i] = y[i] + TAU * dym[i];
			dym[i] += dyt[i];
		}

		step(force, yt, dyt);
		for (int pole = 0; pole <= numPoles; pole++) {
			dyt[2 * pole] = yt[2 * pole + 1];
		}

		for (int i = 0; i < y.length; i++) {
			yout[i] = y[i] + h6 * (dydx[i] + dyt[i] + 2.0 * dym[i]);
		}
	}

	@Override
	public EnvTransition<CartPoleState> getEnvironmentTransition(CartPoleState afterState,
			RandomDataGenerator random) {
		//TODO(Marcin): check rewards
		double reward = isTerminal(afterState) ? -1 : 0;
		return new EnvTransition<>(afterState, reward, afterState);
	}

	@Override
	public List<ContinuousAction> getPossibleActions(CartPoleState state) {
		return possibleActions;
	}

	@Override
	public CartPoleState sampleInitialStateDistribution(RandomDataGenerator random) {
		double[] initialAngles = new double[numPoles];
		double[] initialVelocities = new double[numPoles];
		initialAngles[0] = FOUR_DEGREES;
		return new CartPoleState(0, 0, initialAngles, initialVelocities);
	}

	@Override
	public boolean isTerminal(CartPoleState state) {
		double position = state.getCartPosition();
		if (Math.abs(position) > trackLength) {
			return true;
		}

		for (int pole = 0; pole < numPoles; pole++) {
			double poleAngle = state.getPoleAngle(pole);
			if (Math.abs(poleAngle) > failureAngle) {
				return true;
			}
		}
		return false;
	}

	@Override
	public double getAgentPerformance(double totalReward, int numSteps, CartPoleState finalState) {
		return numSteps;
	}

	@Override
	public double[] encode(CartPoleState state) {
		double[] features = Arrays.copyOf(state.getFeatures(), getNumInputs());
		features[0] /= trackLength;
		features[1] /= trackLength;
		for (int pole = 0; pole < numPoles; pole++) {
			features[2 + (pole * 2)] /= failureAngle;
			features[3 + (pole * 2)] /= failureAngle;
		}

		return features;
	}

	public int getNumInputs() {
		return numPoles * 2 + 2;
	}

	@Override
	public double[] encode(ContinuousAction action) {
		return new double[] { action.getValue() / MAX_FORCE };
	}

	public double evaluate(RealFunction controller, int maxNumSteps, RandomDataGenerator random) {
		int numSteps = 0;
		CartPoleState state = sampleInitialStateDistribution(random);
		while (!isTerminal(state) && numSteps < maxNumSteps) {
			double action = controller.getValue(encode(state));
			state = computeAfterState(state, action);
			numSteps++;
		}

		return numSteps;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("numPoles", numPoles).append("pole lengths", poleLengths)
			.append("pole masses", poleMasses).append("cartMass", cartMass).build();
	}

	@AutoRegistered(defaultSerializer = true)
	public static class CartPoleEnvironmentSerializer implements ObjectSerializer<CartPoleEnvironment> {

		@Override
		public void save(SerializationManager manager, CartPoleEnvironment object, SerializationOutput output)
				throws IOException, SerializationException {
			manager.serialize(object.numPoles, output);
			manager.serialize(object.poleLengths, output);
			manager.serialize(object.poleMasses, output);
			manager.serialize(object.cartMass, output);
		}

		@Override
		public CartPoleEnvironment load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			int numPoles = manager.deserialize(input);
			double[] poleLengths = manager.deserialize(input);
			double[] poleMasses = manager.deserialize(input);
			double cartMass = manager.deserialize(input);

			return new CartPoleEnvironment(numPoles, poleLengths, poleMasses, cartMass);
		}

		@Override
		public int getUniqueSerializerId() {
			return 12122013;
		}
	}
}
