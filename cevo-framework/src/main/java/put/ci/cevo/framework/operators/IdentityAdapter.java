package put.ci.cevo.framework.operators;

/**
 * For a case when adapting is not needed (X=Y)
 */
public class IdentityAdapter<X> implements IndividualAdapter<X, X> {
	/**
	 * {@inheritDoc}
	 * */
	public X from(X object) {
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	public X from(X object, X template) {
		return object;
	}
}
