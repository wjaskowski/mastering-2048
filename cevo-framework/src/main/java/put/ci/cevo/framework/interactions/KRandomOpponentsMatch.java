package put.ci.cevo.framework.interactions;

import com.google.common.base.Preconditions;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;

public class KRandomOpponentsMatch<X> implements Match<X> {

	private final OpponentsStrategy strategy;

	private static class Job<X> {
		public final X solution;
		public final X test;

		public Job(X solution, X test) {
			this.solution = solution;
			this.test = test;
		}
	}

	private final InteractionDomain<X, X> domain;
	private final int numOpponents;

	public enum OpponentsStrategy {
		FIXED_OPPONENTS,
		RANDOM_OPPONENTS,
	}

	@AccessedViaReflection
	public KRandomOpponentsMatch(InteractionDomain<X, X> domain, int numOpponents, OpponentsStrategy strategy) {
		Preconditions.checkArgument(0 < numOpponents);
		this.domain = domain;
		this.numOpponents = numOpponents;
		this.strategy = strategy;
	}

	@Override
	public MatchTable<X> execute(List<X> solutions, ThreadedContext context) {
		Preconditions.checkArgument(solutions.size() % 2 == 0,
				"Sorry, did not make it work for an odd number of players");

		List<X> players = new ArrayList<>(solutions);

		final MatchTable<X> table = new MatchTable<>(players);

		List<Job<X>> jobs = prepareJobs(context, players);

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
			table.addSymmetricResult(job.solution, job.test, results.get(j));
		}

		return table;
	}

	private List<Job<X>> prepareJobs(ThreadedContext context, List<X> players) {
		List<Job<X>> jobs = new ArrayList<>(players.size());

		if (strategy == OpponentsStrategy.RANDOM_OPPONENTS) {
			for (int i = 0; i < numOpponents; ++i) {
				RandomUtils.shuffle(players, context.getRandomForThread());
				for (int j = 0; j < players.size(); j += 2) {
					jobs.add(new Job<>(players.get(j), players.get(j + 1)));
				}
			}
		} else if (strategy == OpponentsStrategy.FIXED_OPPONENTS) {
			List<X> opponents = RandomUtils.sampleMore(players, numOpponents, context.getRandomForThread());
			for (X player : players) {
				for (X opponent : opponents) {
					jobs.add(new Job<>(player, opponent));
				}
			}
		}
		return jobs;
	}

}
