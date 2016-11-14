package put.ci.cevo.framework.fitness;

import put.ci.cevo.framework.algorithms.common.PayoffTable;

public interface DistanceFunction<S, T> {
	public double getDistance(S individual1, S individual2, PayoffTable<S, T> payoff);
}
