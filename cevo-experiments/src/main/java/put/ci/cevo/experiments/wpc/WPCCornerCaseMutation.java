package put.ci.cevo.experiments.wpc;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class WPCCornerCaseMutation implements MutationOperator<WPC> {

	private double probability;

	public WPCCornerCaseMutation(double probability) {
		this.probability = probability;
	}

	@Override
	public WPC produce(WPC individual, RandomDataGenerator random) {
		double[] weights = individual.getWeights().clone();
		for (int w = 0; w < weights.length; w++) {
			if (random.nextUniform(0, 1) < probability) {
				weights[w] *= -1;
			}
		}
		return new WPC(weights);
	}
}
