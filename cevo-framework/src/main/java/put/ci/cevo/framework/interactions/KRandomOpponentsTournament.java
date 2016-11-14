package put.ci.cevo.framework.interactions;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Just a wrapper around KRandomOpponentsMatch<X>
 * TODO: What I need to do:
 * 	1. Remove all *Tournament classes.
 * 	2. Make them implement Match interface
 * 	3. Rename Match to Tournament
 * 	4. Add Aggregator interface which produces EvaluatedPopulation from Match
 * 	5. TournamentCoevolutionEvaluator should apply aggregator to the resulting MatchTable
 */
public class KRandomOpponentsTournament<X> implements Tournament<X> {

	private final KRandomOpponentsMatch<X> match;

	@AccessedViaReflection
	public KRandomOpponentsTournament(InteractionDomain<X, X> domain, int numOpponents,
			KRandomOpponentsMatch.OpponentsStrategy strategy) {
		Preconditions.checkArgument(0 < numOpponents);
		this.match = new KRandomOpponentsMatch<X>(domain, numOpponents, strategy);
	}

	@Override
	public EvaluatedPopulation<X> execute(List<X> solutions, ThreadedContext context) {
		Preconditions.checkArgument(solutions.size() % 2 == 0,
				"Sorry, did not make it work for an odd number of players");

		MatchTable<X> table = match.execute(solutions, context);

		List<EvaluatedIndividual<X>> evaluated = new ArrayList<>(solutions.size());
		for (X player : solutions) {
			evaluated.add(new EvaluatedIndividual<>(player, table.averageScoreFor(player)));
		}
		return new EvaluatedPopulation<>(evaluated, table.getTotalEffort());
	}
}
