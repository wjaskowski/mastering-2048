package put.ci.cevo.framework.model;

import org.apache.commons.collections.ListUtils;
import put.ci.cevo.framework.operators.crossover.CrossoverOperator;
import put.ci.cevo.framework.operators.mutation.differential.PopulationMutation;
import put.ci.cevo.framework.selection.IdentitySelectionStrategy;
import put.ci.cevo.framework.selection.SelectionStrategy;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.AsyncTransformSequence;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static put.ci.cevo.util.Pair.create;
import static put.ci.cevo.util.RandomUtils.shuffle;
import static put.ci.cevo.util.sequence.Sequences.seq;

// WARNING/TODO: Use with caution: this is not real DE, because parents are not compared with children before
// changing them.
public class DifferentialEvolutionModel<T> implements EvolutionModel<T> {

	private final SelectionStrategy<T, T> selection;

	private final PopulationMutation<T> mutation;
	private final CrossoverOperator<T> crossover;

	private final int elites;

	@AccessedViaReflection
	public DifferentialEvolutionModel(PopulationMutation<T> mutation, CrossoverOperator<T> crossover) {
		this(new IdentitySelectionStrategy<T>(), mutation, crossover, 0);
	}

	@AccessedViaReflection
	public DifferentialEvolutionModel(SelectionStrategy<T, T> selection, PopulationMutation<T> mutation,
			CrossoverOperator<T> crossover, int elites) {
		this.selection = selection;
		this.mutation = mutation;
		this.crossover = crossover;
		this.elites = elites;
	}

	@Override
	public List<T> evolvePopulation(final List<EvaluatedIndividual<T>> population, final ThreadedContext context) {
		List<EvaluatedIndividual<T>> evaluatedPopulation = new ArrayList<>(population);
		shuffle(evaluatedPopulation, context.getRandomForThread());
		sort(evaluatedPopulation, reverseOrder());

		List<T> eliteIndividuals = seq(evaluatedPopulation).take(elites)
			.map(new Transform<EvaluatedIndividual<T>, T>() {
				@Override
				public T transform(EvaluatedIndividual<T> evaluated) {
					return evaluated.getIndividual();
				}
			}).toImmutableList();

		final List<T> parents = selection.select(evaluatedPopulation, context.getRandomForThread());
		List<T> individuals = new AsyncTransformSequence<T, T>(parents) {
			@Override
			protected void getNext(T parent) {
				T mutated = mutation.produce(parent, population, context.getRandomForThread());
				next(crossover.produce(create(mutated, parent), context.getRandomForThread()));
			}
		}.toList();
		return ListUtils.union(individuals, eliteIndividuals);
	}
}
