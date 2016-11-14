package put.ci.cevo.util.sequence.events;

import static put.ci.cevo.util.ReflectionUtils.invokeConstructor;
import static put.ci.cevo.util.sequence.Sequence.UNKNOWN_SIZE;
import put.ci.cevo.util.info.ProgressInfo;
import put.ci.cevo.util.info.TextProgressInfo;
import put.ci.cevo.util.sequence.Sequence;

public class ProgressSequenceListener<T> implements SequenceListener<T> {

	private final ProgressInfo info;

	public ProgressSequenceListener(String processName, Iterable<?> sequence) {
		this(processName, Sequence.iterableSize(sequence), TextProgressInfo.class);
	}

	public ProgressSequenceListener(String processName, Iterable<?> sequence, Class<? extends ProgressInfo> clazz) {
		this(processName, Sequence.iterableSize(sequence), clazz);
	}

	public ProgressSequenceListener(String processName, long toProcess, Class<? extends ProgressInfo> clazz) {
		this(invokeConstructor(clazz, toProcess == UNKNOWN_SIZE ? null : toProcess, processName));
	}

	public ProgressSequenceListener(ProgressInfo info) {
		this.info = info;
	}

	@Override
	public void onNext(T elem) {
		info.processed();
	}

	@Override
	public void onFinished() {
		info.finished();
	}

	@Override
	public void onBeforeNext() {
		// do nothing
	}

}
