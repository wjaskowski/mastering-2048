package put.ci.cevo.framework.selection;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.List;

import static put.ci.cevo.util.sequence.Sequences.seq;

/**
 * Does no selection, simply returns the passed individuals.
 */
public class IdentitySelectionStrategy<S> implements SelectionStrategy<S, S> {

	@Override
	public List<S> select(List<EvaluatedIndividual<S>> individuals, RandomDataGenerator random) {
		return seq(individuals).map(EvaluatedIndividual.<S> toIndividual()).toList();
	}

}
