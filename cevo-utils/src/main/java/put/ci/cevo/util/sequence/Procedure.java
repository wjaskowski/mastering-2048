package put.ci.cevo.util.sequence;

import put.ci.cevo.util.sequence.transforms.Transform;

/**
 * A transform that does not change the object, but instead invokes {@link #process(Object)} on it to allow side
 * effects.
 */
public abstract class Procedure<T> extends Transform<T, T> {

	private static final Procedure<?> EMPTY = new Procedure<Object>() {
		@Override
		public void process(Object object) {
			// empty
		}
	};

	@Override
	public T transform(T object) {
		process(object);
		return object;
	}

	public Procedure<T> chain(final Procedure<? super T> second) {
		return new Procedure<T>() {
			@Override
			public void process(T object) {
				Procedure.this.process(object);
				second.process(object);
			}
		};
	}

	public abstract void process(T object);

	@SuppressWarnings("unchecked")
	public static <T> Procedure<T> empty() {
		return (Procedure<T>) EMPTY;
	}

}
