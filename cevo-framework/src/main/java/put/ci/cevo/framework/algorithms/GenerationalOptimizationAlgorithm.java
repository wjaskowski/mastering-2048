package put.ci.cevo.framework.algorithms;

import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;

public interface GenerationalOptimizationAlgorithm extends OptimizationAlgorithm {

	public void addNextGenerationListener(EvolutionStateListener listener);

	public void removeNextGenerationListener(EvolutionStateListener listener);

	public void addLastGenerationListener(LastGenerationListener listener);

	public void removeLastGenerationListener(LastGenerationListener listener);

}
