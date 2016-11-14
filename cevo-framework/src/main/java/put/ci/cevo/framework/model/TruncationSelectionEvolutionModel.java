package put.ci.cevo.framework.model;

import org.apache.commons.collections15.ListUtils;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Objects.toStringHelper;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static put.ci.cevo.util.RandomUtils.shuffle;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class TruncationSelectionEvolutionModel<T> implements EvolutionModel<T> {

	private final PopulationFactory<T> factory;
	private final MutationOperator<T> mutation;

	private final int elites;

	public TruncationSelectionEvolutionModel(int elites, PopulationFactory<T> factory, MutationOperator<T> mutation) {
		this.factory = factory;
		this.mutation = mutation;
		this.elites = elites;
	}

	@Override
	public List<T> evolvePopulation(List<EvaluatedIndividual<T>> evaluatedPopulation, final ThreadedContext context) {
		evaluatedPopulation = new ArrayList<>(evaluatedPopulation);
		shuffle(evaluatedPopulation, context.getRandomForThread());
		sort(evaluatedPopulation, reverseOrder());
		
		final List<T> head = seq(evaluatedPopulation).take(elites)
			.transform(new Transform<EvaluatedIndividual<T>, T>() {
				@Override
				public T transform(EvaluatedIndividual<T> evaluated) {
					return mutation.produce(evaluated.getIndividual(), context.getRandomForThread());
				}
			}).toImmutableList();
		
		return ListUtils.union(head, factory.createPopulation(evaluatedPopulation.size() - elites, 
			context.getRandomForThread()));
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("factory", factory).add("mutation", mutation).add("elites", elites).toString();
	}

}
