package put.ci.cevo.framework.interactions;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.algorithms.common.EffortTable.EffortTableBuilder;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A version without:
 * 	- playing self-games
 * 	- non-readable complex sequences to maps conversion
 * 	TODO: Change name to without Alternative
 * 	@param <X> Represents a entity that can interact (its a phenotype)
 */
public class OnePopulationRoundRobinAlternativeInteractionScheme<X> implements InteractionScheme<X, X> {

	private static class Job<S> {
		public final S solution;
		public final S test;

		public Job(S solution, S test) {
			this.solution = solution;
			this.test = test;
		}
	}

	public static enum SelfPlay {
		WITH_SELF_PLAY,
		WITHOUT_SELF_PLAY
	}

	private final InteractionDomain<X, X> domain;
	private final SelfPlay selfPlay;

	@AccessedViaReflection
	public OnePopulationRoundRobinAlternativeInteractionScheme(InteractionDomain<X, X> domain) {
		this(domain, SelfPlay.WITHOUT_SELF_PLAY);
	}

	/**
	 * @param selfPlay whether to play games on the diagonal - of type "X against X" (generally this makes no sense)
	 */
	@AccessedViaReflection
	public OnePopulationRoundRobinAlternativeInteractionScheme(InteractionDomain<X, X> domain, SelfPlay selfPlay) {
		this.domain = domain;
		this.selfPlay = selfPlay;
	}

	@Override
	public InteractionTable<X, X> interact(final List<X> solutions, final List<X> tests, ThreadedContext context) {
		Preconditions.checkArgument(solutions == tests, "This is only for one population coevolution");

		List<Job<X>> jobs = new ArrayList<>();
		for (int i = 0; i < solutions.size(); ++i) {
			for (int j = i + 1; j < solutions.size(); ++j)
				jobs.add(new Job<>(solutions.get(i), solutions.get(j)));
			if (selfPlay == SelfPlay.WITH_SELF_PLAY) {
				jobs.add(new Job<>(solutions.get(i), solutions.get(i)));
			}
		}

		final PayoffTableBuilder<X, X> payoffTable = PayoffTable.create(solutions, tests);
		final EffortTableBuilder<X, X> effortTable = EffortTable.create(solutions, tests);

		context.submit((job, cntx) -> {
			InteractionResult result = domain.interact(job.solution, job.test, cntx.getRandomForThread());
			payoffTable.put(job.solution, job.test, result.firstResult());
			payoffTable.put(job.test, job.solution, result.secondResult());
			effortTable.put(job.solution, job.test, result.getEffort());
			//TODO: Actually, this is a bit converoversial that only solution gets the effort.
			//This shows that we actually do not need effort tables. We need only total effort
			return null;
		}, jobs);

		PayoffTable<X, X> table = payoffTable.build();
		return new InteractionTable<>(table, table, effortTable.build());
	}
}
