package put.ci.cevo.framework.algorithms.archives;

import java.util.List;

public interface Archive<T> {

	public void submit(T individual);
	public void submit(Iterable<T> individual);

	public List<T> getArchived();
	public int size();
}
