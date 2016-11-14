package put.ci.cevo.util.sequence.events;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import put.ci.cevo.util.TypeUtils;
import put.ci.cevo.util.sequence.Sequence;

import com.google.common.base.Preconditions;

/**
 * This sequence fires the events of {@link SequenceListener} when it is iterated for the first time. After the first
 * call to {@link #iterator()}, adding listeners is not possible. Subsequent calls to {@link #iterator()} will return
 * iterators that do not fire the events of the listeners.
 */
public class OneTimeEventsSequence<T> extends Sequence<T> implements SequenceEventsProvider<T> {

	private final List<SequenceListener<? super T>> listeners;

	private Iterator<T> eventsIterator;

	public OneTimeEventsSequence(Iterable<? extends T> baseSequence) {
		this(baseSequence, Collections.<SequenceListener<? super T>> emptyList());
	}

	public OneTimeEventsSequence(Iterable<? extends T> baseSequence, SequenceListener<? super T> listener) {
		this(baseSequence, Collections.<SequenceListener<? super T>> singletonList(listener));
	}

	public OneTimeEventsSequence(final Iterable<? extends T> baseSequence,
			Collection<SequenceListener<? super T>> listeners) {
		this.listeners = new CopyOnWriteArrayList<SequenceListener<? super T>>(listeners);

		setMultiIterable(new FinishableIterable<T>() {
			@Override
			public Iterator<T> iterator() {
				if (eventsIterator == null) {
					EventsSequence<T> eventsSequence = new EventsSequence<T>(
						baseSequence, OneTimeEventsSequence.this.listeners);
					eventsIterator = eventsSequence.iterator();
					return eventsIterator;
				}
				return TypeUtils.genericCast(baseSequence.iterator());
			}

			@Override
			public void finished() {
				finished(baseSequence);
				for (SequenceListener<?> listener : OneTimeEventsSequence.this.listeners) {
					listener.onFinished();
				}
			}
		}, iterableSize(baseSequence));
	}

	@Override
	public void addListener(SequenceListener<T> listener) {
		Preconditions.checkNotNull(eventsIterator, "Cannot add listeners after iterating!");
		listeners.add(listener);
	}

	/** Ensure that the events were called for all elements of this sequence. */
	public void ensureIterated() {
		if (eventsIterator == null) {
			iterator();
		}
		while (eventsIterator.hasNext()) {
			eventsIterator.next();
		}
	}

}
