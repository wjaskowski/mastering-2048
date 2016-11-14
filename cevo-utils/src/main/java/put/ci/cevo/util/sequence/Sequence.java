package put.ci.cevo.util.sequence;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Ordering.natural;
import static put.ci.cevo.util.Pair.create;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import put.ci.cevo.util.Pair;
import put.ci.cevo.util.TypeUtils;
import put.ci.cevo.util.filter.Filter;
import put.ci.cevo.util.info.ProgressInfo;
import put.ci.cevo.util.info.TextProgressInfo;
import put.ci.cevo.util.sequence.aggregates.Aggregate;
import put.ci.cevo.util.sequence.events.ProgressInfoSequence;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.TransformSequence;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class Sequence<T> implements Iterable<T> {

	private static final Logger logger = Logger.getLogger(Sequence.class);

	private static final Sequence<Object> EMPTY_SEQUENCE = new Sequence<Object>(Collections.emptyList());

	protected abstract static class FinishableIterator<U> implements Iterator<U> {

		public void finished() {
			while (hasNext()) {
				next();
			}
		}

		public static void finished(Iterator<?> iterator) {
			try {
				if (iterator instanceof FinishableIterator) {
					((FinishableIterator<?>) iterator).finished();
				} else {
					while (iterator.hasNext()) {
						iterator.next();
					}
				}
			} catch (RuntimeException e) {
				logger.warn("Exception when finishing " + iterator, e);
			}
		}

	}

	public abstract static class FinishableIterable<U> implements Iterable<U> {

		public void finished() {
			FinishableIterator.finished(iterator());
		}

		public static void finished(Iterable<?> iterable) {
			try {
				if (iterable instanceof Collection) {
					return;
				}
				if (iterable instanceof Sequence) {
					((Sequence<?>) iterable).finished();
				} else if (iterable instanceof FinishableIterable) {
					((FinishableIterable<?>) iterable).finished();
				} else {
					FinishableIterator.finished(iterable.iterator());
				}
			} catch (RuntimeException e) {
				logger.warn("Exception when finishing " + iterable, e);
			}
		}

	}

	protected static class OneTimeIterable<U> extends FinishableIterable<U> {

		private final Iterator<? extends U> iterator;
		private boolean active;

		public OneTimeIterable(Iterator<? extends U> iterator) {
			this.iterator = iterator;
			active = true;
		}

		@Override
		public Iterator<U> iterator() {
			if (!active) {
				throw new RuntimeException("The current sequence cannot be iterated more than once!");
			}
			try {
				return TypeUtils.genericCast(iterator);
			} finally {
				active = false;
			}
		}

		public Iterator<? extends U> getIterator() {
			return iterator;
		}

		@Override
		public void finished() {
			if (iterator instanceof FinishableIterator) {
				((FinishableIterator<?>) iterator).finished();
			} else {
				while (iterator.hasNext()) {
					iterator.next();
				}
			}
		}

	}

	public static final int UNKNOWN_SIZE = -1;

	protected Iterable<? extends T> iterable;

	protected int size;

	public static <T> Sequence<T> fromIterable(Iterable<? extends T> iterable) {
		return new Sequence<T>(iterable);
	}

	public static <T> Sequence<T> fromIterator(Iterator<? extends T> iterator) {
		if (!iterator.hasNext()) {
			return emptySequence();
		}
		return new Sequence<T>(iterator);
	}

	public static <T> Sequence<T> fromCollection(Collection<? extends T> collection) {
		if (collection.isEmpty()) {
			return emptySequence();
		}
		return new Sequence<T>(collection);
	}

	public static <T> Sequence<T> singleton(T object) {
		return Sequence.fromCollection(Collections.singleton(object));
	}

	public static <T> Sequence<T> emptySequence() {
		return TypeUtils.genericCast(EMPTY_SEQUENCE);
	}

	public Sequence(Collection<? extends T> collection) {
		this(collection, collection.size());
	}

	public Sequence(Iterator<? extends T> iterator) {
		this(new OneTimeIterable<T>(iterator));
	}

	public Sequence(Iterator<? extends T> iterator, int size) {
		this(new OneTimeIterable<T>(iterator), size);
	}

	public Sequence(Iterable<? extends T> iterable) {
		this(iterable, iterableSize(iterable));
	}

	public static int iterableSize(Iterable<?> iterable) {
		return iterableSize(iterable, UNKNOWN_SIZE);
	}

	public static int iterableSize(Iterable<?> iterable, int defaultSize) {
		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).size();
		}
		if (iterable instanceof Sequence) {
			Sequence<?> seq = (Sequence<?>) iterable;
			return seq.isSizeKnown() ? seq.size() : defaultSize;
		}
		return defaultSize;
	}

	public Sequence(Iterable<? extends T> iterable, int size) {
		this.iterable = iterable;
		this.size = size;
	}

	/** Warning: {@link #iterable} must be initialised in the subclass constructor. */
	protected Sequence() {
		size = UNKNOWN_SIZE;
	}

	protected void setMultiIterable(Iterable<? extends T> iterable) {
		setMultiIterable(iterable, iterableSize(iterable));
	}

	protected void setMultiIterable(Iterable<? extends T> iterable, int size) {
		this.iterable = iterable;
		this.size = size;
	}

	protected void setOneTimeIterable(Iterator<? extends T> iterator) {
		setOneTimeIterable(iterator, UNKNOWN_SIZE);
	}

	protected void setOneTimeIterable(Iterator<? extends T> iterator, int size) {
		iterable = new OneTimeIterable<T>(iterator);
		this.size = size;
	}

	@Override
	public final Iterator<T> iterator() {
		return TypeUtils.genericCast(iterable.iterator());
	}

	/** Returns the first element of the sequence, and possible breaks the sequence. */
	public T getFirst() {
		Iterator<T> iterator = iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	/**
	 * Return the first element from the original sequence. The original sequence is still usable, but the taken element
	 * is skipped.
	 */
	public T takeOne() {
		Iterator<T> original = iterator();
		if (original.hasNext()) {
			iterable = new OneTimeIterable<T>(original);
			return original.next();
		} else {
			return null;
		}
	}

	/**
	 * Return a sequence that takes only the specified number of initial elements from the original sequence. The
	 * original sequence is still usable, but the taken elements are skipped. If the sequence is accessed before the
	 * returned sub-sequence is used up, the result is undefined.
	 */
	public Sequence<T> take(final int size) {
		if (size < 0) {
			throw new IllegalArgumentException(String.valueOf(size));
		}
		final Iterator<T> original = iterator();
		if (!original.hasNext()) {
			return emptySequence();
		}
		iterable = new OneTimeIterable<T>(original);
		return Sequence.fromIterator(new Iterator<T>() {
			private int remaining = size;

			@Override
			public boolean hasNext() {
				return remaining > 0 && original.hasNext();
			}

			@Override
			public T next() {
				if (remaining-- <= 0) {
					throw new NoSuchElementException();
				}
				return original.next();
			}

			@Override
			public void remove() {
				original.remove();
			}
		});
	}

	/**
	 * Return sequences of the specified size (the last sequence can be shorter) that form the original sequence when
	 * joined using {@link MultiSequence}. The resulting sequences are backed by collections and can be used
	 * independently.
	 */
	public Sequence<Sequence<T>> slice(final int sliceSize) {
		return new NullTerminatedSequence<Sequence<T>>() {
			@Override
			protected Sequence<T> getNext() {
				Sequence<T> next = Sequence.this.take(sliceSize).materialise();
				return next.isEmpty() ? null : next;
			}
		};
	}

	public Sequence<Sequence<T>> partition(int sliceSize) {
		return slice(sliceSize);
	}

	public Sequence<Sequence<T>> split(int blocks) {
		float size = isSizeKnown() ? size() : computeSize();
		int partitionSize = (int) Math.ceil(size / blocks);
		return partition(partitionSize > 0 ? partitionSize : 1);
	}

	public Sequence<Pair<T, T>> consecutivePairs() {
		return consecutivePairs(false);
	}

	/**
	 * Returns a sequence of pairs of two consecutive elements from the original sequence. The returned sequence has one
	 * element less than the original one, or the same number of elements if includeLast is set (the second element of
	 * the last pair is then null).
	 */
	public Sequence<Pair<T, T>> consecutivePairs(final boolean includeLast) {
		final Iterator<T> iterator = Sequence.this.iterator();
		if (!iterator.hasNext()) {
			return emptySequence();
		}
		return new NullTerminatedSequence<Pair<T, T>>() {
			private T last = iterator.next();
			private boolean finished = false;

			@Override
			protected Pair<T, T> getNext() {
				if (iterator.hasNext()) {
					T next = iterator.next();
					Pair<T, T> result = Pair.create(last, next);
					last = next;
					return result;
				}
				if (includeLast && !finished) {
					finished = true;
					return Pair.create(last, null);
				}
				return null;
			}
		};
	}

	/**
	 * Return a sequence that drops the specified number of initial elements from the original sequence and takes the
	 * rest.
	 */
	public Sequence<T> drop(int size) {
		Iterator<T> original = iterator();
		for (int i = 0; i < size; i++) {
			if (original.hasNext()) {
				original.next();
			} else {
				break;
			}
		}
		return Sequence.fromIterator(original);
	}

	/** Iterates to the end of the sequence. This might release some resources for some types of sequences. */
	public void finished() {
		FinishableIterable.finished(iterable);
	}

	public <U> Sequence<U> map(Transform<? super T, ? extends U> func) {
		return transform(func);
	}

	public <U> Sequence<U> transform(Transform<? super T, ? extends U> transform) {
		return TransformSequence.fromIterable(this, transform);
	}

	public <U> Sequence<U> collect(Transform<? super T, ? extends U> transform) {
		return transform(transform);
	}

	public <U, V> Multimap<U, V> toMultiMap(Transform<? super T, ? extends Pair<U, V>> transform) {
		return toMultiMap(transform, new Factory<Multimap<U, V>>() {
			@Override
			public Multimap<U, V> create() {
				return ArrayListMultimap.create();
			}
		});
	}

	public <U, V> Multimap<U, V> toMultiMap(Transform<? super T, ? extends Pair<U, V>> transform,
			Factory<Multimap<U, V>> factory) {
		Multimap<U, V> map = factory.create();
		for (T key : this) {
			Pair<U, V> pair = transform.transform(key);
			map.put(pair.first(), pair.second());
		}
		return map;
	}

	public <U, V> Map<U, V> toMap(Transform<? super T, ? extends Pair<U, V>> transform) {
		return toMap(transform, new IdentityHashMap<U, V>());
	}

	public <U, V> Map<U, V> toMap(Transform<? super T, ? extends Pair<U, V>> transform, Map<U, V> map) {
		for (T key : this) {
			Pair<U, V> pair = transform.transform(key);
			map.put(pair.first(), pair.second());
		}
		return map;
	}

	/** Uses {@link IdentityHashMap} as resulting map. */
	public <U> Map<T, U> keysToMap(Transform<? super T, ? extends U> transform) {
		return keysToMap(transform, new Factory<Map<T, U>>() {
			@Override
			public Map<T, U> create() {
				return new IdentityHashMap<>();
			}
		});
	}

	public <U> Map<T, U> keysToMap(Transform<? super T, ? extends U> transform, Factory<Map<T, U>> factory) {
		Map<T, U> map = factory.create();
		for (T key : this) {
			U val = transform.transform(key);
			map.put(key, val);
		}
		return map;
	}

	public Sequence<T> select(Filter<? super T> filter) {
		return FilterSequence.fromIterable(this, filter);
	}

	public Sequence<T> filter(Filter<? super T> filter) {
		return select(filter);
	}

	public Sequence<T> reject(Filter<? super T> filter) {
		return FilterSequence.fromIterable(this, filter.not());
	}

	@SuppressWarnings("unchecked")
	public T find(Filter<? super T> filter) {
		return (T) filter.find(this);
	}

	public Sequence<T> add(T element) {
		return addLast(element);
	}

	public Sequence<T> add(Iterable<? extends T> other) {
		return addLast(other);
	}

	public Sequence<T> addLast(T element) {
		return MultiSequence.fromSequences(of(this, of(element)));
	}

	public Sequence<T> addLast(Iterable<? extends T> other) {
		return MultiSequence.fromSequences(of(this, other));
	}

	public Sequence<T> addFirst(T element) {
		return MultiSequence.fromSequences(of(of(element), this));
	}

	public Sequence<T> addFirst(Iterable<? extends T> other) {
		return MultiSequence.fromSequences(of(other, this));
	}

	public T reduce(Transform<Pair<T, T>, T> function) {
		return reduce(function, takeOne());
	}

	public T reduce(Transform<Pair<T, T>, T> function, T seed) {
		T res = function.transform(create(seed, takeOne()));
		for (T elem : this) {
			res = function.transform(create(res, elem));
		}
		return res;

	}

	public Sequence<Pair<Integer, T>> enumerate() {
		return this.map(new Transform<T, Pair<Integer, T>>() {
			int elem = 0;

			@Override
			public Pair<Integer, T> transform(T object) {
				return create(elem++, object);
			}
		});
	}

	public <U> Sequence<Pair<T, U>> zip(Iterable<U> other) {
		final Iterator<U> otherIterator = other.iterator();
		return transform(new Transform<T, Pair<T, U>>() {
			@Override
			public Pair<T, U> transform(T element) {
				return Pair.create(element, otherIterator.hasNext() ? otherIterator.next() : null);
			}
		});
	}

	public <S> S aggregate(S start, Aggregate<S, ? super T> aggregator) {
		for (T element : this) {
			start = aggregator.aggregate(start, element);
		}
		return start;
	}

	public T aggregate(Aggregate<T, ? super T> aggregator) {
		Iterator<T> it = iterator();
		if (!it.hasNext()) {
			return null;
		}
		T first = it.next();
		return fromIterator(it).aggregate(first, aggregator);
	}

	public Sequence<T> info(String description) {
		return info(description, TextProgressInfo.class);
	}

	public Sequence<T> info(String description, Class<? extends ProgressInfo> clazz) {
		return new ProgressInfoSequence<T>(this, clazz, description);
	}

	public Sequence<T> defensiveCopy() {
		setMultiIterable(toList());
		return this;
	}

	public Sequence<T> materialise() {
		if (!(iterable instanceof Collection)) {
			defensiveCopy();
		}
		return this;
	}

	public Sequence<T> setSize(int size) {
		if (size < 0) {
			throw new RuntimeException("Incorrect size: " + size);
		}
		if (isSizeKnown()) {
			throw new RuntimeException("The size is known: " + size() + ", cannot set it to " + size);
		}
		this.size = size;
		return this;
	}

	public List<T> toImmutableList() {
		return ImmutableList.copyOf(toList());
	}

	public List<T> toList() {
		return fill(new ArrayList<T>());
	}

	public List<T> toList(Factory<List<T>> factory) {
		return fill(factory.create());
	}

	public <F extends Comparable<? super F>> List<F> toSortedList() {
		return toSortedList(natural());
	}

	public <F extends Comparable<? super F>> List<F> toSortedList(Comparator<? super F> order) {
		List<F> list = transform(new Transform<T, F>() {
			@Override
			public F transform(T object) {
				if (object instanceof Comparable) {
					return TypeUtils.explicitCast(object);
				}
				throw new RuntimeException("Unable to sort a list of non-comparable items!");
			}
		}).toList();
		Collections.sort(list, order);
		return list;
	}

	public List<T> sort(Comparator<T> comparator) {
		List<T> list = toList();
		Collections.sort(list, comparator);
		return list;
	}

	public Sequence<T> unpackedSort(Comparator<T> comparator) {
		List<T> list = toList();
		Collections.sort(list, comparator);
		return Sequences.seq(list);
	}

	public Set<T> toSet() {
		return fill(new HashSet<T>());
	}

	public Set<T> toSet(Factory<Set<T>> factory) {
		return fill(factory.create());
	}

	public Object[] toArray() {
		return toList().toArray();
	}

	@SuppressWarnings("unchecked")
	public T[] toArray(Class<T> valClass) {
		List<T> list = toList();
		T[] array = (T[]) Array.newInstance(valClass, list.size());
		for (int i = 0; i < list.size(); ++i) {
			array[i] = list.get(i);
		}
		return array;
	}

	public <C extends Collection<T>> C addToCollection(C collection) {
		return fill(collection);
	}

	public <C extends Collection<T>> C toCollection(C collection) {
		Preconditions.checkArgument(collection.isEmpty(), "Passed collection must be empty!");
		return fill(collection);
	}

	public Collection<T> asCollection() {
		return new AbstractCollection<T>() {
			private Sequence<T> seq = Sequence.this;

			@Override
			public Iterator<T> iterator() {
				return seq.iterator();
			}

			@Override
			public int size() {
				if (seq.isSizeKnown()) {
					return seq.computeSize();
				}
				seq = seq.materialise();
				return seq.size();
			}
		};
	}

	private <C extends Collection<T>> C fill(C collection) {
		for (T elem : this) {
			collection.add(elem);
		}
		return collection;
	}

	/** Checks if the size of this {@link Sequence} is known. */
	public boolean isSizeKnown() {
		return size != UNKNOWN_SIZE;
	}

	/** Returns the size of this {@link Sequence} if it is known. */
	public int size() {
		if (!isSizeKnown()) {
			throw new UnsupportedOperationException("The size of the sequence is unknown. Use isSizeKnown()");
		}
		return size;
	}

	/** Returns the size of this {@link Sequence} if it is known, or the default value. */
	public int size(int defaultSize) {
		if (!isSizeKnown()) {
			return defaultSize;
		}
		return size;
	}

	protected int sizeOrUnknown() {
		return size;
	}

	/** Returns true if the size is known and zero. */
	public boolean isEmpty() {
		if (!isSizeKnown()) {
			throw new UnsupportedOperationException("The size of the sequence is unknown. Use isSizeKnown()");
		}
		return size == 0;
	}

	/** This iterates over the sequence to count the elements if size is not known. */
	public int computeSize() {
		if (isSizeKnown()) {
			return size();
		}
		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).size();
		}
		if (iterable instanceof Sequence) {
			return ((Sequence<?>) iterable).computeSize();
		}
		if (iterable instanceof OneTimeIterable) {
			if (!((OneTimeIterable<?>) iterable).getIterator().hasNext()) {
				return 0;
			}
		}
		int count = 0;
		for (@SuppressWarnings("unused")
		T element : this) {
			count++;
		}
		return count;
	}

	/** If the size is not known, the sequence might become one-time-iterable after calling {@link #computeIsEmpty()}. */
	public boolean computeIsEmpty() {
		if (isSizeKnown()) {
			return isEmpty();
		}
		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).isEmpty();
		}
		if (iterable instanceof Sequence) {
			return ((Sequence<?>) iterable).computeIsEmpty();
		}
		if (iterable instanceof OneTimeIterable) {
			return !((OneTimeIterable<?>) iterable).getIterator().hasNext();
		}
		Iterator<T> iterator = iterator();
		return !iterator.hasNext();
	}

	public boolean any(Filter<? super T> filter) {
		return filter.any(this);
	}

	public boolean all(Filter<? super T> filter) {
		return filter.every(this);
	}

	public boolean none(Filter<? super T> filter) {
		return filter.none(this);
	}

	public String join(String separator) {
		return StringUtils.join(iterable.iterator(), separator);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + (isSizeKnown() ? size() : "?") + "]";
	}

}
