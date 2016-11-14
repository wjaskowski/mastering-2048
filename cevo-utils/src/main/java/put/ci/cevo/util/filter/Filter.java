package put.ci.cevo.util.filter;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections15.Predicate;

/**
 * The filter interface - basically a function from objects of some type to boolean space.
 * 
 */
public interface Filter<T> extends Predicate<T>, com.google.common.base.Predicate<T> {

	public boolean qualifies(T object);

	public Filter<T> not();

	public Filter<T> or(Filter<T> other);

	public Filter<T> and(Filter<T> other);

	public Filter<T> xor(Filter<T> other);

	public boolean any(Iterable<? extends T> objects);

	public boolean every(Iterable<? extends T> objects);

	public boolean none(Iterable<? extends T> objects);

	public T find(Iterable<? extends T> objects);

	public int apply(Iterable<? extends T> features);

	public int apply(Iterator<? extends T> featuresIterator);

	public Collection<T> select(Iterable<? extends T> features);

	public Collection<T> select(Iterator<? extends T> featuresIterator);

}
