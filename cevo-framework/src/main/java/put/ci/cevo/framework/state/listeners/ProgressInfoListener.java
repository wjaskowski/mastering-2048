package put.ci.cevo.framework.state.listeners;

import put.ci.cevo.framework.state.EvolutionState;

public class ProgressInfoListener implements EvolutionStateListener {

	@Override
	public void onNextGeneration(EvolutionState state) {
		state.getTarget().getProgressInfo().processed(state);
		if (state.getTarget().isAchieved(state)) {
			state.getTarget().getProgressInfo().finished();
		}
	}

}
