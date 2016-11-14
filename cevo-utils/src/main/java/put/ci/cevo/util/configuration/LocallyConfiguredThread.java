package put.ci.cevo.util.configuration;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;

public class LocallyConfiguredThread extends Thread {

	private static class SavingExceptionHandler implements UncaughtExceptionHandler {

		private Throwable exception;

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			exception = e;
		}

		public Throwable getException() {
			return exception;
		}

	}

	/** Thread-local override of the global configuration. */
	private final FrameworkConfigurationWrapper localConfiguration;

	public LocallyConfiguredThread() {
		super();
		localConfiguration = getInitialThreadConfiguration();
	}

	public LocallyConfiguredThread(String name) {
		super(name);
		localConfiguration = getInitialThreadConfiguration();
	}

	public LocallyConfiguredThread(Runnable target) {
		super(target);
		localConfiguration = getInitialThreadConfiguration();
	}

	public LocallyConfiguredThread(String name, Runnable target) {
		super(target, name);
		localConfiguration = getInitialThreadConfiguration();
	}

	/**
	 * Starts the thread and waits for it to finish. Rethrows any uncaught exception.
	 */
	public void execute() {
		SavingExceptionHandler excHandler = new SavingExceptionHandler();
		setUncaughtExceptionHandler(excHandler);
		start();
		try {
			join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		Throwable exception = excHandler.getException();
		if (exception != null) {
			throw new RuntimeException(exception);
		}
	}

	private FrameworkConfigurationWrapper getInitialThreadConfiguration() {
		Thread thread = Thread.currentThread();
		if (thread instanceof LocallyConfiguredThread) {
			return new FrameworkConfigurationWrapper(
				((LocallyConfiguredThread) thread).localConfiguration.getBaseConfiguration());
		}
		return new FrameworkConfigurationWrapper(new EmptyConfiguration());
	}

	public LocallyConfiguredThread withOverriddenConfiguration(ConfigurationKey key, Object value) {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty(key.toString(), value);
		return withOverriddenConfiguration(override);
	}

	public LocallyConfiguredThread withOverriddenConfiguration(Map<ConfigurationKey, Object> override) {
		AbstractConfiguration overrideConfig = new BaseConfiguration();
		for (Entry<ConfigurationKey, Object> entry : override.entrySet()) {
			overrideConfig.setProperty(entry.getKey().toString(), entry.getValue());
		}
		return withOverriddenConfiguration(overrideConfig);
	}

	public LocallyConfiguredThread withOverriddenConfiguration(AbstractConfiguration override) {
		if (getState() != State.NEW) {
			throw new RuntimeException("Can override configuration only for new threads! (" + this + ")");
		}
		localConfiguration.override(override);
		return this;
	}

	public LocallyConfiguredThread daemonThread(boolean daemon) {
		setDaemon(daemon);
		return this;
	}

	/** Thread-local override of the global configuration. */
	public FrameworkConfiguration getLocalConfiguration() {
		return localConfiguration;
	}

}
