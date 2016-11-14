package put.ci.cevo.framework.operators.mutation;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.List;

@AccessedViaReflection
public class MutationChain<T> implements MutationOperator<T> {

	private final List<MutationOperator<T>> mutators;

	public MutationChain(Iterable<MutationOperator<T>> mutators) {
		this.mutators = ImmutableList.copyOf(mutators);
	}

	@SafeVarargs
	public MutationChain(MutationOperator<T>... mutators) {
		this.mutators = ImmutableList.copyOf(mutators);
	}

	@Override
	public T produce(T individual, RandomDataGenerator random) {
		for (MutationOperator<T> mutator : mutators) {
			individual = mutator.produce(individual, random);
		}
		return individual;
	}
}
