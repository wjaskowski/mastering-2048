package put.ci.cevo.util.sequence.events;

import java.util.EventListener;

public interface SequenceListener<T> extends EventListener {

	public void onBeforeNext();

	public void onNext(T elem);

	public void onFinished();

}
