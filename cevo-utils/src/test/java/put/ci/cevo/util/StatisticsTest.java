package put.ci.cevo.util;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Test;

public class StatisticsTest {

	@Test
	public void testGetConfidenceIntervalDelta() throws Exception {
		SummaryStatistics stats = new SummaryStatistics();
		double arr[] = { 1, 2, 4, 5, 3, 1, 2, 3, 5, 7, 9, 2, 3, 2, 3, 1, 1, -1, 2, 6, 8, 0, 7, 5, 3, 2, 3, 4, 5, 6, 2,
			1, 4, 5, 7, 9, -1, 2, 3, 5, 6, 7, 3, 3, 34, 2, 3, 23, 3, 3, 23, 43, 23, 23, 4, 2, 32, 34, 2, 34, 2, 32, 5,
			23, 23, 34, 2, 3, 42, 42, 2, 3, 26, 2, 6, 6, 6, 3, 3, 4, 2, 3, 6, 7, 1, 2, 4, 6, 7, 2, 3, 4, 6, 7, 3, 3, 2,
			3, 4, 6, 6 };
		for (double a : arr)
			stats.addValue(a);

		double expectedConfidence = 1.96 * stats.getStandardDeviation() / Math.sqrt(stats.getN());
		double confidence = StatisticUtils.getConfidenceIntervalDelta(stats, 0.05);
		// Will work only for many observations (>30)
		Assert.assertEquals(expectedConfidence, confidence, 0.1);
	}
}
