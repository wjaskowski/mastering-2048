package put.ci.cevo.framework.algorithms.history;

import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;

import java.util.List;

import static java.util.Collections.singletonList;

public class LastGenerationEvolutionHistory implements EvolutionHistory, LastGenerationListener {

	private static final long serialVersionUID = -527130883525086012L;

	private EvolutionState lastGenerationState;

	@Override
	public void onNextGeneration(EvolutionState state) {
		onLastGeneration(state);
	}

	@Override
	public void onLastGeneration(EvolutionState state) {
		if (state.getTarget().isAchieved(state)) {
			lastGenerationState = state;
		}
	}

	@Override
	public List<EvolutionState> getEvolutionHistory() {
		return singletonList(lastGenerationState);
	}

}
