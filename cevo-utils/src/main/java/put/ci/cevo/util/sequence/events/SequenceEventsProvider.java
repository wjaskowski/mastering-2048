package put.ci.cevo.util.sequence.events;

public interface SequenceEventsProvider<T> {

	public void addListener(SequenceListener<T> listener);

}
