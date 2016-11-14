package put.ci.cevo.framework.evaluators;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * Evaluates a single individual
 * TODO: I wonder why IndividualEvaluator does not simply return Fitness
 */
public interface IndividualEvaluator<S> {

	public EvaluatedIndividual<S> evaluate(S individual, ThreadedContext context);
}
