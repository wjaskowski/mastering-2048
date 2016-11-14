package put.ci.cevo.framework.model;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.AsyncTransformSequence;
import put.ci.cevo.util.sequence.Sequences;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Objects.toStringHelper;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static put.ci.cevo.util.RandomUtils.shuffle;

public class MuCommaLambdaEvolutionModel<T> implements EvolutionModel<T> {

	private final MutationOperator<T> mutation;

	private final int mu;
	private final int lambda;

	@AccessedViaReflection
	public MuCommaLambdaEvolutionModel(int mu, int lambda, MutationOperator<T> mutation) {
		Preconditions.checkArgument(lambda % mu == 0, "λ has to be a multiple of μ! [μ=" + mu + ", λ=" + lambda + "]");
		this.mu = mu;
		this.lambda = lambda;
		this.mutation = mutation;
	}

	@Override
	public List<T> evolvePopulation(List<EvaluatedIndividual<T>> evaluatedPopulation, final ThreadedContext context) {
		evaluatedPopulation = new ArrayList<>(evaluatedPopulation);
		shuffle(evaluatedPopulation, context.getRandomForThread());
		sort(evaluatedPopulation, reverseOrder());
		final List<EvaluatedIndividual<T>> parents = Sequences.seq(evaluatedPopulation).take(mu).toImmutableList();

		return new AsyncTransformSequence<EvaluatedIndividual<T>, T>(parents) {
			@Override
			protected void getNext(EvaluatedIndividual<T> evaluated) {
				final T individual = evaluated.getIndividual();
				final int numChildren = lambda / mu;
				for (int i = 0; i < numChildren; i++) {
					next(mutation.produce(individual, context.getRandomForThread()));
				}
			}
		}.toImmutableList();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("μ", mu).add("λ", lambda).add("operator", mutation).toString();
	}

}
