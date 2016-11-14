package put.ci.cevo.experiments.wpc;

import java.util.List;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.DistanceFunction;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class WPCHammingTestDistance implements DistanceFunction<WPC, WPC> {

	@Override
	public double getDistance(WPC individual1, WPC individual2, PayoffTable<WPC, WPC> payoff) {
		List<Double> payoffs1 = payoff.solutionPayoffs(individual1).toList();
		List<Double> payoffs2 = payoff.solutionPayoffs(individual2).toList();

		double distance = 0;
		for (int i = 0; i < payoffs1.size(); i++) {
			distance += Math.abs(payoffs1.get(i) - payoffs2.get(i));
		}
		return distance;
	}
}
