package put.ci.cevo.rl.agent.functions.mlp;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Layer implements Serializable {
	
	private static final long serialVersionUID = -9092237422771129202L;
	
	private Neuron[] neurons;
	private int numInputs;

	public Layer(int numInputs, int numNeurons) {
		this.numInputs = numInputs;
		this.neurons = new Neuron[numNeurons];
		for (int i = 0; i < neurons.length; i++) {
			neurons[i] = new Neuron(numInputs);
		}
	}

	public Layer(Neuron[] neurons) {
		this.numInputs = neurons[0].getNumInputs();
		this.neurons = neurons;
	}

	public int getNumNeurons() {
		return neurons.length;
	}

	public int getNumInputs() {
		return numInputs;
	}

	public double getNeuronWeight(int n, int w) {
		return neurons[n].getWeight(w);
	}

	public double[] getWeights() {
		int weightsCopied = 0;
		double[] weights = new double[getNumNeurons() * (getNumInputs() + 1)];
		for (int n = 0; n < neurons.length; n++) {
			double[] neuronWeights = neurons[n].getWeights();
			System.arraycopy(neuronWeights, 0, weights, weightsCopied, neuronWeights.length);
			weightsCopied += neuronWeights.length;
		}
		return weights;
	}

	public double[] propagate(double[] input) {
		double[] output = new double[neurons.length];
		for (int neuron = 0; neuron < neurons.length; neuron++) {
			output[neuron] = neurons[neuron].propagate(input);
		}
		return output;
	}

	public double[] backPropagate(double[] nextErrors, Layer nextLayer) {
		double[] errors = new double[neurons.length];
		for (int i = 0; i < neurons.length; i++) {
			for (int j = 0; j < nextErrors.length; j++) {
				errors[i] += nextErrors[j] * nextLayer.getNeuronWeight(j, i + 1);
			}
		}
		return errors;
	}

	public double[] getWeightUpdates(double[] input, double[] output, double[] errors, double alpha) {
		int weightsCopied = 0;
		double[] weightUpdates = new double[getNumNeurons() * (getNumInputs() + 1)];
		for (int n = 0; n < neurons.length; n++) {
			double[] nUpdates = neurons[n].getWeightUpdates(input, output[n], errors[n], alpha);
			System.arraycopy(nUpdates, 0, weightUpdates, weightsCopied, nUpdates.length);
			weightsCopied += nUpdates.length;
		}

		return weightUpdates;
	}

	public void updateWeights(double[] updates) {
		for (int n = 0; n < neurons.length; n++) {
			double[] nUpdates = Arrays.copyOfRange(updates, n * (numInputs + 1), (n + 1) * (numInputs + 1));
			neurons[n].updateWeights(nUpdates);
		}
	}

	public void updateWeights(double[] inputs, double[] outputs, double[] errors, double alpha) {
		for (int n = 0; n < neurons.length; n++) {
			neurons[n].updateWeights(inputs, outputs[n], errors[n], alpha);
		}
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("neurons", Arrays.toString(neurons));
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Layer)) {
			return false;
		}

		Layer other = (Layer) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(neurons, other.neurons);
		return equalsBuilder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(neurons);
		return hashCodeBuilder.toHashCode();
	}

}