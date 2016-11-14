package put.ci.cevo.experiments.mlp;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.agent.functions.mlp.MLP;

public class MLPIndividualFactory implements IndividualFactory<MLP> {

	private final double minWeight;
	private final double maxWeight;

	private final MLP template;

	public MLPIndividualFactory(int numInputs, int numHiddenNeurons, int numOutputs, double minWeight, double maxWeight) {
		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
		this.template = new MLP(numInputs, numHiddenNeurons, numOutputs);
	}

	@Override
	public MLP createRandomIndividual(RandomDataGenerator random) {
		return MLP.createRandomizedMLP(template, minWeight, maxWeight, random);
	}

}
