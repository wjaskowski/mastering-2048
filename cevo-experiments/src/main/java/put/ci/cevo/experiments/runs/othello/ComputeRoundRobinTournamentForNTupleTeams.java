package put.ci.cevo.experiments.runs.othello;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.experiments.ntuple.OthelloNTuplesAllStraightFactory;
import put.ci.cevo.experiments.othello.OthelloNTuplesInteraction;
import put.ci.cevo.experiments.tournament.PlayersTeam;
import put.ci.cevo.experiments.tournament.TeamsRoundRobinTournament;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

/** Computes the performance of the standard WPC heuristic for Othello */
public class ComputeRoundRobinTournamentForNTupleTeams {

	public static void main(String[] args) {
		// My players:
		// @formatter:off
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		ArrayList<NTuples> rand = new ArrayList<>();
		for (int i=0;i<5;++i){
			rand.add(new OthelloNTuplesAllStraightFactory(2, -1.0, 1.0).createRandomIndividual(random));
			OthelloLeague.saveInOthelloLeagueFormat(rand.get(rand.size()-1), new File("rand" + i + ".txt"));
		}
		for (int i=5;i<10;++i){
			rand.add(new OthelloNTuplesAllStraightFactory(3, -1.0, 1.0).createRandomIndividual(random));
			OthelloLeague.saveInOthelloLeagueFormat(rand.get(rand.size()-1), new File("rand" + i + ".txt"));
		}
		List<PlayersTeam<NTuples>> teams = Arrays.asList(
			//new NamedTeam<>("swh", new OthelloStandardWPCHeuristic().create()),
			//new NamedTeam<>("luc06", new LucasRunnarson2006().create()),
			//new NamedTeam<>("szu09", new SzubertJaskowskiKrawiec2009().create()),
			//new NamedTeam<>("szu11", new SzubertJaskowskiKrawiec2011().create()),
			//new NamedTeam<>("vs_swh_1", evaluatedFromFile(new File(getPolarisPath(), "wj/best_swh_wpc/best_wpc_10857.dump"))),
			//new NamedTeam<>("vs_meu_1", evaluatedFromFile(new File(getPolarisPath(), "wj/best_meu_wpc/best_wpc_6842.dump"))),
			new PlayersTeam<>("rand0", rand.get(0)),
			new PlayersTeam<>("rand1", rand.get(1)),
			new PlayersTeam<>("rand2", rand.get(2)),
			new PlayersTeam<>("rand3", rand.get(3)),
			new PlayersTeam<>("rand4", rand.get(4)),
			new PlayersTeam<>("rand5", rand.get(5)),
			new PlayersTeam<>("rand6", rand.get(6)),
			new PlayersTeam<>("rand7", rand.get(7)),
			new PlayersTeam<>("rand8", rand.get(8)),
			new PlayersTeam<>("rand9", rand.get(9))
//			new NamedTeam<WPC>("cel-rs", new DefaultIndividualsLoader<WPC>(EvaluatedIndividualLoader.<WPC>individualLoader()).loadIndividuals(new File(getPolarisPath(),
//					"evostar2013/CEL-RS/optimize-mut/celrs-50-25-100-160-mucomma-gauss-1.0-1.0"), "run-*")),
//			new NamedTeam<WPC>("tdl", new StringWPCLoader().loadIndividuals(new File(PolarisPathProvider.getPolarisPath(),
//					"evostar2013/TDL/alpha_0.05_eps_0.1_2"), "*.ind")));
			);
		// @formatter:on

		TeamsRoundRobinTournament<NTuples, NTuples> roundRobin = new TeamsRoundRobinTournament<>(
			new OthelloNTuplesInteraction(false), false);

		ThreadedContext context = new ThreadedContext(123, 8);
		System.out.println(roundRobin.performTournament(teams, teams, context).getTextTable(context));
	}

	private static WPC evaluatedFromFile(File file) {
		EvaluatedIndividual<WPC> evaluated = SerializationManagerFactory.create().deserializeWrapExceptions(file);
		return evaluated.getIndividual();
	}
}
