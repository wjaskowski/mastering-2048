package put.ci.cevo.framework.model;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.CollectionUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Objects.toStringHelper;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static put.ci.cevo.util.RandomUtils.shuffle;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class MuPlusLambdaEvolutionModel<T> implements EvolutionModel<T> {

	private final MutationOperator<T> mutation;

	private final int mu;
	private final int lambda;

	@AccessedViaReflection
	public MuPlusLambdaEvolutionModel(int mu, int lambda, MutationOperator<T> mutation) {
		Preconditions.checkArgument(lambda % mu == 0, "Lambda has to be a multiple of mu");
		this.mutation = mutation;
		this.mu = mu;
		this.lambda = lambda;
	}

	@Override
	public List<T> evolvePopulation(List<EvaluatedIndividual<T>> pop, final ThreadedContext context) {
		pop = new ArrayList<>(pop);
		shuffle(pop, context.getRandomForThread());
		sort(pop, reverseOrder());

		final List<T> parents = seq(pop).take(mu).transform(EvaluatedIndividual.<T> toIndividual()).toList();

		final int numChildrenPerParent = lambda / mu;
		final List<T> multipliedParents = new ArrayList<T>(parents.size() * numChildrenPerParent);
		for (T parent : parents) {
			for (int i = 0; i < numChildrenPerParent; i++) {
				multipliedParents.add(parent);
			}
		}

		final List<T> children = context.invoke(new Worker<T, T>() {
			@Override
			public T process(T parent, ThreadedContext context) {
				return mutation.produce(parent, context.getRandomForThread());
			}
		}, multipliedParents).toList();

		return CollectionUtils.concat(parents, children);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("μ", mu).add("λ", lambda).add("operator", mutation).toString();
	}

}
