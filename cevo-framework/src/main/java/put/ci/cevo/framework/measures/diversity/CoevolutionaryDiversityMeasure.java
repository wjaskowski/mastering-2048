package put.ci.cevo.framework.measures.diversity;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * TODO: It might be worth to consider a single method which returns an instance of a class containing measurements
 * for solutions and tests.
 */
public interface CoevolutionaryDiversityMeasure<S, T> {

	public Measurement measureSolutionsDiversity(PayoffTable<S, T> payoffs, ThreadedContext context);

	public Measurement measureTestsDiversity(PayoffTable<T, S> payoffs, ThreadedContext context);
}
