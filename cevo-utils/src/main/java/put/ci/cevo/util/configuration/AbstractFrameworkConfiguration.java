package put.ci.cevo.util.configuration;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertyConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.log4j.Logger;

import put.ci.cevo.util.ClassResolver;
import put.ci.cevo.util.IntervalConverter;
import put.ci.cevo.util.injector.ClassInjector;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public abstract class AbstractFrameworkConfiguration implements FrameworkConfiguration {

	private static final Logger logger = Logger.getLogger(AbstractFrameworkConfiguration.class);

	private static final List<String> DATE_FORMAT_STRINGS = ImmutableList.of("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");

	private static final ClassResolver classResolver = ClassResolver.DefaultSingleton();
	private static final ClassInjector injector = new ClassInjector(ClassResolver.DefaultSingleton());

	protected static <T> void logDefaultValueWarning(ConfigurationKey key, T defaultValue) {
		logger.warn(String.format("Could not read config for key `%s'. Using default: %s", key.toString(),
			defaultValue == null ? "null" : defaultValue.toString()));
	}

	@Override
	public <T> T getObject(ConfigurationKey key) {
		return injector.injectConstructor(key);
	}

	@Override
	public <T> T getObject(ConfigurationKey key, T defaultValue) {
		try {
			return injector.injectConstructor(key);
			// TODO: This is stupid, but currently there is no other way. Injector should throw a different exception
		} catch (Throwable e) {
			logger.warn(e);
			logDefaultValueWarning(key, defaultValue);
			return defaultValue;
		}
	}

	@Override
	public <T> List<T> createConfiguredObjects(ConfigurationKey key) {
		return injector.<T> injectMultipleConstructors(key).toList();
	}

	@Override
	public File getFile(ConfigurationKey key) {
		return new File(getString(key));
	}

	@Override
	public File getFile(ConfigurationKey key, File defaultValue) {
		String string = getString(key);
		if (string != null) {
			return new File(string);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	@Override
	public List<Integer> getIntList(ConfigurationKey key) {
		return convertToIntList(getList(key));
	}

	@Override
	public List<Integer> getIntList(ConfigurationKey key, List<Integer> defaultValue) {
		// TODO: the second argument null is probably a bug here (and in other places)
		List<String> list = getList(key, null);
		if (list != null) {
			return convertToIntList(list);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	@Override
	public List<File> getFileList(ConfigurationKey key) {
		Preconditions.checkNotNull(key);

		return convertToFileList(getList(key));
	}

	@Override
	public List<File> getFileList(ConfigurationKey key, List<File> defaultValue) {
		Preconditions.checkNotNull(defaultValue);
		Preconditions.checkNotNull(key);

		List<String> list = getList(key, null);
		if (list != null) {
			return convertToFileList(list);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	private List<Integer> convertToIntList(List<String> list) {
		List<Integer> result = new ArrayList<Integer>(list.size());
		for (String v : list) {
			result.add(PropertyConverter.toInteger(interpolate(v)));
		}
		return result;
	}

	@Override
	public List<Long> getLongList(ConfigurationKey key) {
		return convertToLongList(getList(key));
	}

	@Override
	public List<Long> getLongList(ConfigurationKey key, List<Long> defaultValue) {
		List<String> list = getList(key, null);
		if (list != null) {
			return convertToLongList(list);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	private List<Long> convertToLongList(List<String> list) {
		List<Long> result = new ArrayList<Long>(list.size());
		for (String v : list) {
			result.add(PropertyConverter.toLong(interpolate(v)));
		}
		return result;
	}

	@Override
	public List<Double> getDoubleList(ConfigurationKey key) {
		return convertToDoubleList(getList(key));
	}

	@Override
	public List<Double> getDoubleList(ConfigurationKey key, List<Double> defaultValue) {
		List<String> list = getList(key, null);
		if (list != null) {
			return convertToDoubleList(list);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	private List<Double> convertToDoubleList(List<String> list) {
		List<Double> result = new ArrayList<Double>(list.size());
		for (String v : list) {
			result.add(PropertyConverter.toDouble(interpolate(v)));
		}
		return result;
	}

	private static List<File> convertToFileList(List<String> list) {
		List<File> result = new ArrayList<File>();
		for (String v : list) {
			result.add(new File(v));
		}
		return result;
	}

	@Override
	public Date getDate(ConfigurationKey key) {
		return getDate(key, null);
	}

	@Override
	public Date getDate(ConfigurationKey key, Date defaultValue) {
		String str = getString(key);
		if (str != null) {
			return convertToDate(str);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	private Date convertToDate(String str) {
		str = interpolate(str);
		ConversionException exc = null;
		for (String dateFormat : DATE_FORMAT_STRINGS) {
			try {
				return PropertyConverter.toDate(str, dateFormat);
			} catch (ConversionException e) {
				exc = e;
			}
		}
		throw new RuntimeException(exc);
	}

	@Override
	public long getInterval(ConfigurationKey key) {
		String str = getString(key);
		if (str == null) {
			throw new RuntimeException("No property for '" + key + "' (expected interval)");
		}
		return convertToInterval(str);
	}

	@Override
	public long getInterval(ConfigurationKey key, long defaultValue) {
		String str = getString(key);
		if (str != null) {
			return convertToInterval(str);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	@Override
	public Long getInterval(ConfigurationKey key, String defaultValue) {
		String str = getString(key);
		if (str != null) {
			return convertToInterval(str);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue == null ? null : convertToInterval(defaultValue);
	}

	private static long convertToInterval(String str) {
		return IntervalConverter.convert(str);
	}

	@Override
	public <E extends Enum<E>> E getEnumValue(Class<E> E, ConfigurationKey key) {
		return getEnumValue(E, key, null);
	}

	@Override
	public <E extends Enum<E>> E getEnumValue(Class<E> E, ConfigurationKey key, E defaultValue) {
		String stringValue = getString(key);
		if (stringValue != null) {
			try {
				return Enum.valueOf(E, stringValue);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("For '" + key + "' expected " + E + " but found '" + stringValue + "'", e);
			}
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	@Override
	public <E extends Enum<E>> E getEnumValue(ConfigurationKey key, E defaultValue) {
		Class<E> E = defaultValue.getDeclaringClass();
		return getEnumValue(E, key, defaultValue);
	}

	@Override
	public <E extends Enum<E>> List<E> getEnumList(Class<E> E, ConfigurationKey key) {
		return getEnumList(E, key, Collections.<E> emptyList());
	}

	@Override
	public <E extends Enum<E>> List<E> getEnumList(Class<E> E, ConfigurationKey key, List<E> defaultValue) {
		List<String> list = getList(key, null);
		if (list != null) {
			return convertToEnum(E, list);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	private static <E extends Enum<E>> List<E> convertToEnum(Class<E> E, List<String> list) {
		List<E> result = new ArrayList<E>(list.size());
		for (String v : list) {
			result.add(Enum.valueOf(E, v));
		}
		return result;
	}

	@Override
	public <C> Class<? extends C> getClass(ConfigurationKey key) {
		String className = getString(key);
		if (className == null) {
			return null;
		}
		return classResolver.resolve(className);
	}

	@Override
	public <C> Class<? extends C> getClass(ConfigurationKey key, Class<? extends C> defaultValue) {
		String className = getString(key);
		if (className != null) {
			return classResolver.resolve(className);
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	protected String interpolate(String value) {
		return ExtendedCompositeConfiguration.interpolateValue(value);
	}

	@Override
	public Sequence<ConfigurationKey> getKeys() {
		return getKeys(null);
	}

	@Override
	public Sequence<String> getSubKeys() {
		return getSubKeys(null);
	}

	@Override
	public Sequence<ConfigurationKey> getImmediateKeys() {
		return getImmediateKeys(null);
	}

	@Override
	public Sequence<String> getImmediateSubKeys() {
		return getImmediateSubKeys(null);
	}

	@Override
	public Sequence<String> getSubKeys(ConfigurationKey prefix) {
		Sequence<ConfigurationKey> keys = getKeys(prefix);
		if (prefix == null) {
			return keys.transform(Transforms.convertToString());
		}
		final int prefixLength = prefix.toString().length() + 1;
		return keys.transform(new Transform<ConfigurationKey, String>() {
			@Override
			public String transform(ConfigurationKey key) {
				String stringKey = key.toString();
				return prefixLength > stringKey.length() ? null : stringKey.substring(prefixLength);
			}
		}).filter(notNull());
	}

	@Override
	public Sequence<ConfigurationKey> getImmediateKeys(final ConfigurationKey prefix) {
		Sequence<String> subKeys = getImmediateSubKeys(prefix);
		if (prefix == null) {
			return subKeys.transform(Transforms.<String, ConfigurationKey> construct(ConfKey.class, String.class));
		}
		return subKeys.transform(new Transform<String, ConfigurationKey>() {
			@Override
			public ConfigurationKey transform(String subKey) {
				return subKey == null ? prefix : prefix.dot(subKey);
			}
		});
	}

	@Override
	public Sequence<String> getImmediateSubKeys(ConfigurationKey prefix) {
		Sequence<ConfigurationKey> keys = getKeys(prefix).materialise();
		Set<String> subKeys = new HashSet<String>(keys.size());
		int numPart = prefix == null ? 0 : StringUtils.split(prefix.toString(), '.').length;
		for (ConfigurationKey key : keys) {
			String[] parts = StringUtils.split(key.toString(), ".", numPart + 2);
			subKeys.add(numPart < parts.length ? parts[numPart] : null);
		}
		return seq(subKeys);
	}

	@Override
	public Map<String, Object> asMap(ConfigurationKey prefix, Iterable<String> subKeys) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String subKey : subKeys) {
			map.put(subKey, getProperty(subKey == null ? prefix : prefix.dot(subKey)));
		}
		return map;
	}

	@Override
	public Map<ConfigurationKey, Object> asMap() {
		return asMap(getKeys());
	}

	@Override
	public Map<ConfigurationKey, Object> asMap(Iterable<ConfigurationKey> keys) {
		Map<ConfigurationKey, Object> map = new HashMap<ConfigurationKey, Object>();
		for (ConfigurationKey key : keys) {
			map.put(key, getProperty(key));
		}
		return map;
	}

	@Override
	public LinkedHashMap<String, String> getMap(ConfigurationKey key, List<String> mapKeys) {
		final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Properties props = getProperties(key);
		for (final String mapKey : mapKeys) {
			final String value = props.getProperty(mapKey);
			map.put(mapKey, value);
		}
		return map;
	}

	private Integer cachedSeed;

	@Override
	public int getSeed(ConfigurationKey key, Integer defaultValue) {
		if (cachedSeed != null) {
			return cachedSeed;
		}
		if (containsKey(key)) {
			String seed = getString(key);
			if (isNumeric(seed)) {
				cachedSeed = parseInt(seed);
				return cachedSeed;
			} else if (isEmpty(seed) || seed.equals("random")) {
				cachedSeed = new MersenneTwister().nextInt(1000);
				return cachedSeed;
			}
		}
		cachedSeed = defaultValue;
		logger.warn("Explicit seed not set, asumming default value: " + cachedSeed);
		return cachedSeed;
	}

	@Override
	public int getSeed(ConfigurationKey key) {
		if (cachedSeed != null) {
			return cachedSeed;
		}
		if (containsKey(key)) {
			String seed = getString(key);
			if (isNumeric(seed)) {
				cachedSeed = parseInt(seed);
				return cachedSeed;
			}
		}
		cachedSeed = new MersenneTwister().nextInt(1000);
		logger.warn("Explicit seed not set, asumming random value: " + cachedSeed);
		return cachedSeed;
	}
}
