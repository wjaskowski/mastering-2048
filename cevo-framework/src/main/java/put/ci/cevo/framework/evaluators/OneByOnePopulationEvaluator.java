package put.ci.cevo.framework.evaluators;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

/**
 * Evaluates each individual one by one using {@link IndividualEvaluator}.
 */
public class OneByOnePopulationEvaluator<S> implements PopulationEvaluator<S> {

	private final IndividualEvaluator<S> individualEvaluator;

	public OneByOnePopulationEvaluator(IndividualEvaluator<S> individualEvaluator) {
		this.individualEvaluator = individualEvaluator;
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {
		List<EvaluatedIndividual<S>> evaluated = context.invoke(individualEvaluator::evaluate, population).toList();
		return new EvaluatedPopulation<>(evaluated);
	}
}
