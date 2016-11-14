package put.ci.cevo.framework.fitness;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.Map;

import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.doubleAdd;

public class CompetitiveFitnessSharing implements FitnessAggregate {

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(final PayoffTable<S, T> payoff, ThreadedContext context) {
		final Map<T, Double> opponentsSum = seq(payoff.tests()).keysToMap(new Transform<T, Double>() {
			@Override
			public Double transform(T test) {
				return payoff.testPayoffs(test).aggregate(doubleAdd());
			}
		});
		return payoff.solutions().keysToMap(new Transform<S, Fitness>() {
			@Override
			public Fitness transform(S candidate) {
				double fitness = 0;
				for (T opponent : payoff.tests()) {
					double opponentSum = opponentsSum.get(opponent);
					if (opponentSum != 0) {
						fitness += payoff.get(candidate, opponent) / opponentSum;
					}
				}
				return new ScalarFitness(fitness);
			}
		});
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
