package put.ci.cevo.util.random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import put.ci.cevo.util.sequence.transforms.LazyMap;

import java.util.Map;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static put.ci.cevo.util.random.ThreadedContext.SINGLE_THREAD;
import static put.ci.cevo.util.random.ThreadedContext.POOL_THREAD_NAME;

/**
 * Provides a separate random data generator for each thread it is called from. Thread-safe.
 */
public class ThreadedRandom {

	private static final Logger logger = Logger.getLogger(ThreadedRandom.class);

	private final Map<String, RandomDataGenerator> generators;

	public ThreadedRandom() {
		this(new MersenneTwister());
	}

	public ThreadedRandom(long seed) {
		this(new MersenneTwister(seed), SINGLE_THREAD);
	}

	public ThreadedRandom(long seed, int threads) {
		this(new MersenneTwister(seed), threads);
	}

	public ThreadedRandom(RandomGenerator mainRandom) {
		this(mainRandom, SINGLE_THREAD);
	}

	public ThreadedRandom(RandomGenerator mainRandom, int threads) {
		this.generators = createPooledRandoms(mainRandom, threads);
	}

	private static Map<String, RandomDataGenerator> createPooledRandoms(final RandomGenerator random, int poolSize) {
		final Map<String, RandomDataGenerator> generators = new LazyMap<String, RandomDataGenerator>() {
			@Override
			protected RandomDataGenerator transform(String threadName) {
				long seed = random.nextLong();
				logger.debug("Creating a new random data generator for thread: " + threadName + " with seed: " + seed);
				return new RandomDataGenerator(new MersenneTwister(seed));
			}
		};

		// main thread
		createRandomDataGenerator(currentThread().getName(), random.nextLong(), generators);

		// pool threads
		for (int i = 0; i < poolSize; i++) {
			createRandomDataGenerator(format(POOL_THREAD_NAME, i), random.nextLong(), generators);
		}

		return generators;
	}

	private static void createRandomDataGenerator(String threadName, long seed, Map<String, RandomDataGenerator> generators) {
		generators.put(threadName, new RandomDataGenerator(new MersenneTwister(seed)));
		logger.debug("Creating a new random data generator for thread: " + threadName + " with seed: " + seed);
	}

	/**
	 * Calling {@link #forThread()} from concurrent threads may cause non-deterministic assignment of seeds to the
	 * generators. To avoid this race condition, make sure you always call this method from a thread that created an
	 * instance of {@link ThreadedRandom}.
	 */
	public RandomDataGenerator forThread() {
		return forThread(currentThread());
	}

	public RandomDataGenerator forThread(Thread thread) {
		return forThread(thread.getName());
	}

	public RandomDataGenerator forThread(String name) {
		return generators.get(name);
	}

}
