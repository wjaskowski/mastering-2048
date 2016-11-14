package put.ci.cevo.framework.measures;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import put.ci.cevo.util.random.ThreadedContext;

public class PerformanceMeasureUtils {
	/**
	 * Returns performance measure as an average measure performance of a team.
	 * 
	 * Watch out: we threat each measure as a real performance value, but it can be (and in most cases is) only its
	 * approximation
	 */
	public static <S> StatisticalSummary measurePerformanceForTeam(List<S> team, final PerformanceMeasure<S> measure,
			ThreadedContext context) {

		final SynchronizedSummaryStatistics stats = new SynchronizedSummaryStatistics();
		context.submit(new ThreadedContext.Worker<S, Void>() {
			@Override public Void process(S player, ThreadedContext context) throws Exception {
				StatisticalSummary perf = measure.measure(player, context).stats();
				stats.addValue(perf.getMean());
				return null;
			}
		}, team);

		return stats;
	}
}
