package put.ci.cevo.util.sequence.transforms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import put.ci.cevo.util.ReflectionUtils;
import put.ci.cevo.util.filter.Filters;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.Sequences;

import com.google.common.base.Function;

public abstract class Transform<F, T> implements Function<F, T> {

	public abstract T transform(F object);

	@Override
	public T apply(F object) {
		return transform(object);
	}

	public <K> Sequence<T> applyTo(final Iterable<? extends F> input) {
		return Sequences.transform(input, this);
	}

	public <K> Sequence<T> applyToAndFilter(final Iterable<? extends F> input) {
		return Sequences.transform(input, this).filter(Filters.notNull());
	}

	public <K> Transform<F, K> chain(final Transform<? super T, ? extends K> second) {
		final Transform<F, T> self = this;
		return new Transform<F, K>() {
			@Override
			public K transform(F object) {
				return second.transform(self.transform(object));
			}
		};
	}

	public static <T> Transform<Object, T> fromMethod(final String methodName, final Object... arguments) {
		final Class<?>[] argumentTypes = new Class<?>[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			argumentTypes[i] = arguments[i].getClass();
		}
		final Map<Class<?>, Method> methodsByClass = new LazyMap<Class<?>, Method>() {
			@Override
			protected Method transform(Class<?> objectClass) {
				Method method = ReflectionUtils.getMatchingMethod(objectClass, methodName, argumentTypes);
				if (method == null) {
					throw new RuntimeException("No method " + objectClass + "." + methodName + "("
						+ Arrays.asList(argumentTypes) + ")");
				}
				return method;
			}
		};
		return new Transform<Object, T>() {
			@Override
			public T transform(Object object) {
				Class<?> objectClass = object.getClass();
				Method method = methodsByClass.get(objectClass);
				return ReflectionUtils.invoke(object, method, arguments);
			}
		};
	}

	public static <F, T> Transform<F, T> fromMap(final Map<F, T> map) {
		return new Transform<F, T>() {
			@Override
			public T transform(F object) {
				return map.get(object);
			}
		};
	}

	public static <F, T> Transform<F, T> fromConstruction(final Class<F> fromType, final Class<? extends T> toType) {
		final Constructor<? extends T> constructor = ReflectionUtils.getMatchingConstructor(toType, fromType);
		if (constructor == null) {
			throw new RuntimeException("Cannot find constructor of " + toType.getName() + " from " + fromType.getName());
		}
		return new Transform<F, T>() {
			@Override
			public T transform(F object) {
				return ReflectionUtils.invokeConstructor(constructor, object);
			}
		};
	}

}
