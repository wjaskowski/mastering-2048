package put.ci.cevo.framework.individuals.loaders.filters;

import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.List;

public abstract class IndividualsFilter<T> extends Transform<List<T>, List<T>> {

	public abstract List<T> select(List<T> individuals);

	@Override
	public List<T> transform(List<T> individuals) {
		return select(individuals);
	}
}
