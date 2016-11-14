package put.ci.cevo.util.configuration;

import static put.ci.cevo.util.NestedPropertiesParser.split;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertyConverter;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.event.EventSource;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.text.StrLookup;
import org.apache.log4j.Logger;

import put.ci.cevo.util.concurrent.InterruptibleDaemon;

public class ExtendedCompositeConfiguration extends CompositeConfiguration {

	private static final Logger logger = Logger.getLogger(ExtendedCompositeConfiguration.class);

	private static class ConfigsReloadingThread extends InterruptibleDaemon {

		public ConfigsReloadingThread() {
			super();
			setName(getClass().getSimpleName() + '-' + threadId.getAndIncrement());
		}

		private final List<WeakReference<FileConfiguration>> configs = Collections
			.synchronizedList(new LinkedList<WeakReference<FileConfiguration>>());

		public void addConfiguration(FileConfiguration config) {
			configs.add(new WeakReference<FileConfiguration>(config));
		}

		@Override
		protected void doLogic() throws Exception {
			Iterator<WeakReference<FileConfiguration>> it = configs.iterator();
			while (it.hasNext()) {
				FileConfiguration config = it.next().get();
				if (config == null) {
					it.remove();
				} else {
					config.reload();
				}
			}
		}

		@Override
		protected long getSleepTime() {
			return 10000;
		}

		public static ConfigsReloadingThread startThread() {
			ConfigsReloadingThread thread = new ConfigsReloadingThread();
			thread.start();
			return thread;
		}

	}

	private static final AtomicInteger threadId = new AtomicInteger();

	private static final ConfigsReloadingThread configsReloadingThread = ConfigsReloadingThread.startThread();
	private static final ExtendedCompositeConfiguration instance = new ExtendedCompositeConfiguration(true);

	protected ExtendedCompositeConfiguration() {
		this(false);
	}

	/**
	 * Loads a configuration based on the system properties and properties file indicated by the framework.properties
	 * file. Note that the priority of the system properties is highest, meaning that any property passed in such a
	 * manner will be preferred over the one with the same key defined in the properties file.
	 */
	private ExtendedCompositeConfiguration(boolean externalProperties) {
		if (externalProperties) {
			logger.info("Loading runtime configuration");
			addConfiguration(new SystemConfiguration());
			includeConfiguration(System.getProperty("framework.properties"), "System Property: framework.properties");
			if (isEmpty()) {
				logger.warn("Runtime configuration not available, set via "
					+ "-Dframework.properties={path-to-configuration}");
			}
		}
	}

	public static ExtendedCompositeConfiguration getConfiguration() {
		return instance;
	}

	protected void includeConfiguration(String configurationPath, String source) {
		if (configurationPath == null) {
			return;
		}
		try {
			PropertiesConfiguration propConfig = new PropertiesConfiguration();
			propConfig.setEncoding("UTF-8");
			propConfig.load(configurationPath);
			FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
			strategy.setRefreshDelay(10 * 1000);
			propConfig.setReloadingStrategy(strategy);
			addConfiguration(propConfig);
			logger.info("Loaded runtime properties from " + source + ": " + configurationPath);
		} catch (ConfigurationException e) {
			logger.error("Could not load runtime properties from " + source + ": " + configurationPath, e);
		}
	}

	@Override
	protected ConfigurationInterpolator createInterpolator() {
		final StrLookup baseLookup = super.createInterpolator().getDefaultLookup();
		ConfigurationInterpolator interpolator = new ConfigurationInterpolator();
		interpolator.setDefaultLookup(new StrLookup() {
			@Override
			public String lookup(String key) {
				Object value = null;
				try {
					try {
						value = getProperties(key);
					} catch (IllegalArgumentException e) {
						value = getProperty(key);
					}
				} catch (StackOverflowError e) {
					logger.error("Interpolation loop for key '" + key + "'");
					return null;
				}
				if (value == null) {
					return null;
				}
				if (value instanceof Properties) {
					return null;
				} else if (value instanceof Collection<?>) {
					Collection<?> collection = (Collection<?>) value;
					if (collection.size() > 1) {
						return null;
					}
				} else if (value.getClass().isArray() && Array.getLength(value) > 0) {
					if (Array.getLength(value) > 1) {
						return null;
					}
				}
				return baseLookup.lookup(key);
			}
		});
		return interpolator;
	}

	public static String interpolateValue(String value) {
		return instance.interpolate(value);
	}

	@Override
	public void addConfiguration(Configuration config) {
		if (config instanceof EventSource) {
			((EventSource) config).addConfigurationListener(new ConfigurationListener() {
				@Override
				public void configurationChanged(ConfigurationEvent event) {
					ExtendedCompositeConfiguration.this.fireEvent(event.getType(), event.getPropertyName(),
						event.getPropertyValue(), event.isBeforeUpdate());
				}
			});
		}
		if (config instanceof FileConfiguration) {
			configsReloadingThread.addConfiguration((FileConfiguration) config);
		}
		super.addConfiguration(config);
	}

	@Override
	public void addProperty(String key, Object value) {
		fireEvent(EVENT_ADD_PROPERTY, key, value, true);
		Iterator<?> it;
		if (value instanceof String) {
			it = split((String) value, getListDelimiter()).iterator();
		} else {
			it = PropertyConverter.toIterator(value, getListDelimiter());
		}
		while (it.hasNext()) {
			addPropertyDirect(key, it.next());
		}
		fireEvent(EVENT_ADD_PROPERTY, key, value, false);
	}

	@Override
	public Properties getProperties(String key, Properties defaults) {
		String[] tokens = getStringArray(key);
		Properties props = defaults == null ? new Properties() : new Properties(defaults);
		for (String token : tokens) {
			int equalSign = token.indexOf('=');
			if (equalSign > 0) {
				String pkey = token.substring(0, equalSign).trim();
				String pvalue = token.substring(equalSign + 1).trim();
				props.put(pkey, pvalue);
			} else if (token.startsWith("@") || token.startsWith("&")) {
				props.put(token.startsWith("@") ? "class" : "instance", token.substring(1));
			} else if (tokens.length == 1 && "".equals(token)) {
				break;
			} else {
				throw new IllegalArgumentException('\'' + token + "' does not contain an equals sign");
			}
		}
		return props;
	}

	public static ExtendedCompositeConfiguration createEmpty() {
		return new ExtendedCompositeConfiguration();
	}
}
