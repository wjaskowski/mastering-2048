package put.ci.cevo.util.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A basic filter implementation with utility methods and external libraries compatibility (apache commons and google
 * collections).
 * 
 */
public abstract class AbstractFilter<T> implements Filter<T>, com.google.common.base.Predicate<T>,
		org.apache.commons.collections15.Predicate<T> {

	@Override
	public boolean evaluate(T object) {
		return qualifies(object);
	}

	@Override
	public boolean apply(T input) {
		return qualifies(input);
	}

	public AbstractFilter<T> inverse() {
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return !AbstractFilter.this.qualifies(object);
			}
		};
	}

	@Override
	public AbstractFilter<T> not() {
		return inverse();
	}

	@Override
	public AbstractFilter<T> and(final Filter<T> other) {
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return AbstractFilter.this.qualifies(object) && other.qualifies(object);
			}
		};
	}

	@Override
	public AbstractFilter<T> or(final Filter<T> other) {
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return AbstractFilter.this.qualifies(object) || other.qualifies(object);
			}
		};
	}

	@Override
	public AbstractFilter<T> xor(final Filter<T> other) {
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return AbstractFilter.this.qualifies(object) ^ other.qualifies(object);
			}
		};
	}

	@Override
	public boolean any(Iterable<? extends T> objects) {
		for (T object : objects) {
			if (qualifies(object)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean every(Iterable<? extends T> objects) {
		for (T object : objects) {
			if (!qualifies(object)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean none(Iterable<? extends T> objects) {
		return !any(objects);
	}

	@Override
	public T find(Iterable<? extends T> objects) {
		for (T object : objects) {
			if (qualifies(object)) {
				return object;
			}
		}
		return null;
	}

	@Override
	public int apply(Iterable<? extends T> features) {
		return apply(features.iterator());
	}

	@Override
	public int apply(Iterator<? extends T> iterator) {
		int removedCount = 0;
		while (iterator.hasNext()) {
			T feature = iterator.next();
			if (!qualifies(feature)) {
				iterator.remove();
				++removedCount;
			}
		}
		return removedCount;
	}

	@Override
	public List<T> select(Iterable<? extends T> features) {
		return select(features.iterator());
	}

	@Override
	public List<T> select(Iterator<? extends T> iterator) {
		List<T> selected = new ArrayList<T>();
		while (iterator.hasNext()) {
			T feature = iterator.next();
			if (qualifies(feature)) {
				selected.add(feature);
			}
		}
		return selected;
	}

}
