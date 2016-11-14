package put.ci.cevo.framework.algorithms.archives;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Classical, standard Hall of Fame archive of a given maximum size
 */
public final class HallOfFameArchive<S> implements  Archive<S> {
	private Deque<S> archived;
	private final int maxSize;

	public HallOfFameArchive(int maxSize) {
		this.maxSize = maxSize;
		archived = new LinkedList<>();
	}

	@Override
	public void submit(S individual) {
		// Duplicates are not welcome
		if (contains(individual))
			return;

		while (archived.size() >= maxSize) {
			archived.removeFirst();
		}
		archived.addLast(individual);
	}

	@Override
	public void submit(Iterable<S> individuals) {
		for (S individual : individuals) {
			submit(individual);
		}
	}

	private boolean contains(S individual) {
		for (S ind : archived) {
			if (ind.equals(individual))
				return true;
		}
		return false;
	}

	@Override
	public List<S> getArchived() {
		return ImmutableList.copyOf(archived);
	}

	@Override
	public int size() {
		return getArchived().size();
	}
}
