package put.ci.cevo.framework.random.bootstrap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.List;

import static put.ci.cevo.util.RandomUtils.sampleWithReplacement;
import static put.ci.cevo.util.TextUtils.asPercentage;
import static put.ci.cevo.util.sequence.Sequences.range;
import static put.ci.cevo.util.sequence.transforms.Transforms.mean;

public class Bootstrap {

	public static <T> DescriptiveStatistics bootstrap(Bootstrapable<T> bootstrapable,
			Transform<List<T>, Double> statistics, int k, ThreadedContext context) {
		return bootstrap(bootstrapable.getSample(), statistics, k, context);
	}

	public static <T> DescriptiveStatistics bootstrap(final List<T> populationSample,
			final Transform<List<T>, Double> statistics, int k, final ThreadedContext context) {
		final DescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
		context.submit(new Worker<Integer, Void>() {
			@Override
			public Void process(Integer piece, ThreadedContext context) {
				List<T> sample = sampleWithReplacement(populationSample, populationSample.size(), context.getRandomForThread());
				stats.addValue(statistics.transform(sample));
				return null;
			}
		}, range(k));
		return stats;
	}

	public static String bootstrapConfidenceIntervals(Bootstrapable<Double> bootstrapable, int k, double significance,
			ThreadedContext context) {
		return bootstrapConfidenceIntervals(bootstrapable.getSample(), k, significance, context);
	}

	public static String bootstrapConfidenceIntervals(final List<Double> populationSample, int k, double significance,
			ThreadedContext context) {
		DescriptiveStatistics b = bootstrap(populationSample, mean(), k, context);
		return asPercentage(mean().apply(populationSample), 1) + " ± " + asPercentage(b.getStandardDeviation(), 2)
			+ " (" + asPercentage(b.getPercentile(100 * (significance / 2)), 1) + " - "
			+ asPercentage(b.getPercentile(100 * (1.0 - significance / 2)), 1) + ")";
	}

}
