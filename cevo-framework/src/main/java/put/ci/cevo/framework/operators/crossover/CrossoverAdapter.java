package put.ci.cevo.framework.operators.crossover;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import static put.ci.cevo.util.Pair.create;

/**
 * Similar to {@link MutationAdapter}
 */
public final class CrossoverAdapter<X, Y> implements CrossoverOperator<X> {

	private final CrossoverOperator<Y> crossover;
	private final IndividualAdapter<X, Y> adapter;

	@AccessedViaReflection
	public CrossoverAdapter(CrossoverOperator<Y> crossover, IndividualAdapter<X, Y> adapter) {
		this.crossover = crossover;
		this.adapter = adapter;
	}

	@Override
	public X produce(Pair<X, X> individuals, RandomDataGenerator random) {
		Y mutant = crossover.produce(create(adapter.from(individuals.first()), adapter.from(individuals.second())),
			random);
		// This should work in the 99% of cases, but if the "template" of first() is different of the template of
		// the second() it will break (and the behavior is unspecified)
		return adapter.from(mutant, individuals.first());
	}

}
