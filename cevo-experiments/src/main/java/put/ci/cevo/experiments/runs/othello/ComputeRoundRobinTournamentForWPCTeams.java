package put.ci.cevo.experiments.runs.othello;

import static put.ci.cevo.profiles.experiments.PolarisPathProvider.getPolarisPath;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.tournament.PlayersTeam;
import put.ci.cevo.experiments.tournament.TeamsRoundRobinTournament;
import put.ci.cevo.framework.individuals.loaders.EvaluatedIndividualLoader;
import put.ci.cevo.framework.individuals.loaders.DefaultIndividualsLoader;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

/** Computes the performance of the standard WPC heuristic for Othello */
public class ComputeRoundRobinTournamentForWPCTeams {

	public static void main(String[] args) {
		// My players:
		// @formatter:off
		List<PlayersTeam<WPC>> teams = Arrays.asList(
			//new NamedTeam<>("swh", new OthelloStandardWPCHeuristic().create()),
			//new NamedTeam<>("luc06", new LucasRunnarson2006().create()),
			//new NamedTeam<>("szu09", new SzubertJaskowskiKrawiec2009().create()),
			//new NamedTeam<>("szu11", new SzubertJaskowskiKrawiec2011().create()),
			//new NamedTeam<>("vs_swh_1", evaluatedFromFile(new File(getPolarisPath(), "wj/best_swh_wpc/best_wpc_10857.dump"))),
			//new NamedTeam<>("vs_meu_1", evaluatedFromFile(new File(getPolarisPath(), "wj/best_meu_wpc/best_wpc_6842.dump"))),
			new PlayersTeam<WPC>("2-coev", DefaultIndividualsLoader.<WPC> create().loadIndividuals(new File(getPolarisPath(),
					"ieeetec/players/othello/2cel"), "run-*")),
			new PlayersTeam<WPC>("1-coev", new DefaultIndividualsLoader<WPC>(EvaluatedIndividualLoader.<WPC>individualLoader()).loadIndividuals(new File(getPolarisPath(),
					"ieeetec/players/othello/cel"), "run-*")),
			new PlayersTeam<WPC>("2-coev-rs", new DefaultIndividualsLoader<WPC>(EvaluatedIndividualLoader.<WPC>individualLoader()).loadIndividuals(new File(getPolarisPath(),
					"ieeetec/players/othello/2cel-rs"), "run-*")),
			new PlayersTeam<WPC>("1-coev-rs", new DefaultIndividualsLoader<WPC>(EvaluatedIndividualLoader.<WPC>individualLoader()).loadIndividuals(new File(getPolarisPath(),
					"ieeetec/players/othello/cel-rs"), "run-*")),
			new PlayersTeam<WPC>("evol-rs", new DefaultIndividualsLoader<WPC>(EvaluatedIndividualLoader.<WPC>individualLoader()).loadIndividuals(new File(getPolarisPath(),
					"ieeetec/players/othello/rsel"), "run-*")));
//			new NamedTeam<WPC>("cel-rs", new DefaultIndividualsLoader<WPC>(EvaluatedIndividualLoader.<WPC>individualLoader()).loadIndividuals(new File(getPolarisPath(),
//					"evostar2013/CEL-RS/optimize-mut/celrs-50-25-100-160-mucomma-gauss-1.0-1.0"), "run-*")),
//			new NamedTeam<WPC>("tdl", new StringWPCLoader().loadIndividuals(new File(PolarisPathProvider.getPolarisPath(),
//					"evostar2013/TDL/alpha_0.05_eps_0.1_2"), "*.ind")));
		// @formatter:on

		TeamsRoundRobinTournament<WPC, WPC> roundRobin = new TeamsRoundRobinTournament<>(
			new OthelloWPCInteraction(true), true);

		ThreadedContext context = new ThreadedContext(123, 8);
		System.out.println(roundRobin.performTournament(teams, teams, context).getTextTable(context));
	}

	private static WPC evaluatedFromFile(File file) {
		EvaluatedIndividual<WPC> evaluated = SerializationManagerFactory.create().deserializeWrapExceptions(file);
		return evaluated.getIndividual();
	}
}
