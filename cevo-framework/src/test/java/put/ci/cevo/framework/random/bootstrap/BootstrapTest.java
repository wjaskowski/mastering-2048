package put.ci.cevo.framework.random.bootstrap;

import static put.ci.cevo.framework.random.bootstrap.Bootstrap.bootstrap;
import static put.ci.cevo.util.sequence.transforms.Transforms.mean;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.util.random.ThreadedContext;

import com.google.common.collect.ImmutableList;

public class BootstrapTest {

	@Test
	public void testMeanBootstrapping() {
		final List<Double> observations = ImmutableList.of(61.0, 88.0, 89.0, 89.0, 90.0, 92.0, 93.0, 94.0, 98.0, 98.0,
			101.0, 102.0, 105.0, 108.0, 109.0, 113.0, 114.0, 115.0, 120.0, 138.0);

		DescriptiveStatistics stats = bootstrap(observations, mean(), 100000, new ThreadedContext(123, 4));

		Assert.assertEquals(3.4, stats.getStandardDeviation(), 0.1);
		Assert.assertEquals(94.0, stats.getPercentile(2.5), 0.1);
		Assert.assertEquals(107.6, stats.getPercentile(97.5), 0.1);

	}

}
