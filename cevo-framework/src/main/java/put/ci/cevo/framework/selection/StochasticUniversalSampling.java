package put.ci.cevo.framework.selection;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.doubleAdd;

/**
 * Variant of {@link FitnessProportionateSelection}. The algorithm selects in a fitness-proportionate way but biased so
 * that fit individuals always get picked at least once. In other words, it ensures that the frequency of selection for
 * each candidate is consistent with its expected frequency of selection. This is known as a low variance resampling
 * algorithm.
 */
public class StochasticUniversalSampling<T> extends FitnessProportionateSelection<T> {

	@AccessedViaReflection
	public StochasticUniversalSampling(int selectionSize) {
		super(selectionSize);
	}

	@Override
	public List<T> select(List<EvaluatedIndividual<T>> individuals, RandomDataGenerator random) {
		final List<T> selection = new ArrayList<T>(selectionSize);
		final List<Double> cumulativeFitnesses = accumulateFitness(individuals);
		final double totalFitness = seq(individuals).map(EvaluatedIndividual.<T> toFitness())
			.aggregate(doubleAdd());

		double value = RandomUtils.nextUniform(0, totalFitness / selectionSize, random);
		int idx = 0;
		for (int i = 0; i < selectionSize; i++) {
			while (cumulativeFitnesses.get(idx) < value) {
				idx++;
			}
			value += totalFitness / selectionSize;
			selection.add(individuals.get(idx).getIndividual());
		}
		return selection;
	}

	@Override
	public String toString() {
		return "SUS(" + selectionSize + ")";
	}
}
