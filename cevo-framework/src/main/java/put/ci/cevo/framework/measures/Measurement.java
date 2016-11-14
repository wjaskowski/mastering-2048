package put.ci.cevo.framework.measures;

import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import put.ci.cevo.framework.interactions.InteractionResult;

/**
 * The result of PerformanceMeasure.measure()
 */
public class Measurement {

	private final StatisticalSummary stats;
	private final long effort;

	public final static class Builder {

		private final SummaryStatistics stats = new SummaryStatistics();
		private int totalEffort = 0;

		public Builder add(double result) {
			return add(result, 1);
		}

		public Builder add(double result, long effort) {
			stats.addValue(result);
			totalEffort += effort;
			return this;
		}

		public Builder add(InteractionResult result) {
			stats.addValue(result.firstResult());
			totalEffort += result.getEffort();
			return this;
		}

		public Builder add(List<InteractionResult> results) {
			results.forEach(this::add);
			return this;
		}

		public Builder addRaw(List<Double> results) {
			results.forEach(this::add);
			return this;
		}

		public Measurement build() {
			return new Measurement(stats, totalEffort);
		}

	}

	public Measurement(StatisticalSummary stats, long effort) {
		Preconditions.checkArgument(effort >= 0);
		this.stats = stats;
		this.effort = effort;
	}

	public long getEffort() {
		return effort;
	}

	public StatisticalSummary stats() {
		return stats;
	}
}
