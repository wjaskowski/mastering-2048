package put.ci.cevo.util.sequence;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import put.ci.cevo.util.Pair;

public class MultiSequence<T> extends Sequence<T> {

	public static class MultiSequenceIterator<Ti> implements Iterator<Ti> {

		private final Iterator<? extends Iterable<? extends Ti>> sequences;

		private Iterator<? extends Ti> currIterator;
		private Iterator<? extends Ti> nextIterator;

		public MultiSequenceIterator(Iterable<? extends Iterable<? extends Ti>> sequences) {
			this.sequences = sequences.iterator();
		}

		@Override
		public boolean hasNext() {
			while (nextIterator == null || !nextIterator.hasNext()) {
				if (!sequences.hasNext()) {
					return false;
				}
				nextIterator = sequences.next().iterator();
			}
			return true;
		}

		@Override
		public Ti next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			currIterator = nextIterator;
			return nextIterator.next();
		}

		@Override
		public void remove() {
			if (currIterator == null) {
				throw new IllegalStateException();
			}
			currIterator.remove();
		}

	}

	public static <T> Sequence<T> fromSequences(Iterable<? extends Iterable<? extends T>> sequences) {
		return new MultiSequence<T>(sequences);
	}

	public <L extends Iterable<? extends T>> MultiSequence(Iterable<L> sequences) {
		Pair<Iterable<L>, Integer> pair = multiSize(sequences);
		final Iterable<L> iterables = pair.first();
		int size = pair.second();
		setMultiIterable(new FinishableIterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new MultiSequenceIterator<T>(iterables);
			}

			@Override
			public void finished() {
				try {
					for (Iterable<?> iterable : iterables) {
						finished(iterable);
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}, size);
	}

	private static final int MAX_NUM_SEQUENCES_TO_COMPUTE_SIZE = 100;

	private static <T, L extends Iterable<? extends T>> Pair<Iterable<L>, Integer> multiSize(Iterable<L> sequences) {
		Collection<L> iterables = null;
		int size = UNKNOWN_SIZE;
		if (sequences instanceof Collection) {
			iterables = (Collection<L>) sequences;
			size = multiIterableSize(iterables);
		} else if (sequences instanceof Sequence) {
			Sequence<L> sequencesSeq = (Sequence<L>) sequences;
			if (sequencesSeq.isSizeKnown() && sequencesSeq.size() <= MAX_NUM_SEQUENCES_TO_COMPUTE_SIZE) {
				iterables = sequencesSeq.toList();
				size = multiIterableSize(iterables);
			}
		}
		return Pair.create(iterables == null ? sequences : iterables, size);
	}

	public static int multiIterableSize(Collection<? extends Iterable<?>> sequences) {
		int size = 0;
		for (Iterable<?> iterable : sequences) {
			int seqSize = iterableSize(iterable);
			if (seqSize == UNKNOWN_SIZE) {
				return UNKNOWN_SIZE;
			}
			size += seqSize;
		}
		return size;
	}

}
