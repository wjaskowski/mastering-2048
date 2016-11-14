package put.ci.cevo.framework.operators;

public interface IndividualAdapter<X, Y> {
	/**
	 * E.g. creates a DoubleVector from NTuples.
	 *
	 * Note: The implementation does not requires a new object in a special case X=Y
	 */
	Y from(X object);

	/**
	 * E.g. creates an NTuples from DoubleVector. DoubleVector is not enough information to create NTuples, thus an
	 * additional template is required.
	 *
	 * Note: The implementation does not requires a new object in a special case X=Y
	 */
	X from(Y object, X template);
}
