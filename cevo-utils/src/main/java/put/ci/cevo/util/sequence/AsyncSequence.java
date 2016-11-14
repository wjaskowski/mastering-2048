package put.ci.cevo.util.sequence;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class AsyncSequence<T> extends Sequence<T> {

	private class AsyncItIterator extends FinishableIterator<T> {

		@Override
		public boolean hasNext() {
			while (!finished && queue.isEmpty()) {
				getNext();
			}
			return !finished || !queue.isEmpty();
		}

		@Override
		public T next() {
			return queue.removeFirst();
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

	private final LinkedList<T> queue = new LinkedList<T>();

	private boolean finished = false;

	public AsyncSequence() {
		setMultiIterable(new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new AsyncItIterator();
			}
		});
	}

	protected abstract void getNext();

	/** Call this method to put a new element in the sequence. */
	protected void next(T element) {
		queue.add(element);
	}

	/** Call this method to put new elements in the sequence. */
	protected void next(Collection<? extends T> elements) {
		queue.addAll(elements);
	}

	/** Call this method to put new elements in the sequence. */
	protected void next(Iterable<? extends T> elements) {
		for (T element : elements) {
			queue.add(element);
		}
	}

	/** Call this method to put new elements in the sequence. */
	protected void next(T[] elements) {
		for (T element : elements) {
			queue.add(element);
		}
	}

	/** Call to signal that the sequence should be finished. */
	protected void finish() {
		finished = true;
	}

}
