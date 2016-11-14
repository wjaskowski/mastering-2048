package put.ci.cevo.util;

import java.util.Arrays;

public class DoubleArrays {

	private DoubleArrays() {

	}

	public static double[] ones(int length) {
		return vector(1, length);
	}

	public static double[] zeros(int length) {
		return vector(0, length);
	}

	public static double[] vector(double value, int length) {
		double[] vector = new double[length];
		Arrays.fill(vector, value);
		return vector;
	}
}
