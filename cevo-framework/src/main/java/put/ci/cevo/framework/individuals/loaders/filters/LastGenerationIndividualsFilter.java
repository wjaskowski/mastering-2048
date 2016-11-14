package put.ci.cevo.framework.individuals.loaders.filters;

import java.util.List;

import static java.util.Collections.singletonList;

public class LastGenerationIndividualsFilter<T> extends IndividualsFilter<T> {
	@Override
	public List<T> select(List<T> list) {
		return singletonList(list.get(list.size() - 1));
	}
}