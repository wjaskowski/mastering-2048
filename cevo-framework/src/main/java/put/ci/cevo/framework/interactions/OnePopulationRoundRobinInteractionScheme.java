package put.ci.cevo.framework.interactions;

import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.algorithms.common.EffortTable.EffortTableBuilder;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.List;
import java.util.Map;

import static put.ci.cevo.util.sequence.Sequences.seq;

/** @deprecated Use {@link OnePopulationRoundRobinAlternativeInteractionScheme}(SelfPlay.WITH_SELF_PLAY) instead */
@Deprecated
public class OnePopulationRoundRobinInteractionScheme<S> implements InteractionScheme<S, S> {

	private final InteractionDomain<S, S> domain;

	@AccessedViaReflection
	public OnePopulationRoundRobinInteractionScheme(InteractionDomain<S, S> domain) {
		this.domain = domain;
	}

	@Override
	public InteractionTable<S, S> interact(final List<S> solutions, final List<S> tests, ThreadedContext context) {
		final Map<S, List<S>> partitioned = seq(solutions).keysToMap(new Transform<S, List<S>>() {
			@Override
			public List<S> transform(S solution) {
				return tests.subList(solutions.indexOf(solution), solutions.size());
			}
		});

		final PayoffTableBuilder<S, S> payoffTable = PayoffTable.create(solutions, tests);
		final EffortTableBuilder<S, S> effortTable = EffortTable.create(solutions, tests);
		context.submit(new Worker<S, Void>() {
			@Override
			public Void process(S solution, ThreadedContext context) {
				for (S test : partitioned.get(solution)) {
					InteractionResult result = domain.interact(solution, test, context.getRandomForThread());
					payoffTable.put(solution, test, result.firstResult());
					payoffTable.put(test, solution, result.secondResult());
					effortTable.put(solution, test, result.getEffort());
				}
				return null;
			}
		}, solutions);
		PayoffTable<S, S> table = payoffTable.build();
		return new InteractionTable<S, S>(table, table, effortTable.build());
	}
}
