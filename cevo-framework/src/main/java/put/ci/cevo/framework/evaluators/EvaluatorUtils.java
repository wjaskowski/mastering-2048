package put.ci.cevo.framework.evaluators;

import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluatorUtils {

	public static <S> List<EvaluatedIndividual<S>> assignFitness(List<S> population, int generation,
			EffortTable<S, ?> efforts, Map<S, Fitness> fitness) {
		List<EvaluatedIndividual<S>> evaluated = new ArrayList<>(population.size());
		for (S individual : population) {
			evaluated.add(new EvaluatedIndividual<>(individual, fitness.get(individual), generation, efforts
				.computeEffort(individual)));
		}
		return evaluated;
	}

}
