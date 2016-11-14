package put.ci.cevo.framework.fitness;

import put.ci.cevo.util.ReflectionUtils;

public class FitnessAggregateFactory {

	public enum FitnessAggregateType {
		SIMPLE_SUM(SimpleSumFitness.class),
		DISTINCTIONS(Distinctions.class),
		FITNESS_SHARING(CompetitiveFitnessSharing.class),
		DISTINCTIONS_FITNESS_SHARING(DistinctionsFitnessSharing.class),
		AVERAGE_FITNESS(SimpleAverageFitness.class);

		private FitnessAggregateType(Class<? extends FitnessAggregate> clazz) {
			this.clazz = clazz;
		}

		private final Class<? extends FitnessAggregate> clazz;

		public Class<? extends FitnessAggregate> getClazz() {
			return clazz;
		}

	}

	public static FitnessAggregate create(FitnessAggregateType type) {
		return ReflectionUtils.invokeConstructor(type.getClazz());
	}

	public static FitnessAggregate create(Class<? extends FitnessAggregate> clazz) {
		return ReflectionUtils.invokeConstructor(clazz);
	}

	public static FitnessAggregate negate(FitnessAggregate other) {
		return new NegativeFitnessAggregate(other);
	}
}
