package put.ci.cevo.framework.evaluators;

import put.ci.cevo.util.Pair;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

@FunctionalInterface
public interface TwoPopulationEvaluator<S, T> {

	public Pair<EvaluatedPopulation<S>, EvaluatedPopulation<T>> evaluate(List<S> solutions, List<T> tests,
			int generation, ThreadedContext context);

}
