package put.ci.cevo.framework.fitness;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

public interface FitnessAggregate {

	//TODO: Should return List instead of Map (or maybe EvaluatedPopulation?)
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context);
	
}
