package put.ci.cevo.framework.state.listeners;

import put.ci.cevo.framework.state.EvolutionState;

public interface EvolutionStateListener {

	public void onNextGeneration(EvolutionState state);

}