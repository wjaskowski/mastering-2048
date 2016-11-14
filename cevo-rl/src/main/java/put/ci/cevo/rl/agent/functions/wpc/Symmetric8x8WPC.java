package put.ci.cevo.rl.agent.functions.wpc;

import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;

import put.ci.cevo.rl.agent.functions.RealFunction;

public class Symmetric8x8WPC implements RealFunction {

	public static final int NUM_WEIGHTS = 10;

	private WPC fullWPC;
	private double[] weights;

	private static final int[] mapping = { 7, 2, 0, 1, 1, 0, 2, 7, 2, 8, 3, 4, 4, 3, 8, 2, 0, 3, 5, 6, 6, 5, 3, 0, 1,
		4, 6, 9, 9, 6, 4, 1, 1, 4, 6, 9, 9, 6, 4, 1, 0, 3, 5, 6, 6, 5, 3, 0, 2, 8, 3, 4, 4, 3, 8, 2, 7, 2, 0, 1, 1, 0,
		2, 7 };

	public Symmetric8x8WPC() {
		this(new double[NUM_WEIGHTS]);
	}

	public Symmetric8x8WPC(double[] weights) {
		this.weights = weights;

		double[] allWeights = new double[mapping.length];
		for (int i = 0; i < allWeights.length; i++) {
			allWeights[i] = weights[mapping[i]];
		}
		fullWPC = new WPC(allWeights);
	}

	public WPC getFullWPC() {
		return fullWPC;
	}

	public double[] getWeights() {
		return weights.clone();
	}

	public int getNumWeights() {
		return weights.length;
	}

	@Override
	public double getValue(double[] input) {
		return fullWPC.getValue(input);
	}

	@Override
	public void update(double[] input, double expectedValue, double learningRate) {
		throw new NotImplementedException();
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(weights);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Symmetric8x8WPC other = (Symmetric8x8WPC) obj;
		return Arrays.equals(weights, other.weights);
	}

	@Override
	public String toString() {
		String s = "[" + weights.length + "]";
		for (int i = 0; i < weights.length; ++i) {
			s += String.format(" %.2f,", weights[i]);
		}
		return s;
	}
}
