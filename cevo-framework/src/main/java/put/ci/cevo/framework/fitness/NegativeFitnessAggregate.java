package put.ci.cevo.framework.fitness;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;

import java.util.Map;

public class NegativeFitnessAggregate implements FitnessAggregate {

	private final FitnessAggregate aggregate;

	public NegativeFitnessAggregate(FitnessAggregate aggregate) {
		this.aggregate = aggregate;
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return Transforms.transformValues(aggregate.aggregateFitness(payoff, context), new Transform<Fitness, Fitness>() {
			@Override
			public Fitness transform(Fitness fitness) {
				return fitness.negate();
			}
		});
	}
}
