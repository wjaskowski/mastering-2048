package put.ci.cevo.util.filter;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;

public final class Filters {

	public static final Filter<Object> ALL = new AbstractFilter<Object>() {
		@Override
		public boolean qualifies(Object object) {
			return true;
		}
	};

	public static final Filter<Object> NONE = new AbstractFilter<Object>() {
		@Override
		public boolean qualifies(Object object) {
			return false;
		}
	};

	public static final Filter<Object> NOT_NULL = new AbstractFilter<Object>() {
		@Override
		public boolean qualifies(Object object) {
			return object != null;
		}
	};

	public static final Filter<Number> IS_ZERO = new AbstractFilter<Number>() {
		@Override
		public boolean qualifies(Number number) {
			return number.doubleValue() == 0;
		}
	};

	public static final Filter<Number> NON_ZERO = new AbstractFilter<Number>() {
		@Override
		public boolean qualifies(Number number) {
			return number.doubleValue() != 0;
		}
	};

	@SuppressWarnings("unchecked")
	public static final <T> Filter<T> all() {
		return (Filter<T>) ALL;
	}

	@SuppressWarnings("unchecked")
	public static final <T> Filter<T> none() {
		return (Filter<T>) NONE;
	}

	@SuppressWarnings("unchecked")
	public static final <T> Filter<T> notNull() {
		return (Filter<T>) NOT_NULL;
	}

	public static final <T> Filter<T> isNull() {
		return Filters.<T> notNull().not();
	}

	@SuppressWarnings("unchecked")
	public static <N extends Number> Filter<N> isZero() {
		return (Filter<N>) IS_ZERO;
	}

	@SuppressWarnings("unchecked")
	public static <N extends Number> Filter<N> nonZero() {
		return (Filter<N>) NON_ZERO;
	}

	public static final <T> Filter<T> in(final Collection<?> set) {
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return set.contains(object);
			}
		};
	}

	public static final <T> Filter<T> in(final Map<?, ?> map) {
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return map.containsKey(object);
			}
		};
	}

	public static final <T> Filter<T> random(final float probability) {
		final Random random = new Random();
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return random.nextFloat() < probability;
			}
		};
	}

	public static final Filter<String> regexp(String regexp) {
		return regexp(Pattern.compile(regexp));
	}

	public static final Filter<String> regexp(final Pattern regexp) {
		return new AbstractFilter<String>() {
			@Override
			public boolean qualifies(String string) {
				return regexp.matcher(string).find();
			}
		};
	}

	public static final <T> Filter<T> equalsFilter(final T equalTo) {
		return new AbstractFilter<T>() {
			@Override
			public boolean qualifies(T object) {
				return ObjectUtils.equals(equalTo, object);
			}
		};
	}

	public static final <T> Filter<T> not(Filter<T> filter) {
		return filter.not();
	}

	public static final <T> boolean any(Iterable<? extends T> objects, Filter<T> filter) {
		return filter.any(objects);
	}

	public static final <T> boolean every(Iterable<? extends T> objects, Filter<T> filter) {
		return filter.every(objects);
	}

	public static final <T> boolean none(Iterable<? extends T> objects, Filter<T> filter) {
		return filter.none(objects);
	}

	private Filters() {
		// forbid construction
	}

}
