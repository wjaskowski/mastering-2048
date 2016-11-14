package put.ci.cevo.rl.agent.functions.mlp;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

import com.google.common.base.Preconditions;

public class MLP implements RealFunction, Serializable {

	private static final long serialVersionUID = -6009780187729900116L;
	
	private int numWeights;
	private Layer[] layers;
	
	private int numInputs;
	private int numHiddenNeurons;
	private int numOutputs;

	@AccessedViaReflection
	public MLP(int numInputs, int numHiddenNeurons, int numOutputs) {
		this.numInputs = numInputs;
		this.numHiddenNeurons = numHiddenNeurons;
		this.numOutputs = numOutputs;
		
		Preconditions.checkArgument(numHiddenNeurons > 0);

		Layer hiddenLayer = new Layer(numInputs, numHiddenNeurons);
		Layer outputLayer = new Layer(numHiddenNeurons, numOutputs);

		layers = new Layer[] { hiddenLayer, outputLayer };
		for (Layer layer : layers) {
			this.numWeights += layer.getNumNeurons() * (layer.getNumInputs() + 1);
		}
	}

	public MLP(Layer[] layers) {
		this.layers = layers;
		for (Layer layer : layers) {
			this.numWeights += layer.getNumNeurons() * (layer.getNumInputs() + 1);
		}
	}

	@Override
	public double getValue(double[] input) {
		double[][] outputs = propagate(input);
		return outputs[layers.length - 1][0];
	}

	public double[][] propagate(double[] input) {
		Preconditions.checkArgument(input.length == layers[0].getNumInputs());
		double[][] outputs = new double[layers.length][];
		for (int i = 0; i < layers.length; i++) {
			outputs[i] = layers[i].propagate((i == 0) ? input : outputs[i - 1]);
		}
		return outputs;
	}

	public double[] getWeightUpdates(double[] input, double targetValue, double alpha) {
		double[][] outputs = propagate(input);
		double[][] errors = backPropagate(outputs, targetValue);

		int copiedWeights = 0;
		double[] weightUpdates = new double[numWeights];
		for (int layer = 0; layer < layers.length; layer++) {
			double[] lUpdates = layers[layer].getWeightUpdates((layer == 0) ? input : outputs[layer - 1],
				outputs[layer], errors[layer], alpha);
			System.arraycopy(lUpdates, 0, weightUpdates, copiedWeights, lUpdates.length);
			copiedWeights += lUpdates.length;
		}

		return weightUpdates;
	}

	public void updateWeights(double[] updates) {
		int updatedWeights = 0;
		for (int layer = 0; layer < layers.length; layer++) {
			int layerWeights = layers[layer].getNumNeurons() * (layers[layer].getNumInputs() + 1);
			layers[layer].updateWeights(Arrays.copyOfRange(updates, updatedWeights, updatedWeights + layerWeights));
			updatedWeights += layerWeights;
		}
	}

	@Override
	public void update(double[] input, double correctValue, double alpha) {
		double[][] outputs = propagate(input);
		double[][] errors = backPropagate(outputs, correctValue);

		layers[0].updateWeights(input, outputs[0], errors[0], alpha);
		for (int layer = 1; layer < layers.length; layer++) {
			layers[layer].updateWeights(outputs[layer - 1], outputs[layer], errors[layer], alpha);
		}
	}

	public double[][] backPropagate(double[][] outputs, double correctValue) {
		return backPropagate(outputs, new double[] { correctValue });
	}

	private double[][] backPropagate(double[][] outputs, double[] correctValues) {
		double[][] errors = new double[layers.length][];

		int outputLayer = layers.length - 1;
		errors[outputLayer] = new double[correctValues.length];
		for (int i = 0; i < correctValues.length; i++) {
			errors[outputLayer][i] = correctValues[i] - outputs[outputLayer][i];
		}

		for (int layer = outputLayer - 1; layer >= 0; layer--) {
			errors[layer] = layers[layer].backPropagate(errors[layer + 1], layers[layer + 1]);
		}
		return errors;
	}

	public double[] getWeights() {
		int copiedWeights = 0;
		double[] weights = new double[numWeights];
		for (int layer = 0; layer < layers.length; layer++) {
			double[] layerWeights = layers[layer].getWeights();
			System.arraycopy(layerWeights, 0, weights, copiedWeights, layerWeights.length);
			copiedWeights += layerWeights.length;
		}
		return weights;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("layers", Arrays.toString(layers));
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
		if (!(obj instanceof MLP)) {
			return false;
		}
		MLP other = (MLP) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(layers, other.layers);
		return equalsBuilder.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(layers).toHashCode();
	}

	public static MLP fromWeights(double[] weights, MLP template) {
		int copiedWeights = 0;
		Layer[] layers = new Layer[template.layers.length];
		for (int l = 0; l < layers.length; l++) {
			Neuron[] layerNeurons = new Neuron[template.layers[l].getNumNeurons()];
			int numNeuronWeights = template.layers[l].getNumInputs() + 1;
			for (int n = 0; n < layerNeurons.length; n++) {
				layerNeurons[n] = new Neuron(ArrayUtils.subarray(weights, copiedWeights, copiedWeights
					+ numNeuronWeights));
				copiedWeights += numNeuronWeights;
			}
			layers[l] = new Layer(layerNeurons);
		}
		return new MLP(layers);
	}

	public static MLP createRandomizedMLP(MLP template, double minWeight, double maxWeight, RandomDataGenerator random) {
		Layer[] layers = new Layer[template.layers.length];
		for (int l = 0; l < layers.length; l++) {
			Neuron[] layerNeurons = new Neuron[template.layers[l].getNumNeurons()];
			int numNeuronWeights = template.layers[l].getNumInputs() + 1;
			for (int n = 0; n < layerNeurons.length; n++) {
				layerNeurons[n] = new Neuron(RandomUtils.randomDoubleVector(numNeuronWeights, minWeight, maxWeight,
					random));
			}
			layers[l] = new Layer(layerNeurons);
		}
		return new MLP(layers);
	}

	public static MLP createRandomizedMLP(int numInputs, int numHiddenNeurons, int numOutputs, double minWeight,
			double maxWeight, RandomDataGenerator random) {
		Neuron[] neurons = new Neuron[numHiddenNeurons];
		int numNeuronWeights = numInputs + 1;
		for (int n = 0; n < neurons.length; n++) {
			neurons[n] = new Neuron(RandomUtils.randomDoubleVector(numNeuronWeights, minWeight, maxWeight, random));
		}
		Layer hiddenLayer = new Layer(neurons);

		neurons = new Neuron[numOutputs];
		numNeuronWeights = numHiddenNeurons + 1;
		for (int n = 0; n < neurons.length; n++) {
			neurons[n] = new Neuron(RandomUtils.randomDoubleVector(numNeuronWeights, minWeight, maxWeight, random));
		}
		Layer outputLayer = new Layer(neurons);
		return new MLP(new Layer[] { hiddenLayer, outputLayer });
    }
    
	@AutoRegistered(defaultSerializer = true)
	public static class MLPSerializer implements ObjectSerializer<MLP> {

		@Override
		public void save(SerializationManager manager, MLP mlp, SerializationOutput output) throws IOException,
		SerializationException {
			double[] weights = mlp.getWeights();
			output.writeInt(mlp.numInputs);
			output.writeInt(mlp.numHiddenNeurons);
			output.writeInt(mlp.numOutputs);
			manager.serialize(weights, output);
		}

		@Override
		public MLP load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			int numInputs = input.readInt();
			int numHiddenNeurons = input.readInt();
			int numOutputs = input.readInt();
			
			MLP template = new MLP(numInputs, numHiddenNeurons, numOutputs);
			double[] weights = manager.deserialize(input);
			
			return MLP.fromWeights(weights, template);
		}

		@Override
		public int getUniqueSerializerId() {
			return 10012014;
		}
	}
}
