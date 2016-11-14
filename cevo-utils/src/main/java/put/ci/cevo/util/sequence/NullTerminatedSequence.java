package put.ci.cevo.util.sequence;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Sequence that allows lambda-like creation using the abstract method {@link #getNext()}. When {@link #getNext()}
 * returns null, the sequence is finished.
 */
public abstract class NullTerminatedSequence<T> extends Sequence<T> {

	private class ItIterator extends FinishableIterator<T> {

		private T nextElem;

		private boolean finished = false;

		@Override
		public boolean hasNext() {
			if (finished) {
				return false;
			}
			if (nextElem == null) {
				nextElem = getNext();
				if (nextElem == null) {
					finished = true;
					return false;
				}
			}
			return true;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T res = nextElem;
			nextElem = null;
			return res;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void finished() {
			// empty
		}

	}

	public NullTerminatedSequence() {
		setMultiIterable(new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new ItIterator();
			}
		});
	}

	public NullTerminatedSequence(int size) {
		setMultiIterable(new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new ItIterator();
			}
		}, size);
	}

	protected abstract T getNext();

}
