package put.ci.cevo.framework.selection;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.List;

public interface SelectionStrategy<T, S> {

	public List<S> select(final List<EvaluatedIndividual<T>> individuals, final RandomDataGenerator random);

}
