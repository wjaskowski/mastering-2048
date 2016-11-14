package put.ci.cevo.framework.retrospection.queries;

import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.framework.algorithms.multiobjective.nsga2.NSGA2Fitness;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.filter.AbstractFilter;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.Sequences;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParetoFrontQuery<T> implements EvolutionQuery<T> {

	@Override
	public Sequence<EvaluatedIndividual<T>> perform(EvolutionHistory history) {
		EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history);
		Map<Integer, List<EvaluatedIndividual<T>>> solutions = processor.getSolutionsHistory();
		
		List<EvaluatedIndividual<T>> pop = solutions.get(Collections.max(solutions.keySet()));
		
		return Sequences.seq(pop).select(new AbstractFilter<EvaluatedIndividual<T>>() {
			@Override
			public boolean qualifies(EvaluatedIndividual<T> ind) {
				return ind.fitness(NSGA2Fitness.class).rank == 1;
			}
		});
	}

}
