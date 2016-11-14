package put.ci.cevo.util;

import java.util.Iterator;
import java.util.Map;

/**
 * This class allows explicit conversions from unparametrised generic types to parametrised generic types, for selected
 * classes. This conversion, as opposed to the standard cast, does not generate an Eclipse warning when called.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TypeUtils {

	/** Explicit conversion for the {@link Class} objects. */
	public static <E> Class<E> genericCast(Class type) {
		return type;
	}

	/** Explicit conversion for the {@link Iterable} objects. */
	public static <G extends Iterable<?>> G genericCast(Iterable iterable) {
		return (G) iterable;
	}

	/** Explicit conversion for the {@link Iterator} objects. */
	public static <G extends Iterator<?>> G genericCast(Iterator iterator) {
		return (G) iterator;
	}

	/** Explicit conversion for the {@link Map} objects. */
	public static <G extends Map<?, ?>> G genericCast(Map map) {
		return (G) map;
	}

	/** Explicit conversion for the {@link Map.Entry} objects. */
	public static <G extends Map.Entry<?, ?>> G genericCast(Map.Entry mapEntry) {
		return (G) mapEntry;
	}

	/** Explicit conversion for the {@link Pair} objects. */
	public static <G extends Pair<?, ?>> G genericCast(Pair pair) {
		return (G) pair;
	}

	/**
	 * The most crude version of explicit conversion. Note: you shouldn't abuse this method - apply only if everything
	 * else fails with a warning.
	 */
	public static <T> T explicitCast(Object object) {
		return (T) object;
	}

}
