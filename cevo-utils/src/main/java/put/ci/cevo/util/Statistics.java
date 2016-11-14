package put.ci.cevo.util;

import static put.ci.cevo.util.TextUtils.format;

import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;

import put.ci.cevo.util.sequence.transforms.LazyMap;

import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.procedures.DoubleProcedure;

public class Statistics {

	private final DoubleArrayList observations;

	private Statistics(DoubleArrayList observations) {
		this.observations = observations;
	}

	public double[] getSample() {
		return observations.toArray();
	}

	public SummaryStatistics asStatisticalSummary() {
		final SummaryStatistics statistics = new SummaryStatistics();
		observations.forEach(new DoubleProcedure() {
			@Override
			public void apply(double value) {
				statistics.addValue(value);
			}
		});
		return statistics;
	}

	public String meanWithConfidence() {
		StatisticalSummary statistics = asStatisticalSummary();
		return format(statistics.getMean()) + " ± "
			+ format(StatisticUtils.getConfidenceIntervalDelta(statistics, 0.05));
	}

	public double getConfidenceInterval() {
		return StatisticUtils.getConfidenceIntervalDelta(asStatisticalSummary(), 0.05);
	}

	public double ttest(Statistics other) {
		return TestUtils.t(other.getSample(), this.getSample());
	}

	public double pvalue(Statistics other) {
		return TestUtils.homoscedasticTTest(other.getSample(), this.getSample()) / 2;
	}

	public static LazyMap<Integer, SummaryStatistics> createStatisticsMap() {
		return new LazyMap<Integer, SummaryStatistics>(new TreeMap<Integer, SummaryStatistics>()) {
			@Override
			protected SummaryStatistics transform(Integer num) {
				return new SummaryStatistics();
			}
		};
	}

	public static Statistics create(DoubleArrayList observations) {
		return new Statistics(observations);
	}

	public static Statistics create(Iterable<Double> observations) {
		DoubleArrayList doubles = new DoubleArrayList();
		for (Double value : observations) {
			doubles.add(value);
		}
		return new Statistics(doubles);
	}

}