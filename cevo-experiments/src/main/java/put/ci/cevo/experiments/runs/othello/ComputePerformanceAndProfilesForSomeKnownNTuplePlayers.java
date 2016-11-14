package put.ci.cevo.experiments.runs.othello;

import static put.ci.cevo.util.StatisticUtils.meanWithConfidenceIntervalPercent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import put.ci.cevo.experiments.othello.AgainstOthelloRandomPlayerPerformanceMeasure;
import put.ci.cevo.experiments.othello.OthelloInteractionDomain;
import put.ci.cevo.experiments.othello.OthelloLeaguePerformanceMeasure;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

/**
 * Computes the performance of the standard NTuple heuristic for Othello
 */
public class ComputePerformanceAndProfilesForSomeKnownNTuplePlayers {

	private static final int SAMPLE_SIZE = 100000;

	public static void main(String[] args) throws SerializationException, IOException {
		SerializationManager serialization = SerializationManagerFactory.create();
		ThreadedContext context = new ThreadedContext(123);

		OthelloInteractionDomain othello = new OthelloInteractionDomain(new DoubleOthello());

		PerformanceMeasure<OthelloPlayer> meuMeasure = new AgainstOthelloRandomPlayerPerformanceMeasure(othello,
				SAMPLE_SIZE);

		// Measure is a number of games against Standard WPC Heuristic in eps=0.1 settings
		PerformanceMeasure<OthelloPlayer> swhMeasure = new OthelloLeaguePerformanceMeasure(SAMPLE_SIZE);

		//PerfProfileDatabase<WPC> db = serialization.deserialize(new File(
		//	getProfilesDBDir(), "othello-bw-profile-db-rsel-symmetric.dump"));
		// new File(CommonConstants.DB_DIR, "othello-pprofile-db-random-new.dump"));

		// My players:
		List<Pair<String, ? extends OthelloPlayer>> players = new ArrayList<>();
		/*players.add(Pair.create("all-2-inv_best", new OthelloNTuplesPlayer(fromFile(
				new File(PolarisPathProvider.getPolarisPath(), "wj/best_swh_ntuples/all-2-inv-best.dump")),
				BoardEvaluationType.BOARD_INVERSION)));*/

		/*players.add(new Pair<String, OthelloPlayer>("PB11_ETDL", new Burrow2011ETDLPlayer().create()));
		players.add(new Pair<String, OthelloPlayer>("LR06", new LucasRunnarson2006Player().create()));
		players.add(new Pair<String, OthelloPlayer>("M10_Nash70", new Manning2010Nash70Player().create()));
		players.add(new Pair<String, OthelloPlayer>("EM10", new Manning2010TCIAG2Player().create()));
		players.add(new Pair<String, OthelloPlayer>("SJK09", new SzubertJaskowskiKrawiec2009Player().create()));*/
		//players.add(new Pair<String, OthelloPlayer>("SJK11", new SzubertJaskowskiKrawiec2011Player().create()));
		/*players.add(new Pair<String, OthelloPlayer>("SJK13_ETDL",
				new SzubertJaskowskiKrawiec2013ETDLPlayer().create()));
		players.add(new Pair<String, OthelloPlayer>("SJK13_CTDL",
				new SzubertJaskowskiKrawiec2013CTDLPlayer().create()));
		players.add(new Pair<String, OthelloPlayer>("RL14_iPrefN", new RunnarsonLucas2014IPrefNPlayer().create()));
		players.add(new Pair<String, OthelloPlayer>("SWH", new OthelloStandardWPCHeuristicPlayer().create()));*/
		//players.add(new Pair<String, OthelloPlayer>("RL14_iPref1", new RunnarsonLucas2014IPref1Player().create()));
		/*players.add(new Pair<String, OthelloPlayer>("WJ_Coev", new OthelloNTuplesPlayer(
				serialization.<NTuples>deserializeWrapExceptions(
						new File(
								"/Users/Wojciech/mnt/cane/projects/ntuples-coev/results-all-234/all-3_600-1_eps-1_10000/100/best.dump")),
				BoardEvaluationType.BOARD_INVERSION)));*/

		//Pair.create("vs_swh_35x3", evaluatedFromFile(
		//	new File("/home/wojciech/mnt/polluks/projects/best_swh_ntuples_35x3/best_ntuples_5024.dump")).getIndividual()
		//Pair.create("1-2-3-966_from_gen_4945", evaluatedFromFile(
		//	new File(PolarisPathProvider.getPolarisPath(), "wj/best_swh_ntuples/1-2-3-966_from_gen_4945.dump")).getIndividual())

		//Pair.create("1-2-3-966-neg_from_gen_4987", evaluatedFromFile(
		//	new File("/home/wojciech/projects/cevo/cevo-experiments/experiments/best_swh_ntuples_1_2_3_966-neg/best_ntuples_4987.dump")).getIndividual()

		players.add(Pair.create("cmaes-against-swh",  new OthelloNTuplesPlayer(serialization.<NTuples>deserializeWrapExceptions(
				new File(
						"/Users/Wojciech/mnt/cane/projects/cmaes-othello/results/against-swh/all-4-2x2_300_CMAES_SWH/100/best.dump")),
				BoardEvaluationType.OUTPUT_NEGATION)));

		System.out.println("Name\tMEU\tConf\tSWH\tConf");
		for (Pair<String, ? extends OthelloPlayer> player : players) {
			//OthelloLeague.saveInOthelloLeagueFormat(player.second(), new File(player.first() + ".ntuple"));
			// Compute and print performance and 95% confidence interval
			StatisticalSummary meu = meuMeasure.measure(player.second(), context).stats();
			StatisticalSummary swh = swhMeasure.measure(player.second(), context).stats();
			System.out.println(String.format("%s\t%s\t%s", player.first(),
				meanWithConfidenceIntervalPercent(meu, 0.05, "%.2f\t%.2f"),
				meanWithConfidenceIntervalPercent(swh, 0.05, "%.2f\t%.2f")));

			// Compute performance profile
			// PerfProfile profile = PerfProfile.createForPlayerTeam(db, othello, Arrays.asList(player.second()), context);
			// profile.saveAsCSV(new File(player.first() + ".profile"));
		}
	}

	private static EvaluatedIndividual<NTuples> evaluatedFromFile(File file) {
		try {
			return SerializationManagerFactory.create().deserialize(file);
		} catch (SerializationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static NTuples fromFile(File file) {
		try {
			return SerializationManagerFactory.create().deserialize(file);
		} catch (SerializationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
