package put.ci.cevo.framework.interactions;

import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.algorithms.common.EffortTable.EffortTableBuilder;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;

import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public final class RoundRobinInteractionScheme<S, T> implements InteractionScheme<S, T> {

	protected final InteractionDomain<S, T> domain;

	@AccessedViaReflection
	public RoundRobinInteractionScheme(InteractionDomain<S, T> domain) {
		this.domain = domain;
	}

	@Override
	public InteractionTable<S, T> interact(final List<S> solutions, final List<T> tests, ThreadedContext context) {
		final PayoffTableBuilder<S, T> solutionsPayoffTable = PayoffTable.create(solutions, tests);
		final PayoffTableBuilder<T, S> testsPayoffTable = PayoffTable.create(tests, solutions);
		final EffortTableBuilder<S, T> effortTable = EffortTable.create(solutions, tests);

		context.submit(new Worker<S, Void>() {
			@Override
			public Void process(S solution, ThreadedContext context) {
				for (T test : tests) {
					InteractionResult result = domain.interact(solution, test, context.getRandomForThread());
					solutionsPayoffTable.put(solution, test, result.firstResult());
					testsPayoffTable.put(test, solution, result.secondResult());
					effortTable.put(solution, test, result.getEffort());
				}
				return null;
			}
		}, solutions);

		return new InteractionTable<S, T>(solutionsPayoffTable.build(), testsPayoffTable.build(), effortTable.build());
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("interaction", domain).toString();
	}
}
