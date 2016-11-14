package put.ci.cevo.util.sequence.transforms;

import static put.ci.cevo.util.ReflectionUtils.invokeConstructor;
import static put.ci.cevo.util.TypeUtils.genericCast;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import put.ci.cevo.util.Pair;
import put.ci.cevo.util.TextUtils;
import put.ci.cevo.util.sequence.Procedure;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;

/**
 * A collection of utility methods producing {@link Transform}s.
 */
public class Transforms {

	public static <T> Procedure<T> identity() {
		return Procedure.empty();
	}

	public static <F, T, U> Map<U, T> transformKeys(Map<F, T> map, Transform<F, U> transform) {
		Map<U, T> transformedMap = genericCast(invokeConstructor(map.getClass(), map.size()));
		for (Entry<F, T> entry : map.entrySet()) {
			transformedMap.put(transform.transform(entry.getKey()), entry.getValue());
		}
		return transformedMap;
	}

	public static <F, T, U> Map<F, U> transformValues(Map<F, T> map, Transform<T, U> transform) {
		Map<F, U> transformedMap = genericCast(invokeConstructor(map.getClass(), map.size()));
		for (Entry<F, T> entry : map.entrySet()) {
			transformedMap.put(entry.getKey(), transform.transform(entry.getValue()));
		}
		return transformedMap;
	}

	public static <F> Transform<F, Wrapper<F>> identityWrap() {
		return new Transform<F, Equivalence.Wrapper<F>>() {
			@Override
			public Wrapper<F> transform(F object) {
				return Equivalence.identity().wrap(object);
			}
		};
	}

	public static <F> Transform<Wrapper<F>, F> identityUnwrap() {
		return new Transform<Wrapper<F>, F>() {
			@Override
			public F transform(Wrapper<F> object) {
				return object.get();
			}
		};
	}

	public static <T> Transform<Object, T> invoke(String methodName, Object... arguments) {
		return Transform.fromMethod(methodName, arguments);
	}

	public static <F, T> Transform<F, T> extractFrom(Map<F, T> map) {
		return Transform.fromMap(map);
	}

	public static <T> Transform<Integer, T> extractFrom(final List<T> list) {
		return new Transform<Integer, T>() {
			@Override
			public T transform(Integer index) {
				return list.get(index);
			}
		};
	}

	public static <F, T> Transform<F, T> construct(Class<? extends T> type, Class<F> fromType) {
		return Transform.fromConstruction(fromType, type);
	}

	public static <T> Transform<Object, T> get(String propertyName) {
		return invoke("get" + StringUtils.capitalize(propertyName));
	}

	public static <T> Transform<Object, T> get(Class<T> toPropertyType) {
		return invoke("get" + toPropertyType.getSimpleName());
	}

	public static <T> Transform<Object, T> convert(Class<T> toType) {
		return invoke("to" + toType.getSimpleName());
	}

	public static <K, V> Transform<Map.Entry<K, V>, Pair<K, V>> asPairs() {
		return new Transform<Map.Entry<K, V>, Pair<K, V>>() {
			@Override
			public Pair<K, V> transform(Entry<K, V> entry) {
				return Pair.create(entry.getKey(), entry.getValue());
			}
		};
	}

	public static <T> Transform<Pair<? extends T, ?>, T> getFirst() {
		return new Transform<Pair<? extends T, ?>, T>() {
			@Override
			public T transform(Pair<? extends T, ?> pair) {
				return pair.first();
			}
		};
	}

	public static <T> Transform<Pair<?, ? extends T>, T> getSecond() {
		return new Transform<Pair<?, ? extends T>, T>() {
			@Override
			public T transform(Pair<?, ? extends T> pair) {
				return pair.second();
			}
		};
	}

	public static <T> Transform<Entry<? extends T, ?>, T> entryKey() {
		return new Transform<Entry<? extends T, ?>, T>() {
			@Override
			public T transform(Entry<? extends T, ?> entry) {
				return entry.getKey();
			}
		};
	}

	public static <T> Transform<Entry<?, ? extends T>, T> entryValue() {
		return new Transform<Entry<?, ? extends T>, T>() {
			@Override
			public T transform(Entry<?, ? extends T> entry) {
				return entry.getValue();
			}
		};
	}

	public static <T> Transform<T, String> convertToString() {
		return new Transform<T, String>() {
			@Override
			public String transform(T object) {
				return object.toString();
			}
		};
	}

	public static Transform<Double, String> formatDouble() {
		return new Transform<Double, String>() {
			@Override
			public String transform(Double object) {
				return TextUtils.format(object);
			}
		};
	}

	public static Transform<Double, Double> multiply(final double factor) {
		return new Transform<Double, Double>() {
			@Override
			public Double transform(Double value) {
				return value * factor;
			}
		};
	}

	public static Transform<Double, Double> negativeValue() {
		return new Transform<Double, Double>() {
			@Override
			public Double transform(Double val) {
				return -val;
			}
		};
	}

	public static Transform<Collection<?>, Integer> size() {
		return new Transform<Collection<?>, Integer>() {
			@Override
			public Integer transform(Collection<?> coll) {
				return coll.size();
			}
		};
	}

	public static <T extends Comparable<? super T>> Transform<Pair<T, T>, T> max() {
		return new Transform<Pair<T, T>, T>() {
			@Override
			public T transform(Pair<T, T> pair) {
				return pair.first().compareTo(pair.second()) >= 0 ? pair.first() : pair.second();
			}
		};
	}

	public static <T extends Comparable<? super T>> Transform<Pair<T, T>, T> min() {
		return new Transform<Pair<T, T>, T>() {
			@Override
			public T transform(Pair<T, T> pair) {
				return pair.first().compareTo(pair.second()) <= 0 ? pair.first() : pair.second();
			}
		};
	}

	public static Transform<List<Double>, Double> mean() {
		return new Transform<List<Double>, Double>() {
			@Override
			public Double transform(List<Double> list) {
				double sum = 0;
				for (Number val : list) {
					sum += val.doubleValue();
				}
				return sum / list.size();
			}
		};
	}

}
