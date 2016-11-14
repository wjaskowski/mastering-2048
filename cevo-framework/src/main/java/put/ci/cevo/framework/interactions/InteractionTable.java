package put.ci.cevo.framework.interactions;

import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable;

public class InteractionTable<S, T> {

	private final PayoffTable<S, T> solutionsPayoffs;
	private final PayoffTable<T, S> testsPayoffs;
	private final EffortTable<S, T> efforts;

	public InteractionTable(PayoffTable<S, T> solutionsPayoffs, PayoffTable<T, S> testsPayoffs,
			EffortTable<S, T> efforts) {
		this.solutionsPayoffs = solutionsPayoffs;
		this.testsPayoffs = testsPayoffs;
		this.efforts = efforts;
	}

	public PayoffTable<S, T> getSolutionsPayoffs() {
		return solutionsPayoffs;
	}

	public PayoffTable<T, S> getTestsPayoffs() {
		return testsPayoffs;
	}

	public EffortTable<S, T> getEfforts() {
		return efforts;
	}
}
