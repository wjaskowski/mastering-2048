package put.ci.cevo.framework.evaluators;

import put.ci.cevo.util.random.ThreadedContext;
import java.util.List;

@FunctionalInterface
public interface PopulationEvaluator<S> {

	EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context);

}
