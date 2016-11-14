package put.ci.cevo.framework.algorithms;

import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.random.ThreadedContext;

public interface OptimizationAlgorithm {

	public void evolve(EvolutionTarget target, ThreadedContext context);

}
