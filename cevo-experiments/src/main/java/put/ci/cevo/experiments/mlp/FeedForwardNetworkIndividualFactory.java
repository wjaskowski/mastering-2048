package put.ci.cevo.experiments.mlp;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.agent.functions.mlp.FeedForwardNetwork;

public class FeedForwardNetworkIndividualFactory implements IndividualFactory<FeedForwardNetwork> {

	private double minWeight;
	private double maxWeight;

	private FeedForwardNetwork template;

	public FeedForwardNetworkIndividualFactory(int numInputs, int numHiddenNeurons, int numOutputs, double minWeight,
			double maxWeight) {
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
		this.template = new FeedForwardNetwork(numInputs, numHiddenNeurons, numOutputs);
	}

	@Override
	public FeedForwardNetwork createRandomIndividual(RandomDataGenerator random) {
		return FeedForwardNetwork.createRandomNetwork(template, minWeight, maxWeight, random);
	}

}
