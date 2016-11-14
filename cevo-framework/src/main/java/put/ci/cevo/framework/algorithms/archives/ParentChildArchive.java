package put.ci.cevo.framework.algorithms.archives;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.sequence.transforms.Transforms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.copyOf;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class ParentChildArchive<S> implements Archive<Pair<S, EvaluatedIndividual<S>>> {

	private final Map<S, EvaluatedIndividual<S>> parentsByChildren = new HashMap<>();

	@Override
	public void submit(Iterable<Pair<S, EvaluatedIndividual<S>>> individual) {
		for (Pair<S, EvaluatedIndividual<S>> pair : individual) {
			submit(pair);
		}
	}

	@Override
	public void submit(Pair<S, EvaluatedIndividual<S>> pair) {
		parentsByChildren.put(pair.first(), pair.second());
	}

	@Override
	public List<Pair<S, EvaluatedIndividual<S>>> getArchived() {
		return copyOf(seq(parentsByChildren.entrySet()).map(Transforms.<S, EvaluatedIndividual<S>> asPairs()));
	}

	public EvaluatedIndividual<S> getParent(EvaluatedIndividual<S> child) {
		EvaluatedIndividual<S> parent = parentsByChildren.get(child.getIndividual());
		return parent == null ? child : parent;
	}

	@Override
	public int size() {
		return parentsByChildren.size();
	}

}
