package put.ci.cevo.framework.state;

import com.google.common.collect.ImmutableList;
import put.ci.cevo.framework.termination.EmptyTarget;
import put.ci.cevo.framework.termination.EvolutionTarget;

import java.util.Collections;
import java.util.List;

public class TwoPopulationEvolutionState<S, T> extends EvolutionState {

	private final ImmutableList<EvaluatedIndividual<S>> solutions;
	private final ImmutableList<EvaluatedIndividual<T>> tests;

	public TwoPopulationEvolutionState(long elapsedTime, int generation, long totalEffort) {
		this(elapsedTime, generation, totalEffort, Collections.<EvaluatedIndividual<S>> emptyList(), Collections
			.<EvaluatedIndividual<T>> emptyList(), new EmptyTarget());
	}

	public TwoPopulationEvolutionState(long elapsedTime, int generation, long totalEffort,
			List<EvaluatedIndividual<S>> solutions, List<EvaluatedIndividual<T>> tests) {
		this(elapsedTime, generation, totalEffort, solutions, tests, new EmptyTarget());
	}

	public TwoPopulationEvolutionState(long elapsedTime, int generation, long totalEffort,
			List<EvaluatedIndividual<S>> solutions, List<EvaluatedIndividual<T>> tests, EvolutionTarget target) {
		super(elapsedTime, generation, totalEffort, target);
		this.solutions = ImmutableList.copyOf(solutions);
		this.tests = ImmutableList.copyOf(tests);
	}

	@Override
	public List<EvaluatedIndividual<S>> getEvaluatedSolutions() {
		return solutions;
	}

	@Override
	public List<EvaluatedIndividual<T>> getEvaluatedTests() {
		return tests;
	}

	public static <S, T> TwoPopulationEvolutionState<S, T> initialEvolutionState() {
		// -1, so when we add 1, we get generation 0
		return new TwoPopulationEvolutionState<>(0, -1, 0);
	}
}
