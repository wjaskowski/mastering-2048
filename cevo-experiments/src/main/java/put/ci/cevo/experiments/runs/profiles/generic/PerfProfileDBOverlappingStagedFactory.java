package put.ci.cevo.experiments.runs.profiles.generic;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.factories.StagedPopulationFactory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.util.RandomUtils.sample;

public class PerfProfileDBOverlappingStagedFactory<S, T> implements StagedPopulationFactory<S, T> {

	private final List<List<T>> stagedPools = new ArrayList<>();
	private final int numGenerations;

	@AccessedViaReflection
	public PerfProfileDBOverlappingStagedFactory(String dbFile, int maxPerformance, int minPerformance,
			int performanceStep, int numGenerations, int stageWidth) {
		for (int performance = minPerformance; performance < maxPerformance; performance += performanceStep) {
			List<T> players = PerfProfileDBFactory.readTestsOfDifficulty(dbFile, performance, performance + stageWidth);
			stagedPools.add(players);
		}

		this.numGenerations = numGenerations;
	}

	@Override
	public List<T> createPopulation(int generation, List<S> solutions, int populationSize, ThreadedContext context) {
		int stage = generation * stagedPools.size() / numGenerations;
		Preconditions.checkArgument(populationSize <= stagedPools.get(stage).size());
		return sample(stagedPools.get(stage), populationSize, context.getRandomForThread());
	}
}
