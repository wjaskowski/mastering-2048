package put.ci.cevo.experiments.runs.profiles.generic;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.factories.StagedPopulationFactory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

public class PerfProfileDBCyclicStagedFactory<S, T> implements StagedPopulationFactory<S, T> {

	private final List<List<T>> stagedPools = new ArrayList<>();
	private final int stageLength;

	@AccessedViaReflection
	public PerfProfileDBCyclicStagedFactory(String dbFile, int maxPerformance, int minPerformance, int performanceStep,
			int stageLength) {
		this.stageLength = stageLength;

		for (int performance = minPerformance; performance < maxPerformance; performance += performanceStep) {
			List<T> players = PerfProfileDBFactory.readTestsOfDifficulty(dbFile, performance, performance
				+ performanceStep);
			stagedPools.add(players);
		}
	}

	@Override
	public List<T> createPopulation(int generation, List<S> solutions, int populationSize, ThreadedContext context) {
		int stage = (generation / stageLength) % stagedPools.size();
		Preconditions.checkArgument(populationSize <= stagedPools.get(stage).size());
		return RandomUtils.sample(stagedPools.get(stage), populationSize, context.getRandomForThread());
	}
}
