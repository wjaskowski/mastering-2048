package put.ci.cevo.experiments.new2048;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.primitives.Ints;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.util.Lists;
import put.ci.cevo.util.StatisticUtils;
import put.ci.cevo.util.TableUtil;

/**
 * Not very beautiful extension of the Measurement class for storing additional data for 2048 game
 */
public class Game2048Measurement extends Measurement {

	private static final List<int[]> STAGES = Arrays.asList(
			new int[] { 11 },
			new int[] { 12 },
			new int[] { 13 },
			new int[] { 13, 12 },
			new int[] { 14 },
			new int[] { 14, 12 },
			new int[] { 14, 13 },
			new int[] { 14, 13, 12 },
			new int[] { 15 },
			new int[] { 15, 12 },
			new int[] { 15, 13 },
			new int[] { 15, 13, 12 },
			new int[] { 15, 14 },
			new int[] { 15, 14, 12 },
			new int[] { 15, 14, 13 },
			new int[] { 15, 14, 13, 12 },
			new int[] { 16, });

	private final TableUtil.TableBuilder tableBuilder = new TableUtil.TableBuilder(
			"perf", "conf", "64",
			"32_16_8_4", "32_16_8", "32_16_4", "32_16",
			"32_8_4", "32_8", "32_4", "32",
			"16_8_4", "16_8", "16_4", "16",
			"8_4", "8", "4", "2");

	private final TreeMap<int[], Double> achievedStages;

	public final static class Builder {

		private final SummaryStatistics rewardStats = new SummaryStatistics();
		private final TreeMap<int[], Double> achievedStages = new TreeMap<>(Ints.lexicographicalComparator());
		private int totalEffort = 0;

		public Builder() {
			for (int[] stage : Game2048Measurement.STAGES) {
				achievedStages.put(stage, 0.0);
			}
		}

		public Builder add(Game2048Outcome result) {
			return add(result, 1);
		}

		public Builder add(Game2048Outcome result, int effort) {
			rewardStats.addValue(result.score());
			for (int[] stage : Game2048Measurement.STAGES) {
				if (stageAchieved(stage, result.getLastState())) {
					achievedStages.put(stage, achievedStages.get(stage) + 1);
				}
			}
			totalEffort += effort;
			return this;
		}

		private boolean stageAchieved(int[] stage, State2048 lastState) {
			int[] values = lastState.getValues();
			IntArrayList lastStage = new IntArrayList();
			for (int i = values.length - 1; i >= 0; i--) {
				for (int j = 0; j < values[i]; ++j)
					lastStage.add(i);
			}
			return Ints.lexicographicalComparator().compare(stage, lastStage.toArray()) <= 0;
		}

		public Builder addRaw(List<Game2048Outcome> results) {
			results.forEach(this::add);
			return this;
		}

		public Game2048Measurement build() {
			for (int[] stage : Game2048Measurement.STAGES) {
				achievedStages.replace(stage, achievedStages.get(stage) / (double) rewardStats.getN());
			}
			return new Game2048Measurement(rewardStats, totalEffort, achievedStages);
		}
	}

	public Game2048Measurement(StatisticalSummary reward, int effort, TreeMap<int[], Double> percentAchievedStages) {
		super(reward, effort);
		this.achievedStages = percentAchievedStages;
	}

	public double getPercentage(int... tileValues) {
		int[] key = valuesToIndices(tileValues);
		return achievedStages.get(key);
	}

	private int[] valuesToIndices(int[] values) {
		return IntStream.of(values).map(value -> Ints.indexOf(State2048.REWARDS, value)).toArray();
	}

	@Override
	public String toString() {
		List<Object> values = new ArrayList<>();
		values.add(String.format("%6.0f", stats().getMean()));
		values.add(String.format("%5.0f", StatisticUtils.getConfidenceIntervalDelta(stats(), 0.05)));
		for (int[] stage : Lists.reversed(Game2048Measurement.STAGES)) {
			values.add(String.format("%.2f", achievedStages.get(stage)));
		}
		tableBuilder.addRow(values);
		return TableUtil.tableToString(tableBuilder.build()).replaceAll("\\+\n", "").replaceAll("\\+|\\||\\-", "")
						.replaceAll("\n$", "");
	}
}
