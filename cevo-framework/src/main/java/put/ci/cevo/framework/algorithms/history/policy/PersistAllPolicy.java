package put.ci.cevo.framework.algorithms.history.policy;

import put.ci.cevo.framework.state.EvolutionState;

public class PersistAllPolicy implements HistoryStoragePolicy {

	@Override
	public boolean qualifies(EvolutionState state) {
		return true;
	}

}
