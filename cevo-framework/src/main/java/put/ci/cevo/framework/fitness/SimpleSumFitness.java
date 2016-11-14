package put.ci.cevo.framework.fitness;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.Map;

import static put.ci.cevo.util.sequence.aggregates.Aggregates.doubleAdd;

public class SimpleSumFitness implements FitnessAggregate {

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(final PayoffTable<S, T> payoff, ThreadedContext context) {
		return payoff.solutions().keysToMap(new Transform<S, Fitness>() {
			@Override
			public Fitness transform(S solution) {
				return new ScalarFitness(payoff.solutionPayoffs(solution).aggregate(doubleAdd()));
			}
		});
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
