package put.ci.cevo.experiments.mlp;

import java.util.Arrays;

import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.rl.agent.functions.mlp.FeedForwardNetwork;

public class FeedForwardNetworkDoubleVectorAdapter implements IndividualAdapter<FeedForwardNetwork, DoubleVector> {

	@Override
	public DoubleVector from(FeedForwardNetwork network) {
		return new DoubleVector(network.getWeights());
	}

	@Override
	public FeedForwardNetwork from(DoubleVector vector, FeedForwardNetwork template) {
		double[] weightsVector = vector.toArray();
		int numNeurons = template.getNumNeurons();
		int numWeights = template.getNumInputs() + template.getNumOutputs();

		double[][] weights = new double[numNeurons][];
		for (int neuron = 0; neuron < numNeurons; neuron++) {
			weights[neuron] = Arrays.copyOfRange(weightsVector, neuron * numWeights, (neuron + 1) * numWeights);
		}

		return new FeedForwardNetwork(weights, template.getNumInputs(), template.getNumOutputs());
	}
}
