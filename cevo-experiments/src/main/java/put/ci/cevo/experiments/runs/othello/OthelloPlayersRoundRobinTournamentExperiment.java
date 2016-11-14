package put.ci.cevo.experiments.runs.othello;

import org.apache.commons.math3.util.Pair;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.othello.AllStatesInteractionDomain;
import put.ci.cevo.experiments.othello.OthelloInteractionDomain;
import put.ci.cevo.experiments.othello.RepeatedInteractionDomain;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.MatchTable;
import put.ci.cevo.framework.interactions.RoundRobinMatch;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.LucasInitialOthelloStates;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.published.*;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class OthelloPlayersRoundRobinTournamentExperiment implements Experiment {
	private static Logger logger = Logger.getLogger(OthelloPlayersRoundRobinTournamentExperiment.class);

	private static final Configuration config = Configuration.getConfiguration();

	private enum GamesScheme {
		LUCAS_BOARDS, OPPONENTS_EPS
	}

	private static final GamesScheme GAMES_SCHEME = config.getEnumValue(GamesScheme.class, new ConfKey("games_scheme"),
			GamesScheme.LUCAS_BOARDS);

	private static InteractionDomain<OthelloPlayer, OthelloPlayer> DOMAIN;
	private static boolean IS_SYMMETRIC_DOMAIN;

	static {
		// Deterministic, on 1000 boards
		if (GAMES_SCHEME == GamesScheme.LUCAS_BOARDS) {
			DOMAIN = new AllStatesInteractionDomain<>(new DoubleOthello(), new LucasInitialOthelloStates(false)
					.boards());
			IS_SYMMETRIC_DOMAIN = true;
		// Opponents random moves with eps=0.1
		} else if (GAMES_SCHEME == GamesScheme.OPPONENTS_EPS) {
			DOMAIN = new RepeatedInteractionDomain<>(new OthelloInteractionDomain(new DoubleOthello(0.0, 0.1,
					Integer.MAX_VALUE)), 1000);
			IS_SYMMETRIC_DOMAIN = false;
		}
	}

	@Override
	public void run(String[] args) {
		ArrayList<Pair<String, OthelloPlayer>> players = new ArrayList<>();
		players.add(new Pair<>("SWH", new OthelloStandardWPCHeuristicPlayer().create()));
		players.add(new Pair<>("LR06", new LucasRunnarson2006Player().create()));
		players.add(new Pair<>("SJK09", new SzubertJaskowskiKrawiec2009Player().create()));
		players.add(new Pair<>("SJK11", new SzubertJaskowskiKrawiec2011Player().create()));
		players.add(new Pair<>("SJK13_CTDL",
				new SzubertJaskowskiKrawiec2013CTDLPlayer().create()));
		players.add(new Pair<>("SJK13_ETDL",
				new SzubertJaskowskiKrawiec2013ETDLPlayer().create()));
		players.add(new Pair<>("PB11_ETDL", new Burrow2011ETDLPlayer().create()));
		players.add(new Pair<>("EM10_Nash70", new Manning2010Nash70Player().create()));
		players.add(new Pair<>("EM10_TCIAG2", new Manning2010TCIAG2Player().create()));
		players.add(new Pair<>("EM10_TCIAG1", new Manning2010TCIAG1Player().create()));
		players.add(new Pair<>("EM10_GECCO", new Manning2010GECCOPlayer().create()));
		players.add(new Pair<>("RL14_iPref1", new RunnarsonLucas2014IPref1Player().create()));
		players.add(new Pair<>("RL14_iPrefN", new RunnarsonLucas2014IPrefNPlayer().create()));

		players.add(new Pair<>("CMAES-4-2x2", new JaskowskiSzubert2015CoCMAES4_2x2Player().create()));
		//players.add(new Pair<>("CMAES-4", new JaskowskiSzubert2015CoCMAES4Player().create()));
		//players.add(new Pair<>("CMAES-3", new JaskowskiSzubert2015CoCMAES3Player().create()));
		//players.add(new Pair<>("CMAES-2", new JaskowskiSzubert2015CoCMAES2Player().create()));

		//players.add(new Pair<>("WJ_All2", new Jaskowski2014All2Player().create()));
		//players.add(new Pair<>("WJ_All123", new Jaskowski2014All123Player().create()));

		roundRobinTournament(players, new ThreadedContext(123));

	}

	public static void main(String[] args) {
		new OthelloPlayersRoundRobinTournamentExperiment().run(args);
	}

	private static void roundRobinTournament(ArrayList<Pair<String, OthelloPlayer>> players, ThreadedContext context) {

		List<OthelloPlayer> onlyPlayers = new ArrayList<>(players.size());
		for (Pair<String, OthelloPlayer> player : players) {
			onlyPlayers.add(player.getSecond());
		}

		MatchTable<OthelloPlayer> table = new RoundRobinMatch<>(DOMAIN, IS_SYMMETRIC_DOMAIN).execute(onlyPlayers,
				context);

		IdentityHashMap<OthelloPlayer, String> names = new IdentityHashMap<>();
		for (Pair<String, OthelloPlayer> player : players) {
			names.put(player.getSecond(), player.getFirst());
		}
		System.out.println(table.toString(names));
	}
}
