package put.ci.cevo.framework.algorithms.archives;

import com.google.common.collect.Lists;

import java.util.List;

public class SimpleArchive<S> implements Archive<S> {

	private final List<S> archive = Lists.newArrayList();

	@Override
	public void submit(S individual) {
		archive.add(individual);
	}

	@Override
	public void submit(Iterable<S> individuals) {
		for (S individual : individuals) {
			submit(individual);
		}
	}

	@Override
	public List<S> getArchived() {
		return archive;
	}

	@Override
	public int size() {
		return archive.size();
	}

}
