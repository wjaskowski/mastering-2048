package put.ci.cevo.framework.operators.mutation;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.RandomUtils;

import java.util.List;

/**
 * Pick random mutation and use it
 */
public class RandomMutation<T> implements MutationOperator<T> {

	private final List<MutationOperator<T>> operators;

	@SafeVarargs
	public RandomMutation(MutationOperator<T>... operators) {
		this.operators = ImmutableList.copyOf(operators);
	}

	@Override
	public T produce(T individual, RandomDataGenerator random) {
		return RandomUtils.pickRandom(operators, random).produce(individual, random);
	}
}
