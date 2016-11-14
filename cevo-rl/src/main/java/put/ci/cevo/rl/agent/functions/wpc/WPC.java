package put.ci.cevo.rl.agent.functions.wpc;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

public class WPC implements RealFunction, Serializable {

	private static final long serialVersionUID = 8899559906200648797L;

	private final double weights[];

	@AccessedViaReflection
	public WPC(int numWeights) {
		this.weights = new double[numWeights];
	}

	public WPC(double weights[]) {
		this.weights = weights.clone();
	}

	public double[] getWeights() {
		return weights.clone();
	}

	public double get(int idx) {
		return weights[idx];
	}

	public int getSize() {
		return weights.length;
	}

	@Override
	public double getValue(double[] input) {
		return Math.tanh(multiply(input));
	}

	private double multiply(double[] input) {
		double dotProduct = 0;
		for (int i = 0; i < weights.length; i++) {
			dotProduct += weights[i] * (input[i]);
		}
		return dotProduct;
	}

	@Override
	public void update(double[] input, double expectedValue, double learningRate) {
		double prediction = getValue(input);
		double derivative = 1.0 - (prediction * prediction);

		double delta = learningRate * (expectedValue - prediction) * derivative;

		for (int w = 0; w < weights.length; w++) {
			weights[w] += delta * input[w];
		}
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
		WPC other = (WPC) obj;
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

	@AutoRegistered(defaultSerializer = true)
	public static class WPCSerializer implements ObjectSerializer<WPC> {

		@Override
		public void save(SerializationManager manager, WPC object, SerializationOutput output) throws IOException,
				SerializationException {
			manager.serialize(object.weights, output);
		}

		@Override
		public WPC load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			double[] weights = manager.deserialize(input);
			return new WPC(weights);
		}

		@Override
		public int getUniqueSerializerId() {
			return 34534;
		}
	}
}
