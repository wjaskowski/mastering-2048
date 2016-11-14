package put.ci.cevo.framework.algorithms.history.policy;

import put.ci.cevo.framework.state.EvolutionState;

public interface HistoryStoragePolicy {

	public boolean qualifies(EvolutionState state);

}
