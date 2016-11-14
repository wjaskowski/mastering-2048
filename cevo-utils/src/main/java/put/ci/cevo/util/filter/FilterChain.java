package put.ci.cevo.util.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterChain<T> extends AbstractFilter<T> {

	private final List<Filter<? super T>> chain = new ArrayList<Filter<? super T>>();

	@Override
	public boolean qualifies(T object) {
		for (Filter<? super T> filter : chain) {
			if (!filter.qualifies(object)) {
				return false;
			}
		}
		return true;
	}

	public FilterChain<T> addFilter(Filter<? super T> filter) {
		chain.add(filter);
		return this;
	}

}
