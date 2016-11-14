package put.ci.cevo.framework.operators.mutation;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Mutates an individual of type X using a mutation for type Y and adapter from type X to Y and vice versa
 */
public final class MutationAdapter<X, Y> implements MutationOperator<X> {

	private final MutationOperator<Y> mutation;
	private final IndividualAdapter<X, Y> adapter;

	@AccessedViaReflection
	public MutationAdapter(MutationOperator<Y> mutation, IndividualAdapter<X, Y> adapter) {
		this.mutation = mutation;
		this.adapter = adapter;
	}

	@Override
	public X produce(X individual, RandomDataGenerator random) {
		Y produced = getMutation().produce(adapter.from(individual), random);
		return adapter.from(produced, individual);
	}

	public MutationOperator<Y> getMutation() {
		return mutation;
	}

	@Override
	public String toString() {
		return "AdapterMutation [mutation=" + mutation + ", adapter=" + adapter + "]";
	}
}
