package put.ci.cevo.framework.operators.mutation.differential;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.framework.operators.mutation.MutationAdapter;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.List;

import static put.ci.cevo.util.sequence.Sequences.seq;

/**
 * Similar to {@link MutationAdapter} but designed to work with {@link PopulationMutation} instances.
 */
public final class PopulationMutationAdapter<X, Y> implements PopulationMutation<X> {

	private final PopulationMutation<Y> mutation;
	private final IndividualAdapter<X, Y> adapter;

	@AccessedViaReflection
	public PopulationMutationAdapter(PopulationMutation<Y> mutation, IndividualAdapter<X, Y> adapter) {
		this.mutation = mutation;
		this.adapter = adapter;
	}

	@Override
	public X produce(X individual, List<EvaluatedIndividual<X>> population, RandomDataGenerator random) {
		final Y mutant = mutation.produce(adapter.from(individual),
			seq(population).map(new Transform<EvaluatedIndividual<X>, EvaluatedIndividual<Y>>() {
				@Override
				public EvaluatedIndividual<Y> transform(EvaluatedIndividual<X> object) {
					return EvaluatedIndividual.template(adapter.from(object.getIndividual()), object);
				}
			}).toList(), random);
		return adapter.from(mutant, individual);
	}

	public PopulationMutation<Y> getMutation() {
		return mutation;
	}

}
