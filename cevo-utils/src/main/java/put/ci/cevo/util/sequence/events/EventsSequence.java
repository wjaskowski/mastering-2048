package put.ci.cevo.util.sequence.events;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import put.ci.cevo.util.sequence.Sequence;

/**
 * This sequence fires the events of {@link SequenceListener} each time it is iterated. Total amount of calls to
 * {@link SequenceListener#onNext(Object)} might be higher than the size of this sequence if it is iterated more than
 * once.
 */
public class EventsSequence<T> extends Sequence<T> implements SequenceEventsProvider<T> {

	private class ListeningIterator implements Iterator<T> {

		private final Iterator<? extends T> baseIterator;

		public ListeningIterator(Iterator<? extends T> baseIterator) {
			this.baseIterator = baseIterator;
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = baseIterator.hasNext();
			if (!hasNext) {
				fireFinished();
			}
			return hasNext;
		}

		@Override
		public T next() {
			fireBeforeNext();
			T next = baseIterator.next();
			fireNext(next);
			return next;
		}

		@Override
		public void remove() {
			baseIterator.remove();
		}

	}

	private final List<SequenceListener<? super T>> listeners;

	public EventsSequence(Iterable<? extends T> baseSequence) {
		this(baseSequence, Collections.<SequenceListener<? super T>> emptyList());
	}

	public EventsSequence(Iterable<? extends T> baseSequence, SequenceListener<? super T> listener) {
		this(baseSequence, Collections.<SequenceListener<? super T>> singletonList(listener));
	}

	public EventsSequence(final Iterable<? extends T> baseSequence, Collection<SequenceListener<? super T>> listeners) {
		this.listeners = new CopyOnWriteArrayList<SequenceListener<? super T>>(listeners);
		setMultiIterable(new FinishableIterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new ListeningIterator(baseSequence.iterator());
			}

			@Override
			public void finished() {
				finished(baseSequence);
				fireFinished();
			}
		}, iterableSize(baseSequence));
	}

	@Override
	public void addListener(SequenceListener<T> listener) {
		listeners.add(listener);
	}

	private void fireBeforeNext() {
		for (SequenceListener<? super T> listener : listeners) {
			listener.onBeforeNext();
		}
	}

	private void fireNext(T elem) {
		for (SequenceListener<? super T> listener : listeners) {
			listener.onNext(elem);
		}
	}

	private void fireFinished() {
		for (SequenceListener<? super T> listener : listeners) {
			listener.onFinished();
		}
	}

}
