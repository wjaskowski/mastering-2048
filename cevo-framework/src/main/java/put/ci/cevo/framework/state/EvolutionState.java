package put.ci.cevo.framework.state;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import put.ci.cevo.framework.termination.EvolutionTarget;

public abstract class EvolutionState {

	private final long elapsedTime;
	private final long totalEffort;

	private final int generation;

	private final EvolutionTarget target;

	public EvolutionState(long elapsedTime, int generation, long totalEffort, EvolutionTarget target) {
		this.elapsedTime = elapsedTime;
		this.generation = generation;
		this.totalEffort = totalEffort;
		this.target = target;
	}

	public abstract <S> List<EvaluatedIndividual<S>> getEvaluatedSolutions();

	public abstract <T> List<EvaluatedIndividual<T>> getEvaluatedTests();

	/** Time elapsed since evolution started */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/** Total evolutionary effort up to this point */
	public long getTotalEffort() {
		return totalEffort;
	}

	public int getGeneration() {
		return generation;
	}

	public <S> EvaluatedIndividual<S> getBestSolution() {
		List<EvaluatedIndividual<S>> evaluatedSolutions = getEvaluatedSolutions();
		EvaluatedIndividual<S> best = evaluatedSolutions.get(0);
		for (EvaluatedIndividual<S> evaluatedSolution : evaluatedSolutions) {
			if (evaluatedSolution.fitness().betterThan(best.fitness()))
				best = evaluatedSolution;
		}
		return best;
	}

	public boolean targetAchieved(EvolutionTarget target) {
		return target.isAchieved(this);
	}

	public EvolutionTarget getTarget() {
		return target;
	}

	@Override
	public String toString() {
		SummaryStatistics solutionsStats = new SummaryStatistics();
		for (EvaluatedIndividual<?> ind : getEvaluatedSolutions()) {
			solutionsStats.addValue(ind.getFitness());
		}
		if (getEvaluatedSolutions().equals(getEvaluatedTests())) {
			return format(ENGLISH, "[gen = %3d, eff = %10d, pop = (%.3f, %.3f, %.3f)]", getGeneration(),
				getTotalEffort(), solutionsStats.getMin(), solutionsStats.getMean(), solutionsStats.getMax());
		}
		SummaryStatistics testsStats = new SummaryStatistics();
		for (EvaluatedIndividual<?> ind : getEvaluatedTests()) {
			testsStats.addValue(ind.getFitness());
		}
		return format(ENGLISH, "[gen = %3d, eff = %10d, solutions = (%.3f, %.3f, %.3f), tests = (%.3f, %.3f, %.3f)",
			getGeneration(), getTotalEffort(), solutionsStats.getMin(), solutionsStats.getMean(),
			solutionsStats.getMax(), testsStats.getMin(), testsStats.getMean(), testsStats.getMax());
	}
}
