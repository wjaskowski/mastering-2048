package put.ci.cevo.util.configuration;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.event.EventSource;

import put.ci.cevo.util.sequence.Sequence;

public interface FrameworkConfiguration {

	public EventSource getEventSource();

	public <T> T getObject(ConfigurationKey key);

	public <T> T getObject(ConfigurationKey key, T defaultValue);

	public <T> List<T> createConfiguredObjects(ConfigurationKey key);

	public boolean containsKey(ConfigurationKey key);

	public int getSeed(ConfigurationKey key);

	public int getSeed(ConfigurationKey key, Integer defaultValue);

	public String getString(ConfigurationKey key);

	public String getString(ConfigurationKey key, String defaultValue);

	public File getFile(ConfigurationKey key);

	public File getFile(ConfigurationKey key, File defaultValue);

	public List<String> getList(ConfigurationKey key);

	public List<String> getList(ConfigurationKey key, List<String> defaultValue);

	public Properties getProperties(ConfigurationKey key) throws IllegalArgumentException;

	public Object getProperty(ConfigurationKey key);

	public boolean getBoolean(ConfigurationKey key);

	public Boolean getBoolean(ConfigurationKey key, Boolean defaultValue);

	public byte getByte(ConfigurationKey key);

	public Byte getByte(ConfigurationKey key, Byte defaultValue);

	public double getDouble(ConfigurationKey key);

	public Double getDouble(ConfigurationKey key, Double defaultValue);

	public float getFloat(ConfigurationKey key);

	public Float getFloat(ConfigurationKey key, Float defaultValue);

	public int getInt(ConfigurationKey key);

	public Integer getInt(ConfigurationKey key, Integer defaultValue);

	public long getLong(ConfigurationKey key);

	public Long getLong(ConfigurationKey key, Long defaultValue);

	public short getShort(ConfigurationKey key);

	public Short getShort(ConfigurationKey key, Short defaultValue);

	public BigDecimal getBigDecimal(ConfigurationKey key);

	public BigDecimal getBigDecimal(ConfigurationKey key, BigDecimal defaultValue);

	public BigInteger getBigInteger(ConfigurationKey key);

	public BigInteger getBigInteger(ConfigurationKey key, BigInteger defaultValue);

	public List<Integer> getIntList(ConfigurationKey key);

	public List<Integer> getIntList(ConfigurationKey key, List<Integer> defaultValue);

	public List<Long> getLongList(ConfigurationKey key);

	public List<Long> getLongList(ConfigurationKey key, List<Long> defaultValue);

	public List<Double> getDoubleList(ConfigurationKey key);

	public List<Double> getDoubleList(ConfigurationKey key, List<Double> defaultValue);

	public List<File> getFileList(ConfigurationKey key);

	public List<File> getFileList(ConfigurationKey key, List<File> defaultValue);

	public Date getDate(ConfigurationKey key);

	public Date getDate(ConfigurationKey key, Date defaultValue);

	/**
	 * @see IntervalConverter#convert(String)
	 */
	public long getInterval(ConfigurationKey key);

	/**
	 * @see IntervalConverter#convert(String)
	 */
	public long getInterval(ConfigurationKey key, long defaultValue);

	/**
	 * @see IntervalConverter#convert(String)
	 */
	public Long getInterval(ConfigurationKey key, String defaultValue);

	public <E extends Enum<E>> E getEnumValue(Class<E> E, ConfigurationKey key);

	public <E extends Enum<E>> E getEnumValue(Class<E> E, ConfigurationKey key, E defaultValue);

	public <E extends Enum<E>> E getEnumValue(ConfigurationKey key, E defaultValue);

	public <E extends Enum<E>> List<E> getEnumList(Class<E> E, ConfigurationKey key);

	public <E extends Enum<E>> List<E> getEnumList(Class<E> E, ConfigurationKey key, List<E> defaultValue);

	public <C> Class<? extends C> getClass(ConfigurationKey key);

	public <C> Class<? extends C> getClass(ConfigurationKey key, Class<? extends C> defaultValue);

	/**
	 * Return all keys that start with the specified key and a dot, or are equal to this key. If the argument is null,
	 * return all keys, like {@link #getKeys()}.
	 */
	public Sequence<ConfigurationKey> getKeys(ConfigurationKey prefix);

	/** Return all keys in the configuration. */
	public Sequence<ConfigurationKey> getKeys();

	/**
	 * Return all keys that start with the specified key and a dot, with the prefix and the dot truncated. If argument
	 * is null, works like {@link #getSubKeys()}.
	 */
	public Sequence<String> getSubKeys(ConfigurationKey prefix);

	/** Return all keys as strings. */
	public Sequence<String> getSubKeys();

	/**
	 * Get all keys that start with prefix and a dot, or are equal to prefix, but with the ending truncated, so that the
	 * resulting keys are longer by at most one dot-separated part. So if the configuration has keys {@code a},
	 * {@code a.b} and {@code a.x.y}, this method for argument {@code a} will return {@code a}, {@code a.b} and
	 * {@code a.x}. Duplicates are removed.
	 */
	public Sequence<ConfigurationKey> getImmediateKeys(ConfigurationKey prefix);

	/**
	 * Get all keys from the configuration, but truncated to their first dot-separated part. Duplicates are removed.
	 */
	public Sequence<ConfigurationKey> getImmediateKeys();

	/**
	 * Get all keys that start with prefix and a dot, or are equal to prefix, but with the prefix truncated, and the
	 * ending truncated, so that the resulting keys are just one dot-separated part, or null for key equal to the
	 * prefix. So if the configuration has keys {@code a}, {@code a.b} and {@code a.x.y}, this method for argument
	 * {@code a} will return {@code null}, {@code b} and {@code x}. Duplicates are removed.
	 */
	public Sequence<String> getImmediateSubKeys(ConfigurationKey prefix);

	/**
	 * Get all keys from the configuration, but truncated to their first dot-separated part, and represented as string.
	 * Duplicates are removed.
	 */
	public Sequence<String> getImmediateSubKeys();

	/**
	 * Get map from the specified strings to the value read from configuration from keys created by the strings dotted
	 * to the prefix. Prefix and a key can be null.
	 */
	public Map<String, Object> asMap(ConfigurationKey prefix, Iterable<String> subKeys);

	/** Get map of keys and values in the configuration. */
	public Map<ConfigurationKey, Object> asMap();

	/** Get map from the specified keys to their values in the configuration. */
	public Map<ConfigurationKey, Object> asMap(Iterable<ConfigurationKey> keys);

	/**
	 * Get a map of values in the specified configuration key given the map keys used in the configuration key. The
	 * method supports constructs e.g. in the form:
	 * some.config.key=mapKey1=someValue,mapKey2=someOtherValue,mapKey3=value1 \,value2\,value3
	 * 
	 * @param key
	 *            The configuration key for which the values are retrieved.
	 * @param mapKeys
	 *            The keys of the map specified in the configuration key. The order of keys is important in the sense
	 *            that the same order is given in the returned map.
	 * @return A map with the values specified in the configuration. This map preserves the order of values from the
	 *         list given in mapKeys. If there is no value for a specified map key, then there is a null value under
	 * */
	public LinkedHashMap<String, String> getMap(ConfigurationKey key, List<String> mapKeys);
}
