package put.ci.cevo.framework.selection;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.binarySearch;

/**
 * This algorithm selects individuals in proportion to their fitness: if an individual has a higher fitness, it’s
 * selected more often. Also known as the roulette selection.
 */
public class FitnessProportionateSelection<T> implements SelectionStrategy<T, T> {

	protected final int selectionSize;

	@AccessedViaReflection
	public FitnessProportionateSelection(int selectionSize) {
		this.selectionSize = selectionSize;
	}

	@Override
	public List<T> select(List<EvaluatedIndividual<T>> individuals, RandomDataGenerator random) {
		final List<Double> cumulativeFitnesses = accumulateFitness(individuals);
		final List<T> selection = new ArrayList<T>(selectionSize);
		for (int i = 0; i < selectionSize; i++) {
			double randomFitness = random.nextUniform(0, 1) * cumulativeFitnesses.get(cumulativeFitnesses.size() - 1);
			int index = binarySearch(cumulativeFitnesses, randomFitness);
			if (index < 0) {
				index = Math.abs(index + 1);
			}
			selection.add(individuals.get(index).getIndividual());
		}
		return selection;
	}

	protected List<Double> accumulateFitness(List<EvaluatedIndividual<T>> individuals) {
		final List<Double> cumulativeFitnesses = new ArrayList<>(individuals.size());
		cumulativeFitnesses.add(individuals.get(0).getFitness());
		for (int i = 1; i < individuals.size(); i++) {
			double fitness = individuals.get(i).getFitness();
			cumulativeFitnesses.add(cumulativeFitnesses.get(i - 1) + fitness);
		}
		return cumulativeFitnesses;
	}

}
