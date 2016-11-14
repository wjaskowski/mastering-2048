package put.ci.cevo.experiments.numbers;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.ArrayList;
import java.util.List;

public class DimensionsParetoHillClimber implements EvolutionModel<DoubleVector> {

	private final NumbersGameBiasedMutation mutator;

	public DimensionsParetoHillClimber() {
		this(new NumbersGameBiasedMutation(0.1, 0));
	}

	public DimensionsParetoHillClimber(NumbersGameBiasedMutation mutator) {
		this.mutator = mutator;
	}

	@Override
	public List<DoubleVector> evolvePopulation(List<EvaluatedIndividual<DoubleVector>> evaluatedPopulation,
			ThreadedContext context) {

		List<DoubleVector> nextPopulation = new ArrayList<>(evaluatedPopulation.size());
		for (EvaluatedIndividual<DoubleVector> indiv : evaluatedPopulation) {
			DoubleVector parent = indiv.getIndividual();
			DoubleVector child = mutator.produce(parent, context.getRandomForThread());
			nextPopulation.add(paretoDominates(child, parent) ? child : parent);
		}

		return nextPopulation;
	}

	private static boolean paretoDominates(DoubleVector first, DoubleVector second) {
		Preconditions.checkArgument(first.size() == second.size(),
				"Unable to compare individuals evaluated on different objectives");
		boolean dominates = false;
		for (int i = 0; i < first.size(); i++) {
			if (first.get(i) > second.get(i)) {
				dominates = true;
			} else if (first.get(i) < second.get(i)) {
				return false;
			}
		}
		return dominates;
	}
}
