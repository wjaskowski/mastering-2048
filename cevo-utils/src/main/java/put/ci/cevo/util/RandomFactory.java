package put.ci.cevo.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import org.apache.commons.collections15.Factory;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.random.ThreadedContext;

/*
 * Similar to <code>Factory<T></code> from apache commons
 */
public interface RandomFactory<T> {
	/**
	 * Create a new object.
	 * 
	 * @return a new object
	 */
	public T create(RandomDataGenerator random);

	default public T create(ThreadedContext context) {
		return create(context.getRandomForThread());
	}

	public static <T> RandomFactory<T> from(Factory<T> factory) {
		return random -> factory.create();
	}

	/** Create random factory generating samples/lists of objects */
	public static <T> RandomFactory<List<T>> sampleFrom(RandomFactory<T> factory, int sampleSize) {
		Preconditions.checkArgument(sampleSize >= 0, "Invalid sample size: " + sampleSize);

		return new RandomFactory<List<T>>() {
			@Override
			public List<T> create(RandomDataGenerator random) {
				return Stream.generate(() -> factory.create(random)).limit(sampleSize).collect(Collectors.toList());
			}
		};
	}
}
