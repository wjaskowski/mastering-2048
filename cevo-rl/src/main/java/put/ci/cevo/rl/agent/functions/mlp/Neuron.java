package put.ci.cevo.rl.agent.functions.mlp;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Neuron implements Serializable {

	private static final long serialVersionUID = -637818000242204002L;
	
	private double[] weights;
	private int numInputs;

	private TransferFunction transferFunction;

	public Neuron(double[] weights, TransferFunction transferFunction) {
		this.transferFunction = transferFunction;
		this.numInputs = weights.length - 1;
		this.weights = weights.clone();
	}

	public Neuron(int numInputs, TransferFunction transferFunction) {
		this.transferFunction = transferFunction;
		this.numInputs = numInputs;
		weights = new double[numInputs + 1];
	}

	public Neuron(int numInputs) {
		this(numInputs, new TanhTransferFunction());
	}

	public Neuron(double[] weights) {
		this(weights, new TanhTransferFunction());
	}

	public int getNumInputs() {
		return numInputs;
	}

	public double getWeight(int w) {
		return weights[w];
	}

	public double[] getWeights() {
		return weights.clone();
	}

	public double propagate(double[] input) {
		double activation = weights[0];
		for (int i = 1; i < weights.length; i++) {
			activation += weights[i] * input[i - 1];
		}

		return transferFunction.transfer(activation);
	}

	public double[] getWeightUpdates(double[] input, double output, double error, double alpha) {
		double derivative = transferFunction.derivative(output);
		double delta = alpha * error * derivative;

		double[] weightUpdates = new double[weights.length];
		weightUpdates[0] = delta;
		for (int w = 1; w < weights.length; w++) {
			weightUpdates[w] = delta * input[w - 1];
		}

		return weightUpdates;
	}

	public void updateWeights(double[] updates) {
		for (int w = 0; w < weights.length; w++) {
			weights[w] += updates[w];
		}
	}

	public void updateWeights(double[] inputs, double output, double error, double learningRate) {
		double derivative = transferFunction.derivative(output);
		double delta = learningRate * error * derivative;

		weights[0] += delta;
		for (int w = 1; w < weights.length; w++) {
			weights[w] += delta * inputs[w - 1];
		}
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("weights", Arrays.toString(weights)).append("transfer", transferFunction);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(weights).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Neuron)) {
			return false;
		}

		Neuron other = (Neuron) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(weights, other.weights);
		equalsBuilder.append(transferFunction, other.transferFunction);
		return equalsBuilder.isEquals();
	}

	public interface TransferFunction {
		double transfer(double activation);

		double derivative(double output);
	}

	private static class TanhTransferFunction implements TransferFunction, Serializable {

		private static final long serialVersionUID = 3278020616938753026L;

		@Override
		public double transfer(double activation) {
			return Math.tanh(activation);
		}

		@Override
		public double derivative(double output) {
			return (1.0 - (output * output));
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof TanhTransferFunction;
		}

		@Override
		public String toString() {
			return "Tanh";
		}
	}
}
