package put.ci.cevo.framework.state;

import com.google.common.collect.ImmutableList;
import put.ci.cevo.framework.termination.EmptyTarget;
import put.ci.cevo.framework.termination.EvolutionTarget;

import java.util.Collections;
import java.util.List;

public class OnePopulationEvolutionState<S> extends EvolutionState {

	private final ImmutableList<EvaluatedIndividual<S>> solutions;

	public OnePopulationEvolutionState(long elapsedTime, int generation, long totalEffort,
			List<EvaluatedIndividual<S>> solutions) {
		this(elapsedTime, generation, totalEffort, solutions, new EmptyTarget());
	}

	public OnePopulationEvolutionState(long elapsedTime, int generation, long totalEffort,
			List<EvaluatedIndividual<S>> solutions, EvolutionTarget target) {
		super(elapsedTime, generation, totalEffort, target);
		this.solutions = ImmutableList.copyOf(solutions);
	}

	@Override
	public List<EvaluatedIndividual<S>> getEvaluatedSolutions() {
		return solutions;
	}

	@Override
	public List<EvaluatedIndividual<S>> getEvaluatedTests() {
		return solutions;
	}

	public static <S> OnePopulationEvolutionState<S> initialEvolutionState() {
		// -1, so when we add 1, we get generation 0
		return new OnePopulationEvolutionState<>(0, -1, 0, Collections.<EvaluatedIndividual<S>> emptyList());
	}
}
