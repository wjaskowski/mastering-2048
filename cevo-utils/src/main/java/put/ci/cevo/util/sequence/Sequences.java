package put.ci.cevo.util.sequence;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections15.iterators.ArrayIterator;

import put.ci.cevo.util.filter.Filter;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.TransformSequence;

import com.google.common.collect.ImmutableList;

/**
 * A collection for utility methods concerning sequences.
 */
public class Sequences {

	public static <F, T> Sequence<T> transform(Iterable<? extends F> input, Transform<? super F, ? extends T> tf) {
		return new TransformSequence<T>(input, tf);
	}

	public static <F, T> Sequence<T> transform(Iterator<? extends F> input, Transform<? super F, ? extends T> tf) {
		return transform(seq(input), tf);
	}

	public static <F, T> Sequence<T> transform(F[] input, Transform<? super F, ? extends T> tf) {
		return transform(seq(input), tf);
	}

	public static <T> Sequence<T> filter(Iterable<? extends T> input, Filter<? super T> filter) {
		return new FilterSequence<T>(input, filter);
	}

	public static <T> Sequence<T> filter(Iterator<? extends T> input, Filter<? super T> filter) {
		return filter(seq(input), filter);
	}

	public static <T, P extends T> Sequence<T> filter(P[] input, Filter<? super T> filter) {
		return Sequences.<T> filter(seq(input), filter);
	}

	public static <T> Sequence<T> flatten(Iterable<? extends Iterable<? extends T>> sequences) {
		return new MultiSequence<T>(sequences);
	}

	public static <T, I extends Iterable<? extends T>> Sequence<T> flatten(I first, I second) {
		return new MultiSequence<T>(ImmutableList.of(first, second));
	}

	public static <T, I extends Iterable<? extends T>> Sequence<T> flatten(I first, I second, I third) {
		return new MultiSequence<T>(ImmutableList.of(first, second, third));
	}

	public static <T> Sequence<T> seq(T object) {
		return Sequence.singleton(object);
	}

	public static <T> Sequence<T> seq(T first, T second) {
		return new Sequence<T>(ImmutableList.of(first, second));
	}

	public static <T> Sequence<T> seq(T first, T second, T third) {
		return new Sequence<T>(ImmutableList.of(first, second, third));
	}

	@SafeVarargs
	public static <T> Sequence<T> seq(T... array) {
		return new Sequence<T>(Arrays.asList(array));
	}

	public static Sequence<Short> seq(short[] array) {
		return arraySeq(array);
	}

	public static Sequence<Integer> seq(int[] array) {
		return arraySeq(array);
	}

	public static Sequence<Long> seq(long[] array) {
		return arraySeq(array);
	}

	public static Sequence<Float> seq(float[] array) {
		return arraySeq(array);
	}

	public static Sequence<Double> seq(double[] array) {
		return arraySeq(array);
	}

	private static <T> Sequence<T> arraySeq(final Object array) {
		return new Sequence<T>(new Sequence.FinishableIterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new ArrayIterator<T>(array);
			}

			@Override
			public void finished() {
				// empty
			}
		}, Array.getLength(array));
	}

	@SuppressWarnings("unchecked")
	public static <T> Sequence<T> seq(Iterable<? extends T> iterable) {
		if (iterable instanceof Sequence) {
			return (Sequence<T>) iterable;
		}
		if (iterable instanceof Collection) {
			return Sequence.fromCollection((Collection<? extends T>) iterable);
		}
		return Sequence.fromIterable(iterable);
	}

	public static <T> Sequence<T> seq(Iterable<? extends T> iterable, int size) {
		return new Sequence<T>(iterable, size);
	}

	public static <T> Sequence<T> seq(Iterator<? extends T> iterator) {
		return Sequence.fromIterator(iterator);
	}

	public static <T> Sequence<T> seq(Iterator<? extends T> iterator, int size) {
		return new Sequence<T>(iterator, size);
	}

	public static <T> Sequence<T> seq(Collection<? extends T> collection) {
		return Sequence.fromCollection(collection);
	}

	public static <T> Sequence<T> construct(final int length, final Transform<?, T> constructor) {
		return Sequence.<T> fromIterator(new Iterator<T>() {
			private int index;

			@Override
			public boolean hasNext() {
				return index < length;
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				index++;
				return constructor.transform(null);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		});
	}

	/**
	 * Returns a sequence filled with consecutive natural numbers (from 0 to length - 1)
	 */
	public static Sequence<Integer> range(int length) {
		return range(0, length);
	}

	public static Sequence<Integer> range(final int start, final int endExcl) {
		return range(start, endExcl, 1);
	}

	public static Sequence<Integer> range(final int start, final int endExcl, final int step) {
		if (start >= endExcl) {
			return Sequence.emptySequence();
		}
		return new Sequence<Integer>(new Iterable<Integer>() {
			@Override
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					private int current = start;

					@Override
					public boolean hasNext() {
						return current < endExcl;
					}

					@Override
					public Integer next() {
						if (!hasNext()) {
							throw new NoSuchElementException();
						}
						final int last = current;
						current += step;
						return last;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		}, endExcl - start);
	}

	public static Sequence<Long> range(long endExcl) {
		return range(0, endExcl);
	}

	public static Sequence<Long> range(final long start, final long endExcl) {
		return range(start, endExcl, 1L);
	}

	public static Sequence<Long> range(final long start, final long endExcl, final long step) {
		if (start >= endExcl) {
			return Sequence.emptySequence();
		}
		long size = endExcl - start;
		return new Sequence<Long>(new Iterable<Long>() {
			@Override
			public Iterator<Long> iterator() {
				return new Iterator<Long>() {
					private long current = start;

					@Override
					public boolean hasNext() {
						return current < endExcl;
					}

					@Override
					public Long next() {
						if (!hasNext()) {
							throw new NoSuchElementException();
						}
						final long last = current;
						current += step;
						return last;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		}, size <= Integer.MAX_VALUE ? (int) size : -1);
	}
}
