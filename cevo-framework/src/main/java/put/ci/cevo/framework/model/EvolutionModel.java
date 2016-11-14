package put.ci.cevo.framework.model;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

public interface EvolutionModel<T> {

	public List<T> evolvePopulation(List<EvaluatedIndividual<T>> evaluatedPopulation, final ThreadedContext context);

}
