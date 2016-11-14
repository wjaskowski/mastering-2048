package put.ci.cevo.experiments.dct.experiments;

import put.ci.cevo.experiments.Model;
import put.ci.cevo.experiments.dct.reports.CADensityHistogram;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.MemoryEvolutionHistory;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.ProgressInfoListener;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class DCTModel extends Model {

	private final CADensityHistogram histogram;

	@AccessedViaReflection
	public DCTModel(GenerationalOptimizationAlgorithm algorithm, String name) {
		this(algorithm, new MemoryEvolutionHistory(), name);
	}

	@AccessedViaReflection
	public DCTModel(GenerationalOptimizationAlgorithm algorithm, EvolutionHistory history, String name) {
		this(algorithm, history, new ProgressInfoListener(), name);
	}

	@AccessedViaReflection
	public DCTModel(GenerationalOptimizationAlgorithm algorithm, EvolutionHistory history,
			EvolutionStateListener listener, String name) {
		super(algorithm, history, listener, name);
		this.histogram = new CADensityHistogram();
	}

	@Override public Retrospector evolve(EvolutionTarget target, ThreadedRandom random, int threads) {
		addListener(histogram);
		return super.evolve(target, random, threads);
	}

	public CADensityHistogram getHistogram() {
		return histogram;
	}
}
