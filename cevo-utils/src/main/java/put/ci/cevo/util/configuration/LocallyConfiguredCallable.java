package put.ci.cevo.util.configuration;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.configuration.AbstractConfiguration;

/**
 * This callable calls its {@link #callInternal()} method inside a locally configured thread.
 */
public class LocallyConfiguredCallable<T> extends LocallyConfiguredThread implements Callable<T> {

	private final Callable<T> callable;

	private volatile Exception exception;
	private volatile T result;

	public LocallyConfiguredCallable() {
		this((Callable<T>) null);
	}

	public LocallyConfiguredCallable(String name) {
		this(name, null);
	}

	public LocallyConfiguredCallable(Callable<T> callable) {
		super();
		this.callable = callable;
	}

	public LocallyConfiguredCallable(String name, Callable<T> callable) {
		super(name);
		this.callable = callable;
	}

	@Override
	public LocallyConfiguredCallable<T> withOverriddenConfiguration(ConfigurationKey key, Object value) {
		super.withOverriddenConfiguration(key, value);
		return this;
	}

	@Override
	public LocallyConfiguredCallable<T> withOverriddenConfiguration(Map<ConfigurationKey, Object> override) {
		super.withOverriddenConfiguration(override);
		return this;
	}

	@Override
	public LocallyConfiguredCallable<T> withOverriddenConfiguration(AbstractConfiguration override) {
		super.withOverriddenConfiguration(override);
		return this;
	}

	/** Override this method (or alternatively pass a callable to the constructor) to define the function. */
	protected T callInternal() throws Exception {
		if (callable == null) {
			throw new RuntimeException("Must either pass a callable or override the callInternal method!");
		}
		return callable.call();
	}

	@Override
	public void run() {
		result = null;
		exception = null;
		try {
			result = callInternal();
		} catch (Exception e) {
			exception = e;
		} catch (Throwable e) {
			exception = new RuntimeException(e);
		}
	}

	public T getResult() throws Exception {
		if (exception != null) {
			throw exception;
		}
		return result;
	}

	/**
	 * After executing the thread (by {@link #start()} or {@link #execute()}) call this method to get the result of
	 * {@link #callInternal()}. If {@link #callInternal()} threw an exception, this exception (wrapped in
	 * {@link RuntimeException}) will be rethrown.
	 */
	public T getResultWrapExceptions() {
		try {
			return getResult();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Call {@link #callInternal()} in a locally configured thread, wait for it to finish and return its result or
	 * rethrow the exception it threw (without wrapping).
	 */
	@Override
	public final T call() throws Exception {
		execute();
		return getResult();
	}

	public final T callWrapExceptions() {
		try {
			return call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
