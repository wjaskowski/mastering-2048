package put.ci.cevo.framework.interactions;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.operators.mutation.NeutralMutation;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;

/**
 * An interaction scheme wrapper, that allows individuals to mutate before they are confronted with each other.
 * 
 * Interestingly, it models organisms' lifetime adaptations (possibly random - cnf.
 * http://htpprints.yorku.ca/archive/00000172/01/hinton-nowlan.htm)
 * 
 * In particular it could be used to perform directed mutations (i.e. learning).
 * 
 */
public class MutatedIndividualsInteractionScheme<S, T> implements InteractionScheme<S, T> {

	private final InteractionScheme<S, T> innerInteractionScheme;

	private final MutationOperator<T> testsMutation;
	private final MutationOperator<S> solutionsMutation;

	@AccessedViaReflection
	public MutatedIndividualsInteractionScheme(InteractionScheme<S, T> innerInteractionScheme,
			MutationOperator<T> testsMutation) {
		this(innerInteractionScheme, new NeutralMutation<S>(), testsMutation);
	}

	@AccessedViaReflection
	public MutatedIndividualsInteractionScheme(InteractionScheme<S, T> innerInteractionScheme,
			MutationOperator<S> solutionsMutation, MutationOperator<T> testsMutation) {
		this.innerInteractionScheme = innerInteractionScheme;
		this.solutionsMutation = solutionsMutation;
		this.testsMutation = testsMutation;
	}

	@Override
	public InteractionTable<S, T> interact(List<S> solutions, List<T> tests, ThreadedContext context) {
		List<S> mutatedSolutions = getMutatedIndividuals(solutions, solutionsMutation, context.getRandomForThread());
		List<T> mutatedTests = getMutatedIndividuals(tests, testsMutation, context.getRandomForThread());

		return innerInteractionScheme.interact(mutatedSolutions, mutatedTests, context);
	}

	/**
	 * @param mutation
	 *            is applied only to the population an individual is evolved against. The mutation is temporal: just for
	 *            the time of evaluation
	 */
	private <V> List<V> getMutatedIndividuals(List<V> individuals, MutationOperator<V> mutation,
			RandomDataGenerator random) {
		List<V> mutated = new ArrayList<V>();
		for (V ind : individuals) {
			mutated.add(mutation.produce(ind, random));
		}
		return mutated;
	}
}
