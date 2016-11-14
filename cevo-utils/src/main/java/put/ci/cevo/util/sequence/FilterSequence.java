package put.ci.cevo.util.sequence;

import java.util.Iterator;
import java.util.NoSuchElementException;

import put.ci.cevo.util.filter.Filter;

public class FilterSequence<T> extends Sequence<T> {

	private static class FilterIterator<T> implements Iterator<T> {

		private final Iterator<? extends T> iterator;

		private final Filter<? super T> filter;

		private boolean loadedNext = false;

		private T nextElem;

		public FilterIterator(Iterable<? extends T> iterable, Filter<? super T> filter) {
			iterator = iterable.iterator();
			this.filter = filter;
		}

		private void loadNext() {
			while (!loadedNext) {
				if (!iterator.hasNext()) {
					return;
				}
				nextElem = iterator.next();
				if (filter.qualifies(nextElem)) {
					loadedNext = true;
				}
			}
		}

		@Override
		public boolean hasNext() {
			loadNext();
			return loadedNext;
		}

		@Override
		public T next() {
			if (!loadedNext) {
				throw new NoSuchElementException();
			}
			loadedNext = false;
			return nextElem;
		}

		@Override
		public void remove() {
			iterator.remove();
		}

	}

	public static <T> FilterSequence<T> fromIterable(Iterable<? extends T> input, Filter<? super T> filter) {
		return new FilterSequence<T>(input, filter);
	}

	public FilterSequence(final Iterable<? extends T> iterable, final Filter<? super T> filter) {
		super(new FinishableIterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new FilterIterator<T>(iterable, filter);
			}

			@Override
			public void finished() {
				finished(iterable);
			}
		});
	}

}
