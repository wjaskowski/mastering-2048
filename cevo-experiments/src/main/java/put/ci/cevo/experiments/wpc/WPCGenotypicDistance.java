package put.ci.cevo.experiments.wpc;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.DistanceFunction;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class WPCGenotypicDistance implements DistanceFunction<WPC, WPC> {

	@Override
	public double getDistance(WPC individual1, WPC individual2, PayoffTable<WPC, WPC> payoff) {
		double[] weights1 = individual1.getWeights();
		double[] weights2 = individual2.getWeights();

		double distance = 0;
		for (int i = 0; i < weights1.length; i++) {
			distance += (weights1[i] - weights2[i]) * (weights1[i] - weights2[i]);
		}
		return distance;
	}
}
