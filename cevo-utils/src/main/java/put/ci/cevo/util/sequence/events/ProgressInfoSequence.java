package put.ci.cevo.util.sequence.events;

import put.ci.cevo.util.info.ProgressInfo;
import put.ci.cevo.util.sequence.Sequence;

public class ProgressInfoSequence<T> extends OneTimeEventsSequence<T> {

	public ProgressInfoSequence(Iterable<? extends T> sequence, String processDescription) {
		super(sequence, new ProgressSequenceListener<T>(processDescription, sequence));
	}

	public ProgressInfoSequence(Iterable<? extends T> sequence, Class<? extends ProgressInfo> clazz, String description) {
		super(sequence, new ProgressSequenceListener<T>(description, sequence, clazz));
	}

	public static <T> Sequence<T> wrap(Iterable<T> sequence, String processDescription) {
		return new ProgressInfoSequence<T>(sequence, processDescription);
	}

}
