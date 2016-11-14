package put.ci.cevo.experiments.reports;

import put.ci.cevo.framework.algorithms.OptimizationAlgorithm;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;

/**
 * Generic interface for reports. Any class designed to summarize {@link OptimizationAlgorithm} results should implement
 * it. Reports could be driven by events from {@link EvolutionStateListener}s or designed to support only specific
 * experiments such as {@link ConfiguredExperimentReport}.
 */
public interface Report {

	public void generate();

}
