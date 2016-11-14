package put.ci.cevo.util.sequence.aggregates;

import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import put.ci.cevo.util.Pair;

public class Aggregates {

	private static final Aggregate<Integer, Number> INT_ADD = new Aggregate<Integer, Number>() {
		@Override
		public Integer aggregate(Integer aggregate, Number element) {
			return aggregate + element.intValue();
		}
	};

	private static final Aggregate<Long, Number> LONG_ADD = new Aggregate<Long, Number>() {
		@Override
		public Long aggregate(Long aggregate, Number element) {
			return aggregate + element.longValue();
		}
	};

	private static final Aggregate<Float, Number> FLOAT_ADD = new Aggregate<Float, Number>() {
		@Override
		public Float aggregate(Float aggregate, Number element) {
			return aggregate + element.floatValue();
		}
	};

	private static final Aggregate<Double, Number> DOUBLE_ADD = new Aggregate<Double, Number>() {
		@Override
		public Double aggregate(Double aggregate, Number element) {
			return aggregate + element.doubleValue();
		}
	};

	public static Aggregate<Double, Number> meanValue() {
		return new Aggregate<Double, Number>() {
			private long n = 1;

			@Override
			public Double aggregate(Double aggregate, Number element) {
				n++;
				return ((n - 1) * aggregate / n) + (element.doubleValue() / n);
			}
		};
	}

	public static <K, V> Aggregate<Map<K, V>, Pair<K, V>> pairToMapAggregate() {
		return new Aggregate<Map<K, V>, Pair<K, V>>() {
			@Override
			public Map<K, V> aggregate(Map<K, V> accumulator, Pair<K, V> element) {
				accumulator.put(element.first(), element.second());
				return accumulator;
			}
		};
	}

	public static Aggregate<SummaryStatistics, Double> statisticsAggregate() {
		return new Aggregate<SummaryStatistics, Double>() {
			@Override
			public SummaryStatistics aggregate(SummaryStatistics accumulator, Double element) {
				accumulator.addValue(element);
				return accumulator;
			}
		};
	}

	public static Aggregate<Integer, Number> intAdd() {
		return INT_ADD;
	}

	public static Aggregate<Long, Number> longAdd() {
		return LONG_ADD;
	}

	public static Aggregate<Float, Number> floatAdd() {
		return FLOAT_ADD;
	}

	public static Aggregate<Double, Number> doubleAdd() {
		return DOUBLE_ADD;
	}

}
