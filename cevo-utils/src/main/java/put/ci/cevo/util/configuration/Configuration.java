package put.ci.cevo.util.configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.event.EventSource;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.Sequences;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;

public class Configuration extends AbstractFrameworkConfiguration {

	private static final FrameworkConfigurationWrapper globalConfiguration = new FrameworkConfigurationWrapper(
		ExtendedCompositeConfiguration.getConfiguration());

	private static final Configuration configuration = new Configuration();

	private Configuration() {
		// Singleton
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	static FrameworkConfigurationWrapper getGlobalConfiguration() {
		return globalConfiguration;
	}

	/**
	 * Return thread local configuration if present, and contains the key or key is null, global configuration
	 * otherwise.
	 */
	private FrameworkConfiguration baseConfig(ConfigurationKey key) {
		Thread thread = Thread.currentThread();
		if (thread instanceof LocallyConfiguredThread) {
			FrameworkConfiguration threadConfig = ((LocallyConfiguredThread) thread).getLocalConfiguration();
			if (key == null || threadConfig.containsKey(key)) {
				return threadConfig;
			}
		}
		return globalConfiguration;
	}

	@Override
	public boolean containsKey(ConfigurationKey key) {
		return baseConfig(key).containsKey(key);
	}

	@Override
	public BigDecimal getBigDecimal(ConfigurationKey key, BigDecimal defaultValue) {
		return baseConfig(key).getBigDecimal(key, defaultValue);
	}

	@Override
	public BigDecimal getBigDecimal(ConfigurationKey key) {
		return baseConfig(key).getBigDecimal(key);
	}

	@Override
	public BigInteger getBigInteger(ConfigurationKey key, BigInteger defaultValue) {
		return baseConfig(key).getBigInteger(key, defaultValue);
	}

	@Override
	public BigInteger getBigInteger(ConfigurationKey key) {
		return baseConfig(key).getBigInteger(key);
	}

	@Override
	public Boolean getBoolean(ConfigurationKey key, Boolean defaultValue) {
		return baseConfig(key).getBoolean(key, defaultValue);
	}

	@Override
	public boolean getBoolean(ConfigurationKey key) {
		return baseConfig(key).getBoolean(key);
	}

	@Override
	public Byte getByte(ConfigurationKey key, Byte defaultValue) {
		return baseConfig(key).getByte(key, defaultValue);
	}

	@Override
	public byte getByte(ConfigurationKey key) {
		return baseConfig(key).getByte(key);
	}

	@Override
	public Double getDouble(ConfigurationKey key, Double defaultValue) {
		return baseConfig(key).getDouble(key, defaultValue);
	}

	@Override
	public double getDouble(ConfigurationKey key) {
		return baseConfig(key).getDouble(key);
	}

	@Override
	public EventSource getEventSource() {
		return globalConfiguration.getEventSource();
	}

	@Override
	public Float getFloat(ConfigurationKey key, Float defaultValue) {
		return baseConfig(key).getFloat(key, defaultValue);
	}

	@Override
	public float getFloat(ConfigurationKey key) {
		return baseConfig(key).getFloat(key);
	}

	@Override
	public Integer getInt(ConfigurationKey key, Integer defaultValue) {
		return baseConfig(key).getInt(key, defaultValue);
	}

	@Override
	public int getInt(ConfigurationKey key) {
		return baseConfig(key).getInt(key);
	}

	@Override
	public Sequence<ConfigurationKey> getKeys(final ConfigurationKey prefix) {
		return getAllKeys(new Transform<FrameworkConfiguration, Sequence<ConfigurationKey>>() {
			@Override
			public Sequence<ConfigurationKey> transform(FrameworkConfiguration conf) {
				return conf.getKeys(prefix);
			}
		});
	}

	private Sequence<ConfigurationKey> getAllKeys(
			Transform<FrameworkConfiguration, Sequence<ConfigurationKey>> transform) {
		Thread thread = Thread.currentThread();
		if (thread instanceof LocallyConfiguredThread) {
			Sequence<ConfigurationKey> allKeys = transform.transform(
				((LocallyConfiguredThread) thread).getLocalConfiguration()).add(
				transform.transform(globalConfiguration));
			return Sequences.transform(allKeys.transform(Transforms.convertToString()).toSet(),
				Transforms.<String, ConfigurationKey> construct(ConfKey.class, String.class));
		}
		return transform.transform(globalConfiguration);
	}

	@Override
	public List<String> getList(ConfigurationKey key, List<String> defaultValue) {
		return baseConfig(key).getList(key, defaultValue);
	}

	@Override
	public List<String> getList(ConfigurationKey key) {
		return baseConfig(key).getList(key);
	}

	@Override
	public Long getLong(ConfigurationKey key, Long defaultValue) {
		return baseConfig(key).getLong(key, defaultValue);
	}

	@Override
	public long getLong(ConfigurationKey key) {
		return baseConfig(key).getLong(key);
	}

	@Override
	public Properties getProperties(ConfigurationKey key) throws IllegalArgumentException {
		return baseConfig(key).getProperties(key);
	}

	@Override
	public Object getProperty(ConfigurationKey key) {
		return baseConfig(key).getProperty(key);
	}

	@Override
	public Short getShort(ConfigurationKey key, Short defaultValue) {
		return baseConfig(key).getShort(key, defaultValue);
	}

	@Override
	public short getShort(ConfigurationKey key) {
		return baseConfig(key).getShort(key);
	}

	@Override
	public String getString(ConfigurationKey key, String defaultValue) {
		return baseConfig(key).getString(key, defaultValue);
	}

	@Override
	public String getString(ConfigurationKey key) {
		return baseConfig(key).getString(key);
	}

	public static void addDedicateFiledAppender(Logger logger, String fileName, ConfigurationKey experimentId)
			throws IOException {
		FileAppender appender = new FileAppender(new PatternLayout("%m%n"), fileName + "-"
			+ configuration.getInt(experimentId));
		logger.addAppender(appender);
	}
}
