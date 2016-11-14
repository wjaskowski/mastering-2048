package put.ci.cevo.util.sequence.transforms;

import java.util.Iterator;

import put.ci.cevo.util.sequence.Sequence;

public class TransformSequence<T> extends Sequence<T> {

	private static class TransformIterator<F, T> implements Iterator<T> {

		private final Iterator<? extends F> it;

		private final Transform<? super F, ? extends T> tf;

		public TransformIterator(Iterable<? extends F> input, Transform<? super F, ? extends T> tf) {
			this.it = input.iterator();
			this.tf = tf;
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public T next() {
			return tf.transform(it.next());
		}

		@Override
		public void remove() {
			it.remove();
		}

	}

	public static <F, T> TransformSequence<T> fromIterable(Iterable<? extends F> input,
			Transform<? super F, ? extends T> tf) {
		return new TransformSequence<T>(input, tf);
	}

	public <F> TransformSequence(final Iterable<? extends F> input, final Transform<? super F, ? extends T> tf) {
		super(new FinishableIterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new TransformIterator<F, T>(input, tf);
			}

			@Override
			public void finished() {
				finished(input);
			}
		}, iterableSize(input));
	}

}
