package put.ci.cevo.util.injector;

import static org.apache.commons.beanutils.MethodUtils.invokeExactStaticMethod;
import static put.ci.cevo.util.sequence.Sequences.transform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.MethodUtils;

import put.ci.cevo.util.ClassResolver;
import put.ci.cevo.util.filter.AbstractFilter;
import put.ci.cevo.util.lister.ClassesLister;
import put.ci.cevo.util.lister.ClassesListerOptions;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;

@SuppressWarnings("rawtypes")
public final class ParsingConverters extends ConvertUtilsBean {

	public @interface ParseSubclasses {
		// tagging annotation
	}

	private static class ParsingConverter implements Converter {

		private static final String PARSE_METHOD = "parse";

		private final Class<?> targetType;
		private final List<Class<?>> subclasses;

		private ParsingConverter(Class<?> targetType, List<Class<?>> subclasses) {
			this.targetType = targetType;
			this.subclasses = subclasses;
		}

		@Override
		public Object convert(Class targetType, Object value) {
			if (targetType != this.targetType) {
				fail(targetType, value);
			}
			for (Class<?> clazz : subclasses) {
				try {
					return invokeExactStaticMethod(clazz, PARSE_METHOD, value.toString());
				} catch (NoSuchMethodException e) {
					// ignore
				} catch (IllegalAccessException e) {
					// ignore
				} catch (InvocationTargetException e) {
					// ignore
				} catch (Exception e) {
					final String message = "Fatal error while parsing: class=" + clazz + " value=" + value.toString();
					throw new RuntimeException(message, e);
				}
			}
			fail(targetType, value);
			return value;
		}

		public static ParsingConverter create(Class<?> targetType) {
			if (isDirectlyParsable(targetType)) {
				return new ParsingConverter(targetType, Arrays.<Class<?>> asList(targetType));
			}
			if (!targetType.isAnnotationPresent(ParseSubclasses.class)) {
				return null;
			}

			final ClassesLister classesLister = new ClassesLister.Builder(ClassResolver.defaultPackages()).options(
				new ClassesListerOptions(targetType)).buildImmediately();

			final Sequence<Class<?>> subclasses = transform(classesLister.getClassNames(),
				new Transform<String, Class<?>>() {
					@Override
					public Class<?> transform(String className) {
						try {
							return Class.forName(className);
						} catch (ClassNotFoundException e) {
							throw new RuntimeException("A fatal error occured while loading listed classes!", e);
						}
					}
				});

			final List<Class<?>> parsable = subclasses.filter(new AbstractFilter<Class<?>>() {
				@Override
				public boolean qualifies(Class<?> clazz) {
					return isDirectlyParsable(clazz);
				}
			}).toList();

			return !parsable.isEmpty() ? new ParsingConverter(targetType, parsable) : null;
		}

		private static boolean isDirectlyParsable(Class<?> type) {
			final Method parseMethod = MethodUtils.getAccessibleMethod(type, PARSE_METHOD, String.class);
			return parseMethod != null && Modifier.isStatic(parseMethod.getModifiers());
		}
	};

	private static final Converter ENUM_CONVERTER = new Converter() {

		@Override
		@SuppressWarnings("unchecked")
		public Object convert(Class targetType, Object value) {
			Class enumType = targetType;
			try {
				return Enum.valueOf(enumType, value.toString());
			} catch (Exception e) {
				fail(targetType, value, e);
			}
			return null;
		}
	};

	private static final Converter CLASS_CONVERTER = new Converter() {

		private final ClassResolver beanClassResolver = ClassResolver.DefaultSingleton();

		@Override
		public Object convert(Class targetType, Object value) {
			final String className = value.toString();
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
				Class<?> clazz = beanClassResolver.resolve(className);
				if (clazz != null) {
					return clazz;
				} else {
					fail(targetType, value, e);
					return null;
				}
			}
		}
	};

	private final Map<Class, Converter> convertersCache = new HashMap<Class, Converter>();

	@Override
	public Converter lookup(Class sourceType, Class targetType) {
		return lookup(targetType);
	}

	@Override
	public Converter lookup(Class targetType) {
		if (convertersCache.containsKey(targetType)) {
			return convertersCache.get(targetType);
		}

		if (targetType.isEnum()) {
			return storeConverter(targetType, ENUM_CONVERTER);
		}

		if (Class.class.isAssignableFrom(targetType)) {
			return storeConverter(targetType, CLASS_CONVERTER);
		}

		Converter baseConverter = super.lookup(targetType);
		if (baseConverter != null) {
			return storeConverter(targetType, baseConverter);
		}

		ParsingConverter parsingConverter = ParsingConverter.create(targetType);
		return storeConverter(targetType, parsingConverter);
	}

	private Converter storeConverter(Class<?> targetType, Converter converter) {
		convertersCache.put(targetType, converter);
		return converter;
	}

	private static void fail(Class<?> type, Object value) {
		fail(type, value, null);
	}

	private static void fail(Class<?> type, Object value, Throwable cause) {
		String message = "Invalid conversion attempted!: type='" + type + "'; value='" + value + "'";
		throw new ConversionException(message, cause);
	}
}
