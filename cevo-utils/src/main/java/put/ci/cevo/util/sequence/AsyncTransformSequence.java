package put.ci.cevo.util.sequence;

import java.util.Iterator;

public abstract class AsyncTransformSequence<F, T> extends AsyncSequence<T> {

	private Iterator<? extends F> orignalIterator;

	public AsyncTransformSequence(Iterable<? extends F> iterable) {
		orignalIterator = iterable.iterator();
	}

	@Override
	protected void getNext() {
		if (orignalIterator.hasNext()) {
			getNext(orignalIterator.next());
		} else {
			finish();
		}
	}

	protected abstract void getNext(F input);

	public static <F, I extends Iterable<? extends F>> AsyncTransformSequence<I, F> flatten(
			Iterable<? extends I> nestedElements) {
		return new AsyncTransformSequence<I, F>(nestedElements) {
			@Override
			protected void getNext(I elems) {
				next(elems);
			}
		};
	}

}
