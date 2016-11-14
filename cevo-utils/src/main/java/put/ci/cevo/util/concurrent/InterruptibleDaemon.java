package put.ci.cevo.util.concurrent;

import static org.apache.log4j.Level.ERROR;
import static org.apache.log4j.Level.FATAL;
import static org.apache.log4j.Level.INFO;
import static org.apache.log4j.Level.WARN;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import put.ci.cevo.util.configuration.LocallyConfiguredThread;

public abstract class InterruptibleDaemon extends LocallyConfiguredThread {

	private static final Logger logger = Logger.getLogger(InterruptibleDaemon.class);

	private static final int DEFAULT_MAX_CONSECUTIVE_ERRORS_COUNT = 10;

	protected int maxConsecutiveErrorsCount = DEFAULT_MAX_CONSECUTIVE_ERRORS_COUNT;

	public void setMaxConsecutiveErrors(int maxConsecutiveErrorsCount) {
		this.maxConsecutiveErrorsCount = maxConsecutiveErrorsCount;
	}

	protected InterruptibleDaemon() {
		this(null);
	}

	protected InterruptibleDaemon(String name) {
		setName(name == null ? getClass().getSimpleName() : name);
		setDaemon(true);
	}

	@Override
	public synchronized void start() {
		VMShutdownThreadInterruptor.register(this);
		super.start();
	}

	@Override
	public void run() {
		try {
			log(INFO, "Started daemon: " + getName());
			if (!sleepTime(getInitialSleepTime())) {
				return;
			}
			int consecutiveErrorsCount = 0;
			do {
				try {
					doLogic();
					consecutiveErrorsCount = 0;
				} catch (InterruptedException e) {
					return;
				} catch (Throwable e) {
					log(ERROR, "Error in daemon " + getName() + " logic", e);
					onError(++consecutiveErrorsCount, e);
				}
				if (!sleepTime(getSleepTime())) {
					return;
				}
			} while (!interrupted());
		} finally {
			log(INFO, "Daemon " + getName() + " stopped");
		}
	}

	protected void log(Level level, String message) {
		log(level, message, null);
	}

	/** Log safely, even if logger is no longer available. */
	protected void log(Level level, String message, Throwable exception) {
		try {
			logger.log(level, message, exception);
		} catch (Throwable e) {
			// ignore
		}
	}

	/** Return true if completed normally, false if interrupted. */
	private boolean sleepTime(long sleepTime) {
		if (sleepTime <= 0) {
			return true;
		}
		try {
			Thread.sleep(sleepTime);
			return true;
		} catch (InterruptedException e) {
			log(INFO, "Daemon " + getName() + " interrupted externally");
			return false;
		}
	}

	/** The sleep time before the first iteration of the daemon. */
	protected long getInitialSleepTime() {
		return getSleepTime();
	}

	/** Return true if the thread should be killed now. */
	protected void onError(int consecutiveErrorsCount, Throwable e) {
		if (consecutiveErrorsCount > maxConsecutiveErrorsCount) {
			log(FATAL, "Max consecutive erros count (" + maxConsecutiveErrorsCount
				+ ") reached - interrupting daemon: " + getName());
			interrupt();
		}
	}

	protected void logCountOnError(int consecutiveErrorsCount) {
		if (consecutiveErrorsCount >= maxConsecutiveErrorsCount) {
			log(WARN, "Consecutive errors count: " + consecutiveErrorsCount);
		}
	}

	/** Sleep time before the next call to {@link #doLogic()}. */
	protected abstract long getSleepTime();

	/** The main logic of the daemon. */
	protected abstract void doLogic() throws Exception;

}
