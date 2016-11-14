package put.ci.cevo.framework.algorithms.history;

import com.google.common.collect.Lists;
import put.ci.cevo.framework.algorithms.history.policy.HistoryStoragePolicy;
import put.ci.cevo.framework.algorithms.history.policy.PersistAllPolicy;
import put.ci.cevo.framework.state.EvolutionState;

import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public class MemoryEvolutionHistory implements EvolutionHistory {

	private static final long serialVersionUID = 2619160830679322995L;

	private final HistoryStoragePolicy policy;
	private final List<EvolutionState> history;

	public MemoryEvolutionHistory() {
		this(new PersistAllPolicy());
	}

	public MemoryEvolutionHistory(HistoryStoragePolicy policy) {
		this.policy = policy;
		this.history = Lists.newArrayList();
	}

	@Override
	public void onNextGeneration(EvolutionState state) {
		if (policy.qualifies(state)) {
			history.add(state);
		}
	}

	@Override
	public List<EvolutionState> getEvolutionHistory() {
		return history;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("size", getEvolutionHistory().size()).toString();
	}

}
