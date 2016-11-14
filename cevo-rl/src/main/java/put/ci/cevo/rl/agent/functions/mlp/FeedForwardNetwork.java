package put.ci.cevo.rl.agent.functions.mlp;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.RandomUtils;

import com.google.common.primitives.Doubles;

public class FeedForwardNetwork implements RealFunction {

	private int numHiddenNeurons;
	private int numOutputs;
	private int numInputs;

	private double[][] weights;

	public FeedForwardNetwork(double[][] weights) {
		this(weights, weights[0].length - 1, 1);
	}

	public FeedForwardNetwork(double[][] weights, int numInputs, int numOutputs) {
		this.numHiddenNeurons = weights.length;
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;

		this.weights = new double[weights.length][];
		for (int neuron = 0; neuron < weights.length; neuron++) {
			this.weights[neuron] = weights[neuron].clone();
		}
	}

	public FeedForwardNetwork(int numInputs, int numHiddenNeurons, int numOutputs) {
		this.numInputs = numInputs;
		this.numHiddenNeurons = numHiddenNeurons;
		this.numOutputs = numOutputs;

		this.weights = new double[numHiddenNeurons][numInputs + numOutputs];
	}

	public int getNumNeurons() {
		return numHiddenNeurons;
	}

	public int getNumInputs() {
		return numInputs;
	}

	public int getNumOutputs() {
		return numOutputs;
	}

	@Override
	public double getValue(double[] input) {
		return propagate(input)[0];
	}

	private double[] propagate(double[] input) {
		double[] activation = new double[numHiddenNeurons];
		double[] outputs = new double[numOutputs];

		for (int neuron = 0; neuron < numHiddenNeurons; neuron++) {
			activation[neuron] = 0.0;
			for (int i = 0; i < numInputs; i++) {
				activation[neuron] += weights[neuron][i] * input[i];
			}
			activation[neuron] = Math.tanh(activation[neuron]);
		}

		for (int o = 0; o < numOutputs; o++) {
			outputs[o] = 0.0;
			for (int neuron = 0; neuron < numHiddenNeurons; neuron++) {
				outputs[o] += activation[neuron] * weights[neuron][numInputs + o];
			}
			outputs[o] = Math.tanh(outputs[o]);
		}
		return outputs;
	}

	@Override
	public void update(double[] input, double expectedValue, double learningRate) {
		throw new NotImplementedException();
	}

	public static FeedForwardNetwork createRandomNetwork(FeedForwardNetwork template, double minWeight,
			double maxWeight, RandomDataGenerator random) {
		double[][] weights = new double[template.numHiddenNeurons][];
		int neuronWeights = template.numInputs + template.numOutputs;
		for (int neuron = 0; neuron < weights.length; neuron++) {
			weights[neuron] = RandomUtils.randomDoubleVector(neuronWeights, minWeight, maxWeight, random);
		}
		return new FeedForwardNetwork(weights, template.numInputs, template.numOutputs);
	}

	public double[] getWeights() {
		return Doubles.concat(weights);
	}
}
