package put.ci.cevo.framework.measures;

import java.util.function.Supplier;

import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;

public interface PerformanceMeasure<V> {

	/**
	 * A new subject is produced for each measurement (it can be generated at random, in principle)
	 * TODO: It could be also achieved by capturing random. Consider leaving only the method with supplier
	 */
	Measurement measure(RandomFactory<V> subjectFactory, ThreadedContext context);

	default Measurement measure(V subject, ThreadedContext context) {
		return measure(random -> subject, context);
	}

	/**
	 * A new subject is produced for each measurement
	 */
	default Measurement measure(Supplier<V> subjectSupplier, ThreadedContext context) {
		return measure(random -> subjectSupplier.get(), context);
	}
}
