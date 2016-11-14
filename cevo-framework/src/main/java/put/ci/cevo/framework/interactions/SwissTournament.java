package put.ci.cevo.framework.interactions;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.CompareToBuilder;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.*;

/**
 * Like Swiss-System Tournament (see Wikipedia), but players can play against each other multiple times
 */
public class SwissTournament<X> implements Tournament<X> {

	private final SwissStrategy strategy;

	private static class Job<X> {
		public final X solution;
		public final X test;

		public Job(X solution, X test) {
			this.solution = solution;
			this.test = test;
		}
	}

	private final InteractionDomain<X, X> domain;
	private final int rounds;

	public static enum SwissStrategy {
		SECONDARY_POINTS, WEIGHTED
	}

	/**
	 * @param rounds Number of rounds, in theory it should be at least greater than log_2(#players)
	 */
	@AccessedViaReflection
	public SwissTournament(InteractionDomain<X, X> domain, int rounds, SwissStrategy strategy) {
		Preconditions.checkArgument(0 < rounds);
		this.domain = domain;
		this.rounds = rounds;
		this.strategy = strategy;
	}

	@Override
	public EvaluatedPopulation<X> execute(List<X> solutions, ThreadedContext context) {
		Preconditions.checkArgument(solutions.size() % 2 == 0,
				"Sorry, too much thinking to make it work for odd number of players");
		Preconditions.checkArgument(rounds < solutions.size(),
				"Currently, I do not allow to players to play more than one time against each other, but this is doable");

		List<X> players = new ArrayList<>(solutions);

		final MatchTable<X> table = new MatchTable<>(players);

		final HashSet<Pair<X, X>> paired = new HashSet<>(rounds * players.size() * 4);

		for (int i = 0; i < rounds; ++i) {
			// Shuffle to make sort more non-deterministic
			RandomUtils.shuffle(players, context.getRandomForThread());

			// Sort: better scorers first
			Collections.sort(players, new Comparator<X>() {
				@Override
				public int compare(X o1, X o2) {
					if (strategy == SwissStrategy.SECONDARY_POINTS) {
						return -new CompareToBuilder().append(table.averageScoreFor(o1), table.averageScoreFor(o2))
													  .append(table.averageSecondaryScoreFor(o1),
															  table.averageSecondaryScoreFor(o2))
													  .toComparison();
					} else if (strategy == SwissStrategy.WEIGHTED) {
						return -new CompareToBuilder().append(table.weightedAverageScoreFor(o1),
								table.weightedAverageScoreFor(o2)).toComparison();
					}
					throw new RuntimeException();
				}
			});

			List<Job<X>> jobs = prepareJobs(context, players, paired);

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
		}
		//TODO: Actually, I have two things here: 1) the above code which creates the MatchTable and the below code which produces EvaluatedPopulation
		//by some kind of aggregation. I should split them in a similar manner as it was done in InteractionTable and FitnessAggregate
		List<EvaluatedIndividual<X>> evaluated = new ArrayList<>(players.size());
		for (X player : players) {
			// I also count how good the players I played against were
			double fitness = 0;
			if (strategy == SwissStrategy.WEIGHTED) {
				fitness = table.weightedAverageScoreFor(player);
			} else if (strategy == SwissStrategy.SECONDARY_POINTS) {
				fitness = table.averageScoreFor(player) + 0.0001 * table.averageSecondaryScoreFor(player);
			}
			evaluated.add(new EvaluatedIndividual<>(player, fitness));
		}
		return new EvaluatedPopulation<>(evaluated, table.getTotalEffort());
	}

	public List<Job<X>> prepareJobs(ThreadedContext context, List<X> players, HashSet<Pair<X, X>> paired) {
		int n = players.size();

		boolean[] used = new boolean[n];

		IntOpenHashSet notused = new IntOpenHashSet(n);
		for (int j = 0; j < n; j += 1) {
			notused.add(j);
		}

		// Pair players in this round
		List<Job<X>> jobs = new ArrayList<>();
		for (int j = 0; j < n; j += 1) {
			if (used[j]) {
				continue;
			}
			used[j] = true;
			notused.remove(j);

			// Find a pair
			int k = j + 1;
			for (; k < n; ++k) {
				if (!used[k] && !paired.contains(new Pair<>(players.get(j), players.get(k)))) {
					break;
				}
			}
			if (k == n) {
				// I have found no way to play against an opponent that I had not played before
				k = RandomUtils.pickRandom(notused.toArray(), context.getRandomForThread());
			}

			jobs.add(new Job<>(players.get(j), players.get(k)));
			paired.add(new Pair<>(players.get(j), players.get(k)));
			paired.add(new Pair<>(players.get(k), players.get(j)));
			used[k] = true;
			notused.remove(k);
		}
		return jobs;
	}
}
