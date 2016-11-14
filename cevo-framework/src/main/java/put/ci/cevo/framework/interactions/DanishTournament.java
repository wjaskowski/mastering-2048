package put.ci.cevo.framework.interactions;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.CompareToBuilder;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Like Swiss-System Tournament (see Wikipedia), but players can play against each other multiple times
 */
public class DanishTournament<X> implements Tournament<X> {

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

	/**
	 * @param rounds Number of rounds, in theory it should be at least greater than log_2(#players)
	 */
	@AccessedViaReflection
	public DanishTournament(InteractionDomain<X, X> domain, int rounds) {
		Preconditions.checkArgument(0 < rounds);
		this.domain = domain;
		this.rounds = rounds;
	}

	@Override
	public EvaluatedPopulation<X> execute(List<X> solutions, ThreadedContext context) {
		Preconditions.checkArgument(solutions.size() % 2 == 0,
				"Sorry, too much thinking to make it work for odd number of players");

		List<X> players = new ArrayList<>(solutions);

		final MatchTable<X> scores = new MatchTable<>(players);

		for (int i = 0; i < rounds; ++i) {
			// Shuffle to make sort more non-deterministic
			RandomUtils.shuffle(players, context.getRandomForThread());

			// Sort: better scorers first
			Collections.sort(players, new Comparator<X>() {
				@Override
				public int compare(X o1, X o2) {
					return -new CompareToBuilder().append(scores.averageScoreFor(o1), scores.averageScoreFor(
							o2)).toComparison();
				}
			});

			// Pair neighboring players
			List<Job<X>> jobs = new ArrayList<>();
			for (int j = 0; j < players.size(); j += 2) {
				jobs.add(new Job<>(players.get(j), players.get(j+1)));
			}

			// Make interactions
			List<InteractionResult> results = context.invoke(
					new ThreadedContext.Worker<Job<X>, InteractionResult>() {
						@Override
						public InteractionResult process(Job<X> job, ThreadedContext context) {
							return domain.interact(job.solution, job.test, context.getRandomForThread());
						}
					}, jobs).toList();

			// Update scores basing on interaction results
			for (int j = 0; j < jobs.size(); ++j) {
				Job<X> job = jobs.get(j);
				InteractionResult result = results.get(j);

				scores.addSymmetricResult(job.solution, job.test, result);
			}
		}
		List<EvaluatedIndividual<X>> evaluated = new ArrayList<>(players.size());
		for (X player : players) {
			evaluated.add(new EvaluatedIndividual<>(player, scores.averageScoreFor(player)));
		}
		return new EvaluatedPopulation<>(evaluated, scores.getTotalEffort());
	}
}
