package put.ci.cevo.framework.algorithms;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.MemoryEvolutionHistory;
import put.ci.cevo.framework.retrospection.EvolutionaryRetrospector;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

public class RetrospectiveAlgorithm {

	private final EvolutionHistory history;
	private final GenerationalOptimizationAlgorithm algorithm;

	@AccessedViaReflection
	public RetrospectiveAlgorithm(GenerationalOptimizationAlgorithm algorithm) {
		this(algorithm, new MemoryEvolutionHistory());
	}

	@AccessedViaReflection
	public RetrospectiveAlgorithm(GenerationalOptimizationAlgorithm algorithm, EvolutionHistory history) {
		this.algorithm = algorithm;
		this.history = history;
	}

	public Retrospector evolve(EvolutionTarget target, ThreadedContext context) {
		algorithm.addNextGenerationListener(history);
		algorithm.evolve(target, context);
		return new EvolutionaryRetrospector(history);
	}

	public EvolutionHistory getHistory() {
		return history;
	}

	public GenerationalOptimizationAlgorithm getAlgorithm() {
		return algorithm;
	}

	public static RetrospectiveAlgorithm wrap(GenerationalOptimizationAlgorithm algorithm) {
		return new RetrospectiveAlgorithm(algorithm, new MemoryEvolutionHistory());
	}

}
