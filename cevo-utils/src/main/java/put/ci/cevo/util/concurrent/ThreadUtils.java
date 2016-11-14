package put.ci.cevo.util.concurrent;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class ThreadUtils {

	private final static Logger logger = Logger.getLogger(ThreadUtils.class);

	public static Thread runAsync(Runnable runnable, String threadName) {
		return runAsync(runnable, threadName, false);
	}

	public static Thread runAsync(Runnable runnable, String threadName, boolean daemon) {
		Thread thread = new Thread(runnable, threadName);
		thread.setDaemon(daemon);
		thread.start();
		return thread;
	}

	public static void join(Collection<Thread> threads) {
		try {
			for (Thread t : threads) {
				t.join();
			}
		} catch (InterruptedException e) {
			logger.warn("Interrupted during waiting for threads.", e);
		}
	}

	public static ThreadPoolExecutor createSingleThreadedPoolExecutor() {
		return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

}
