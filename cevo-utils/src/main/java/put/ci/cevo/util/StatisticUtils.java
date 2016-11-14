package put.ci.cevo.util;

import static put.ci.cevo.util.TextUtils.format;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import com.google.common.base.Preconditions;

public class StatisticUtils {

	public static String meanWithConfidenceInterval(StatisticalSummary statistics, double significance) {
		return format(statistics.getMean()) + " ± " + format(getConfidenceIntervalDelta(statistics, significance));
	}

	/**
	 * Returns string with mean and confidence interval for a given {@code statistics}
	 * 
	 * @param format
	 *            format string. It must contain two <code>"%f"</code>, e.g. <code>"%.2f\t%.2f"</code>
	 */
	public static String meanWithConfidenceInterval(StatisticalSummary statistics, double significance, String format) {
		return String.format(format, statistics.getMean(), getConfidenceIntervalDelta(statistics, significance));
	}

	/**
	 * {@inheritDoc StatisticUtils#meanWithCofidenceInterval(StatisticalSummary, double, String)}. In percent points.
	 */
	public static String meanWithConfidenceIntervalPercent(StatisticalSummary statistics, double significance,
			String format) {
		return String.format(format, 100.0 * statistics.getMean(),
			100.0 * getConfidenceIntervalDelta(statistics, significance));
	}

	/** Computes Half of the significance interval width */
	public static double getConfidenceIntervalDelta(StatisticalSummary stats, double significance) {
		if (stats.getN() < 1) {
			return 0.0;
		}
		if (stats.getN() == 1) {
			return 0.0;
		}
		TDistribution tDist = new TDistribution(stats.getN() - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
		return a * stats.getStandardDeviation() / Math.sqrt(stats.getN());
	}

}
