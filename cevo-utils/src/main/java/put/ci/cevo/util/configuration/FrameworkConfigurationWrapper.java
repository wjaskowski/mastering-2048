package put.ci.cevo.util.configuration;

import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.Sequences.transform;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.event.EventSource;

import org.apache.log4j.Logger;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;

public class FrameworkConfigurationWrapper extends AbstractFrameworkConfiguration {

	private static final Logger logger = Logger.getLogger(AbstractFrameworkConfiguration.class);

	private AbstractConfiguration baseConfiguration;

	private LinkedHashSet<String> keys;

	private final ConfigurationListener listener = new ConfigurationListener() {
		@Override
		public void configurationChanged(ConfigurationEvent event) {
			if (!event.isBeforeUpdate()) {
				Object source = event.getSource();
				if (source instanceof AbstractConfiguration) {
					loadKeys((AbstractConfiguration) source);
				}
			}
		}
	};

	public FrameworkConfigurationWrapper(AbstractConfiguration baseConfiguration) {
		setBaseConfiguration(baseConfiguration);
	}

	public void override(AbstractConfiguration override) {
		CompositeConfiguration overridden = new ExtendedCompositeConfiguration();
		overridden.addConfiguration(override);
		overridden.addConfiguration(baseConfiguration);
		setBaseConfiguration(overridden);
	}

	private void setBaseConfiguration(AbstractConfiguration baseConfig) {
		baseConfig.addConfigurationListener(listener);
		if (baseConfiguration != null) {
			baseConfiguration.removeConfigurationListener(listener);
		}
		baseConfiguration = baseConfig;
		loadKeys(baseConfig);
	}

	private void loadKeys(AbstractConfiguration config) {
		Iterator<String> baseKeys = config.getKeys();
		keys = new LinkedHashSet<>(seq(baseKeys).toList());
	}

	public AbstractConfiguration getBaseConfiguration() {
		return baseConfiguration;
	}

	@Override
	public EventSource getEventSource() {
		return baseConfiguration;
	}

	@Override
	public boolean containsKey(ConfigurationKey key) {
		return keys.contains(key.toString());
	}

	@Override
	public String getString(ConfigurationKey key) {
		String value = baseConfiguration.getString(key.toString());
		logValue(key, value);
		return value;
	}

	@Override
	public String getString(ConfigurationKey key, String defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		String string = baseConfiguration.getString(key.toString(), defaultValue);
		logValue(key, string);
		return string;
	}

	private <T> void checkAndLogDefaultValueWarning(ConfigurationKey key, T defaultValue) {
		if (!baseConfiguration.containsKey(key.toString())) {
			logDefaultValueWarning(key, defaultValue);
		}
	}

	@Override
	public List<String> getList(ConfigurationKey key) {
		List<String> strings = seq(baseConfiguration.getList(key.toString(), Collections.emptyList())).transform(
				new Transform<Object, String>() {
					@Override
					public String transform(Object object) {
						return object.toString();
					}
				}).toList();
		logValue(key, strings);
		return strings;
	}

	@Override
	public List<String> getList(ConfigurationKey key, List<String> defaultValue) {
		List<Object> list = baseConfiguration.getList(key.toString(), null);
		if (list != null) {
			List<String> strings = seq(list).transform(new Transform<Object, String>() {
				@Override
				public String transform(Object object) {
					return object.toString();
				}
			}).toList();
			logValue(key, strings);
			return strings;
		}
		logDefaultValueWarning(key, defaultValue);
		return defaultValue;
	}

	@Override
	public Properties getProperties(ConfigurationKey key) throws IllegalArgumentException {
		return baseConfiguration.getProperties(key.toString());
	}

	@Override
	public Object getProperty(ConfigurationKey key) {
		return baseConfiguration.getProperty(key.toString());
	}

	@Override
	public boolean getBoolean(ConfigurationKey key) {
		boolean aBoolean = baseConfiguration.getBoolean(key.toString());
		logValue(key, aBoolean);
		return aBoolean;
	}

	private void logValue(ConfigurationKey key, Object value) {
		if (value != null && baseConfiguration.containsKey(key.toString()))
			logger.info("Read config: " + key + " = " + value.toString());
	}

	@Override
	public Boolean getBoolean(ConfigurationKey key, Boolean defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		Boolean aBoolean = baseConfiguration.getBoolean(key.toString(), defaultValue);
		logValue(key, aBoolean);
		return aBoolean;
	}

	@Override
	public byte getByte(ConfigurationKey key) {
		Byte aByte = baseConfiguration.getByte(key.toString());
		logValue(key, aByte);
		return aByte;
	}

	@Override
	public Byte getByte(ConfigurationKey key, Byte defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		Byte aByte = baseConfiguration.getByte(key.toString(), defaultValue);
		logValue(key, aByte);
		return aByte;
	}

	@Override
	public double getDouble(ConfigurationKey key) {
		double aDouble = baseConfiguration.getDouble(key.toString());
		logValue(key, aDouble);
		return aDouble;
	}

	@Override
	public Double getDouble(ConfigurationKey key, Double defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		double value = baseConfiguration.getDouble(key.toString(), defaultValue);
		logValue(key, value);
		return value;
	}

	@Override
	public float getFloat(ConfigurationKey key) {
		float value = baseConfiguration.getFloat(key.toString());
		logValue(key, value);
		return value;
	}

	@Override
	public Float getFloat(ConfigurationKey key, Float defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		float value = baseConfiguration.getFloat(key.toString(), defaultValue);
		logValue(key, value);
		return value;
	}

	@Override
	public int getInt(ConfigurationKey key) {
		int anInt = baseConfiguration.getInt(key.toString());
		logValue(key, anInt);
		return anInt;
	}

	@Override
	public Integer getInt(ConfigurationKey key, Integer defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		Integer integer = baseConfiguration.getInteger(key.toString(), defaultValue);
		logValue(key, integer);
		return integer;
	}

	@Override
	public long getLong(ConfigurationKey key) {
		Long aLong = baseConfiguration.getLong(key.toString());
		logValue(key, aLong);
		return aLong;
	}

	@Override
	public Long getLong(ConfigurationKey key, Long defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		Long aLong = baseConfiguration.getLong(key.toString(), defaultValue);
		logValue(key, aLong);
		return aLong;
	}

	@Override
	public short getShort(ConfigurationKey key) {
		Short aShort = baseConfiguration.getShort(key.toString());
		logValue(key, aShort);
		return aShort;
	}

	@Override
	public Short getShort(ConfigurationKey key, Short defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		Short aShort = baseConfiguration.getShort(key.toString(), defaultValue);
		logValue(key, aShort);
		return aShort;
	}

	@Override
	public BigDecimal getBigDecimal(ConfigurationKey key) {
		BigDecimal bigDecimal = baseConfiguration.getBigDecimal(key.toString());
		logValue(key, bigDecimal);
		return bigDecimal;
	}

	@Override
	public BigDecimal getBigDecimal(ConfigurationKey key, BigDecimal defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		BigDecimal bigDecimal = baseConfiguration.getBigDecimal(key.toString(), defaultValue);
		logValue(key, bigDecimal);
		return bigDecimal;
	}

	@Override
	public BigInteger getBigInteger(ConfigurationKey key) {
		BigInteger bigInteger = baseConfiguration.getBigInteger(key.toString());
		logValue(key, bigInteger);
		return bigInteger;
	}

	@Override
	public BigInteger getBigInteger(ConfigurationKey key, BigInteger defaultValue) {
		checkAndLogDefaultValueWarning(key, defaultValue);
		BigInteger bigInteger = baseConfiguration.getBigInteger(key.toString(), defaultValue);
		logValue(key, bigInteger);
		return bigInteger;
	}

	@Override
	public Sequence<ConfigurationKey> getKeys(ConfigurationKey prefix) {
		if (prefix == null) {
			return transform(keys, Transforms.<String, ConfigurationKey> construct(ConfKey.class, String.class));
		}
		return transform(baseConfiguration.getKeys(prefix.toString()),
			Transforms.<String, ConfigurationKey> construct(ConfKey.class, String.class));
	}

}
