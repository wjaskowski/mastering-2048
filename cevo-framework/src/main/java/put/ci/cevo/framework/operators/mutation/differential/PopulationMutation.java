package put.ci.cevo.framework.operators.mutation.differential;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.List;

public interface PopulationMutation<T> {

	public T produce(T individual, List<EvaluatedIndividual<T>> population, RandomDataGenerator random);
}