package put.ci.cevo.framework.selection;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.List;

import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.statisticsAggregate;

/**
 * Uses the mean population fitness and fitness standard deviation to adjust individuals fitness scores. Early on it
 * helps to avoid premature convergence caused by the dominance of a few relatively fit candidates in a population of
 * mostly unfit individuals. It also helps to amplify minor fitness differences in a mature population where the rate of
 * improvement has slowed.
 */
public class SigmaScaling<T> implements SelectionStrategy<T, T> {

	private final SelectionStrategy<T, T> strategy;

	@AccessedViaReflection
	public SigmaScaling(int selectionSize) {
		this(new StochasticUniversalSampling<T>(selectionSize));
	}

	@AccessedViaReflection
	public SigmaScaling(SelectionStrategy<T, T> strategy) {
		this.strategy = strategy;
	}

	@Override
	public List<T> select(List<EvaluatedIndividual<T>> individuals, RandomDataGenerator random) {
		final SummaryStatistics statistics = seq(individuals).map(EvaluatedIndividual.<T> toFitness()).aggregate(
			new SummaryStatistics(), statisticsAggregate());
		return strategy.select(seq(individuals).map(new Transform<EvaluatedIndividual<T>, EvaluatedIndividual<T>>() {
			@Override
			public EvaluatedIndividual<T> transform(EvaluatedIndividual<T> individual) {
				return scaleFitness(statistics, individual);
			}
		}).toList(), random);
	}

	private EvaluatedIndividual<T> scaleFitness(SummaryStatistics statistics, EvaluatedIndividual<T> individual) {
		double stdev = statistics.getStandardDeviation();
		if (stdev == 0) {
			return individual.withObjectiveFitness(1);
		}
		double scaledFitness = 1 + (individual.getFitness() - statistics.getMean()) / (2 * stdev);
		return individual.withObjectiveFitness(scaledFitness > 0 ? scaledFitness : 0.1);
	}
}
