package put.ci.cevo.framework.individuals.loaders.filters;

import java.util.List;

public class IdentityIndividualsFilter<T> extends IndividualsFilter<T> {
	@Override
	public List<T> select(List<T> list) {
		return list;
	}
}