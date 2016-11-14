package put.ci.cevo.experiments.runs.profiles.generic;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.factories.StagedPopulationFactory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

public class PerfProfileDBStagedFactory<S, T> implements StagedPopulationFactory<S, T> {

	private final List<List<T>> stagedPools = new ArrayList<>();
	private final int generationStep;

	@AccessedViaReflection
	public PerfProfileDBStagedFactory(String dbFile, int maxPerformance, int minPerformance, int performanceStep,
			int generationStep) {
		this(dbFile, maxPerformance, minPerformance, performanceStep, generationStep, false);
	}

	@AccessedViaReflection
	public PerfProfileDBStagedFactory(String dbFile, int maxPerformance, int minPerformance, int performanceStep,
			int generationStep, boolean incremental) {
		this.generationStep = generationStep;

		for (int performance = minPerformance; performance < maxPerformance; performance += performanceStep) {
			List<T> tests = PerfProfileDBFactory.readTestsOfDifficulty(dbFile, performance, performance
				+ performanceStep);

			if (incremental && !stagedPools.isEmpty()) {
				List<T> previousPool = stagedPools.get(stagedPools.size() - 1);
				tests.addAll(previousPool);
			}
			stagedPools.add(tests);
		}
	}

	@Override
	public List<T> createPopulation(int generation, List<S> solutions, int populationSize, ThreadedContext context) {
		int stage = generation / generationStep;
		Preconditions.checkArgument(populationSize <= stagedPools.get(stage).size());
		return RandomUtils.sample(stagedPools.get(stage), populationSize, context.getRandomForThread());
	}
}
