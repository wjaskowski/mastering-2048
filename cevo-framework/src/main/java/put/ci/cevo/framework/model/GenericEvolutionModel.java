package put.ci.cevo.framework.model;

import org.apache.commons.collections15.ListUtils;
import put.ci.cevo.framework.operators.EvolutionaryOperator;
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
import static put.ci.cevo.util.RandomUtils.shuffle;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class GenericEvolutionModel<T, S> implements EvolutionModel<T> {

	private final SelectionStrategy<T, S> selection;
	private final EvolutionaryOperator<S, T> operator;

	private final int elites;

	@AccessedViaReflection
	public GenericEvolutionModel(SelectionStrategy<T, S> selection, EvolutionaryOperator<S, T> operator) {
		this(selection, operator, 0);
	}

	@AccessedViaReflection
	public GenericEvolutionModel(SelectionStrategy<T, S> selection, EvolutionaryOperator<S, T> operator, int elites) {
		this.selection = selection;
		this.operator = operator;
		this.elites = elites;
	}

	@Override
	public List<T> evolvePopulation(List<EvaluatedIndividual<T>> evaluatedPopulation, final ThreadedContext context) {
		evaluatedPopulation = new ArrayList<>(evaluatedPopulation);
		shuffle(evaluatedPopulation, context.getRandomForThread());
		sort(evaluatedPopulation, reverseOrder());

		List<T> eliteIndividuals = seq(evaluatedPopulation).take(elites)
			.map(new Transform<EvaluatedIndividual<T>, T>() {
				@Override
				public T transform(EvaluatedIndividual<T> evaluated) {
					return evaluated.getIndividual();
				}
			}).toImmutableList();

		final List<S> parents = selection.select(evaluatedPopulation, context.getRandomForThread());
		return ListUtils.union(new AsyncTransformSequence<S, T>(parents) {
			@Override
			protected void getNext(S parent) {
				next(operator.produce(parent, context.getRandomForThread()));
			}
		}.toList(), eliteIndividuals);
	}

}
