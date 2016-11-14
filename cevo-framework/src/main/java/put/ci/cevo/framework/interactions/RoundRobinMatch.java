package put.ci.cevo.framework.interactions;

import java.util.ArrayList;
import java.util.List;

import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

/** For playing a round robin tournament between players */
public class RoundRobinMatch<X> implements Match<X> {

	private static class Job<X> {
		public final X solution;
		public final X test;

		public Job(X solution, X test) {
			this.solution = solution;
			this.test = test;
		}
	}

	private final InteractionDomain<X, X> domain;
	private final boolean isSymmetricDomain;

	public RoundRobinMatch(InteractionDomain<X, X> domain) {
		this(domain, true);
	}

	/**
	 * @param isSymmetricDomain DoubleOthello() is symmetric while neither are DoubleOthello(0, 0.1) or Othello()
	 */
	@AccessedViaReflection
	public RoundRobinMatch(InteractionDomain<X, X> domain, boolean isSymmetricDomain) {
		this.domain = domain;
		this.isSymmetricDomain = isSymmetricDomain;
	}

	@Override
	/** Execute a round robin tournament among a list of players */
	public MatchTable<X> execute(List<X> players, ThreadedContext context) {
		final MatchTable<X> table = new MatchTable<>(players);

		List<Job<X>> jobs = prepareJobs(players);

		// Make interactions
		List<InteractionResult> results = context.invoke(
				new ThreadedContext.Worker<Job<X>, InteractionResult>() {
					@Override
					public InteractionResult process(Job<X> job, ThreadedContext context) {
						return domain.interact(job.solution, job.test, context.getRandomForThread());
					}
				}, jobs).toList();

		// Update table basing on interaction results
		for (int j = 0; j < jobs.size(); ++j) {
			Job<X> job = jobs.get(j);
			table.addResult(job.solution, job.test, results.get(j), isSymmetricDomain);
		}

		return table;
	}

	private List<Job<X>> prepareJobs(List<X> players) {
		List<Job<X>> jobs = new ArrayList<>(players.size());

		for (int i = 0; i < players.size(); ++i) {
			for (int j = 0; j < i; ++j) {
				jobs.add(new Job<>(players.get(i), players.get(j)));
			}
			if (isSymmetricDomain) {
				continue;
			}
			for (int j = i + 1; j < players.size(); ++j) {
				jobs.add(new Job<>(players.get(i), players.get(j)));
			}
		}
		return jobs;
	}
}
