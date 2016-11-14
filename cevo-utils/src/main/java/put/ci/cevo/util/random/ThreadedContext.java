package put.ci.cevo.util.random;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.stats.EventsLogger;
import put.ci.cevo.util.stats.EventsLoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static put.ci.cevo.util.sequence.Sequences.flatten;
import static put.ci.cevo.util.sequence.Sequences.seq;

/**
 * <p>
 * This class is intended to provide a versatile facility for parallel and multi-threaded code execution along with
 * thread-safe random data generator and consistent number of threads across the experiment.
 * </p>
 * <p/>
 * <p>
 * Main advantages of this solution:
 * <ul>
 * <li>{@link ThreadedRandom} instance and number of threads are in one place,
 * <li>Efficiency: cached thread pool vs creating new threads
 * </ul>
 * </p>
 */
public class ThreadedContext {

	@FunctionalInterface
	public interface Worker<T, F> {

		F process(T piece, ThreadedContext context) throws Exception;
	}

	private static class WorkerTask<T, F> implements Callable<List<F>> {

		private final Worker<T, F> worker;
		private final Iterable<T> work;

		private final ThreadedContext context;

		public WorkerTask(Worker<T, F> worker, Iterable<T> work, ThreadedContext context) {
			this.worker = worker;
			this.work = work;
			this.context = context;
		}

		@Override
		public List<F> call() throws Exception {
			List<F> result = new LinkedList<>();
			for (T elem : work) {
				result.add(worker.process(elem, context));
			}
			return result;
		}
	}

	static final int SINGLE_THREAD = 1;
	static final String POOL_THREAD_NAME = "pool-thread-%d";

	private final int threads;

	private final ThreadedRandom random;
	private final RandomDataGenerator generator;

	private final ExecutorService executor;
	private final EventsLogger eventsManager;

	public ThreadedContext() {
		this(new ThreadedRandom(), SINGLE_THREAD);
	}

	public ThreadedContext(long seed) {
		this(seed, SINGLE_THREAD);
	}

	public ThreadedContext(long seed, int threads) {
		this(new ThreadedRandom(seed, threads), threads);
	}

	public ThreadedContext(ThreadedRandom random, int threads) {
		this(random, random.forThread(), threads, EventsLoggerFactory.createEmpty());
	}

	private ThreadedContext(ThreadedRandom random, RandomDataGenerator generator, int threads) {
		this(random, generator, threads, EventsLoggerFactory.createEmpty());
	}

	private ThreadedContext(ThreadedRandom random, RandomDataGenerator generator, int threads, EventsLogger eventsManager) {
		this.threads = threads;
		this.random = random;
		this.generator = generator;
		this.executor = getExecutorService(threads);
		this.eventsManager = eventsManager;
	}

	private static ExecutorService getExecutorService(int threads) {
		if (threads == SINGLE_THREAD) {
			return sameThreadExecutor();
		}
		return getExitingExecutorService((ThreadPoolExecutor) newFixedThreadPool(threads,
				new ThreadFactoryBuilder().setNameFormat(POOL_THREAD_NAME).build()));
	}

	/** Returns the same instance of {@link ThreadedContext} but with parallel processing limited to a single thread */
	public ThreadedContext singleThreaded() {
		return new ThreadedContext(random, generator, SINGLE_THREAD, eventsManager);
	}

	public RandomDataGenerator getRandomForThread() {
		return generator;
	}

	public EventsLogger getEventsLogger() {
		return eventsManager;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public <T> void submit(Worker<T, Void> worker, Iterable<T> work) {
		try {
			for (Future<List<Void>> future : executor.invokeAll(prepareTasks(worker, work))) {
				try {
					future.get();
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public <T, F> Sequence<F> invoke(Worker<T, F> worker, Iterable<T> work) {
		try {
			List<WorkerTask<T, F>> tasks = prepareTasks(worker, work);
			return flatten(seq(executor.invokeAll(tasks)).map(new Transform<Future<List<F>>, List<F>>() {
				@Override
				public List<F> transform(Future<List<F>> future) {
					try {
						return future.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				}
			}));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private <F, T> List<WorkerTask<T, F>> prepareTasks(final Worker<T, F> worker, Iterable<T> work) {
		if (threads == SINGLE_THREAD) {
			return singletonList(new WorkerTask<>(worker, work, new ThreadedContext(random, SINGLE_THREAD)));
		}
		return seq(work).split(threads).enumerate().map(new Transform<Pair<Integer, Sequence<T>>, WorkerTask<T, F>>() {
			@Override
			public WorkerTask<T, F> transform(Pair<Integer, Sequence<T>> pair) {
				RandomDataGenerator generator = random.forThread(format(POOL_THREAD_NAME, pair.first()));
				return new WorkerTask<>(worker, pair.second(), new ThreadedContext(random, generator, SINGLE_THREAD));
			}
		}).toList();
	}

	public static ThreadedContext withEventsLogger(ThreadedRandom random, int threads, EventsLogger eventsManager) {
		return new ThreadedContext(random, random.forThread(), threads, eventsManager);
	}

}
