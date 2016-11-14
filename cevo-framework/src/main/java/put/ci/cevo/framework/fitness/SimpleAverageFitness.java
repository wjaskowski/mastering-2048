package put.ci.cevo.framework.fitness;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.IdentityHashMap;
import java.util.Map;

import static put.ci.cevo.util.sequence.aggregates.Aggregates.meanValue;

public class SimpleAverageFitness implements FitnessAggregate {

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(final PayoffTable<S, T> payoff, ThreadedContext context) {
		Map<S, Fitness> fitnesses = new IdentityHashMap<S, Fitness>();
		for (S s : payoff.solutions()) {
			double fitness = payoff.solutionPayoffs(s).aggregate(meanValue());
			fitnesses.put(s, new ScalarFitness(fitness));
		}
		return fitnesses;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}