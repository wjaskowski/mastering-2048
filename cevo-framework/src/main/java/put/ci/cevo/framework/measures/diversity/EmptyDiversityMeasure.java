package put.ci.cevo.framework.measures.diversity;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * This implementation does not do anything meaningful. Simply returns empty {@link Measurement}.
 * Useful for testing purposes.
 */
public class EmptyDiversityMeasure<S, T> implements CoevolutionaryDiversityMeasure<S, T> {

	private static final Measurement EMPTY_MEASUREMENT = new Measurement.Builder().build();

	@Override
	public Measurement measureSolutionsDiversity(PayoffTable<S, T> payoffs, ThreadedContext context) {
		return EMPTY_MEASUREMENT;
	}

	@Override
	public Measurement measureTestsDiversity(PayoffTable<T, S> payoffs, ThreadedContext context) {
		return EMPTY_MEASUREMENT;
	}

}
