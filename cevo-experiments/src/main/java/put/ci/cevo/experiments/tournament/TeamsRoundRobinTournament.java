package put.ci.cevo.experiments.tournament;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import put.ci.cevo.experiments.tournament.ResultsTable.ResultTableBuilder;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedContext.Worker;
import put.ci.cevo.util.Pair;

public class TeamsRoundRobinTournament<S, T> {

	private final InteractionDomain<S, T> domain;
	private final boolean domainIsSymmetric;

	// TODO: This should allow to make tournaments between players. Now we cannot play a tournament where we have mixed
	// players of WPC and NTuples

	/**
	 * @param domain
	 * @param domainIsSymmetric
	 *            if true for each pair (x,y) I play only one game G(x,y) instead of two: G(x,y) for (x,y) and G(y,x)
	 *            for (y,x)
	 */
	public TeamsRoundRobinTournament(InteractionDomain<S, T> domain, boolean domainIsSymmetric) {
		this.domain = domain;
		this.domainIsSymmetric = domainIsSymmetric;
	}

	public RoundRobinTournamentResults<S, T> performTournament(List<PlayersTeam<S>> teamsA,
			List<PlayersTeam<T>> teamsB, ThreadedContext context) {
		final int n = teamsA.size();
		final ResultTableBuilder<PlayersTeam<S>, PlayersTeam<T>> builder = ResultsTable.create(teamsA, teamsB);

		for (int i = 0; i < n; i++) {
			builder.put(teamsA.get(i), teamsB.get(i), new DescriptiveStatistics());
		}

		Pair<DescriptiveStatistics, DescriptiveStatistics> result;
		if (domainIsSymmetric) {
			// Compute only half of it
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					result = playMatch(teamsA.get(i).players, teamsB.get(j).players, context);
					builder.put(teamsA.get(i), teamsB.get(j), result.first());
					builder.put(teamsA.get(j), teamsB.get(i), result.second());
				}
			}
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i == j) {
						continue;
					}
					result = playMatch(teamsA.get(i).players, teamsB.get(j).players, context);
					builder.put(teamsA.get(i), teamsB.get(j), result.first());
				}
			}
		}
		return new RoundRobinTournamentResults<>(builder.build());
	}

	private Pair<DescriptiveStatistics, DescriptiveStatistics> playMatch(List<S> team1, List<T> team2,
			ThreadedContext context) {
		final DescriptiveStatistics firstTeamStats = new DescriptiveStatistics();
		final DescriptiveStatistics secondTeamStats = new DescriptiveStatistics();

		for (final S player1 : team1) {
			List<InteractionResult> results = context.invoke(new Worker<T, InteractionResult>() {
				@Override
				public InteractionResult process(T player2, ThreadedContext context) {
					return domain.interact(player1, player2, context.getRandomForThread());
				}
			}, team2).toList();

			for (InteractionResult result : results) {
				firstTeamStats.addValue(result.firstResult());
				secondTeamStats.addValue(result.secondResult());
			}
		}
		return Pair.create(firstTeamStats, secondTeamStats);
	}

}
