package put.ci.cevo.framework.model;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.algorithms.archives.ParentChildArchive;
import put.ci.cevo.framework.operators.crossover.CrossoverOperator;
import put.ci.cevo.framework.operators.mutation.differential.PopulationMutation;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.util.Pair.create;

public class ExperimentalDifferentialModel<T> implements EvolutionModel<T> {

	private final PopulationMutation<T> mutation;
	private final CrossoverOperator<T> crossover;

	private final ParentChildArchive<T> archive;

	public ExperimentalDifferentialModel(PopulationMutation<T> mutation, CrossoverOperator<T> crossover) {
		this.mutation = mutation;
		this.crossover = crossover;
		this.archive = new ParentChildArchive<>();
	}

	@Override
	public List<T> evolvePopulation(List<EvaluatedIndividual<T>> evaluatedPopulation, ThreadedContext context) {
		List<T> offspring = new ArrayList<>(evaluatedPopulation.size());
		for (EvaluatedIndividual<T> individual : evaluatedPopulation) {
			EvaluatedIndividual<T> parent = archive.getParent(individual);
			if (individual.getFitness() >= parent.getFitness()) {
				parent = individual;
			}
			T child = createOffspring(parent.getIndividual(), evaluatedPopulation, context.getRandomForThread());
			offspring.add(child);
			archive.submit(create(child, parent));
		}
		return offspring;
	}

	private T createOffspring(T parent, List<EvaluatedIndividual<T>> evaluatedPopulation, RandomDataGenerator random) {
		T mutated = mutation.produce(parent, evaluatedPopulation, random);
		return crossover.produce(create(mutated, parent), random);
	}

}
