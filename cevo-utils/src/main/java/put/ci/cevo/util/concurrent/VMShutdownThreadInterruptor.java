package put.ci.cevo.util.concurrent;

import static java.lang.Runtime.getRuntime;
import static java.lang.Thread.currentThread;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VMShutdownThreadInterruptor {

	private static final VMShutdownThreadInterruptor instance = new VMShutdownThreadInterruptor();

	private final List<WeakReference<Thread>> threads;

	private VMShutdownThreadInterruptor() {
		threads = new ArrayList<WeakReference<Thread>>();
		getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				synchronized (threads) {
					for (WeakReference<Thread> ref : threads) {
						Thread thread = ref.get();
						if (thread != null) {
							thread.interrupt();
						}
					}
				}
			}
		});
	}

	public static void register() {
		register(currentThread());
	}

	public static void register(Thread thread) {
		synchronized (instance.threads) {
			instance.threads.add(new WeakReference<Thread>(thread));
		}
	}

}
