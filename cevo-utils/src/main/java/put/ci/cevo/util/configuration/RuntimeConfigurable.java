package put.ci.cevo.util.configuration;

import static com.google.common.base.Objects.equal;

import java.lang.reflect.Method;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import put.ci.cevo.util.ReflectionUtils;

/**
 * The class for a dynamic runtime configuration parameter, automatically synchronized with {@link Configuration}.
 */
public class RuntimeConfigurable<T> {

	private static final Logger logger = Logger.getLogger(RuntimeConfigurable.class);

	private static abstract class ChangeListener implements ConfigurationListener {

		private final String key;

		public ChangeListener(ConfigurationKey key) {
			this.key = key.toString();
		}

		@Override
		public void configurationChanged(ConfigurationEvent event) {
			if (!event.isBeforeUpdate()) {
				String propertyName = event.getPropertyName();
				if (propertyName == null || propertyName.startsWith(key)) {
					valueChanged();
				}
			}
		}

		protected abstract void valueChanged();

	}

	private static class Wrapper<T> {

		private final T elem;

		public Wrapper(T elem) {
			this.elem = elem;
		}

		public T getElem() {
			return elem;
		}

	}

	protected final String name;
	protected final ConfigurationKey key;
	protected final Method getter;
	protected final T defaultValue;

	/** Value for {@link LocallyConfiguredThread} when overridden. */
	protected final ThreadLocal<Wrapper<T>> overriddenValue;

	/** Current value for threads other than {@link LocallyConfiguredThread}. */
	private volatile T value;

	/** When using this constructor, must override {@link #retrieveValue(NEConfiguration, ConfigurationKey)}. */
	public RuntimeConfigurable(String name, ConfigurationKey key) {
		this(name, key, (String) null, null);
	}

	public RuntimeConfigurable(String name, ConfigurationKey key, T defaultValue) {
		this(name, key, defaultValue.getClass(), defaultValue);
	}

	public RuntimeConfigurable(String name, ConfigurationKey key, Class<?> type) {
		this(name, key, getType(type), null);
	}

	public RuntimeConfigurable(String name, ConfigurationKey key, Class<?> type, T defaultValue) {
		this(name, key, getType(type), defaultValue);
	}

	public RuntimeConfigurable(String name, ConfigurationKey key, String type, T defaultValue) {
		this.name = name;
		this.key = key;
		getter = type == null ? null : ReflectionUtils.getMatchingMethod(FrameworkConfiguration.class, "get"
			+ StringUtils.capitalize(type), ConfigurationKey.class);
		this.defaultValue = defaultValue;
		overriddenValue = new ThreadLocal<Wrapper<T>>() {
			@Override
			protected Wrapper<T> initialValue() {
				Thread thread = Thread.currentThread();
				if (thread instanceof LocallyConfiguredThread) {
					FrameworkConfiguration localConfiguration = ((LocallyConfiguredThread) thread)
						.getLocalConfiguration();
					if (localConfiguration.containsKey(RuntimeConfigurable.this.key)) {
						return new Wrapper<T>(retrieveValue(localConfiguration, RuntimeConfigurable.this.key));
					}
					return null;
				}
				throw new RuntimeException("Called for non-locally configured thread");
			}
		};
		ChangeListener listener = new ChangeListener(key) {
			@Override
			protected void valueChanged() {
				setValue(retrieveValue(Configuration.getGlobalConfiguration(), RuntimeConfigurable.this.key), false);
			}
		};
		Configuration.getConfiguration().getEventSource().addConfigurationListener(listener);
		setValue(retrieveValue(Configuration.getConfiguration(), key), true);
	}

	private static String getType(Class<?> type) {
		if (type == Integer.class) {
			return "Int";
		}
		return type.getSimpleName();
	}

	public ConfigurationKey getKey() {
		return key;
	}

	/** The method should read the value from the configuration passed. */
	protected T retrieveValue(FrameworkConfiguration configuration, ConfigurationKey key) {
		if (configuration.containsKey(key)) {
			return ReflectionUtils.<T> invoke(configuration, getter, key);
		}
		return defaultValue;
	}

	private void setValue(T value, boolean forceLogChange) {
		if (forceLogChange || !equal(this.value, value)) {
			logNewValue(key, value);
		}
		this.value = value;
	}

	private void logNewValue(ConfigurationKey key, T value) {
		StringBuilder sb = new StringBuilder("Value ");
		if (name == null) {
			sb.append("of '" + key + "'");
		} else {
			sb.append(name + " ('" + key + "')");
		}
		sb.append(" set to: " + displayValue(value));
		logger.info(sb.toString());
	}

	protected Object displayValue(T value) {
		return value;
	}

	public T getValue() {
		Thread thread = Thread.currentThread();
		if (thread instanceof LocallyConfiguredThread) {
			Wrapper<T> val = overriddenValue.get();
			if (val != null) {
				return val.getElem();
			}
		}
		return value;
	}

}
