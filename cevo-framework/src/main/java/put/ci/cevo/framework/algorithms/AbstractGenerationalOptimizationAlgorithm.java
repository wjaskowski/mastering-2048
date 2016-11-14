package put.ci.cevo.framework.algorithms;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;

import java.util.List;

public abstract class AbstractGenerationalOptimizationAlgorithm implements GenerationalOptimizationAlgorithm {

	private final List<EvolutionStateListener> nextGenerationlisteners;
	private final List<LastGenerationListener> lastGenerationlisteners;

	public AbstractGenerationalOptimizationAlgorithm() {
		this.nextGenerationlisteners = Lists.newArrayList();
		this.lastGenerationlisteners = Lists.newArrayList();
	}

	@Override
	public final void addNextGenerationListener(EvolutionStateListener listener) {
		Preconditions.checkNotNull(listener);
		nextGenerationlisteners.add(listener);
	}

	@Override
	public final void removeNextGenerationListener(EvolutionStateListener listener) {
		nextGenerationlisteners.remove(listener);
	}

	@Override
	public final void addLastGenerationListener(LastGenerationListener listener) {
		Preconditions.checkNotNull(listener);
		lastGenerationlisteners.add(listener);
	}

	@Override
	public final void removeLastGenerationListener(LastGenerationListener listener) {
		lastGenerationlisteners.remove(listener);
	}

	protected final void fireNextGenerationEvent(EvolutionState state) {
		for (EvolutionStateListener listener : nextGenerationlisteners) {
			listener.onNextGeneration(state);
		}
	}

	protected final void fireLastGenerationEvent(EvolutionState state) {
		for (LastGenerationListener listener : lastGenerationlisteners) {
			listener.onLastGeneration(state);
		}
	}

}
