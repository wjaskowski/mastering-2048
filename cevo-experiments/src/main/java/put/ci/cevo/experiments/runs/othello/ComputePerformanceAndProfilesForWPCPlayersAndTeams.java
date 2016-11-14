package put.ci.cevo.experiments.runs.othello;

import static org.apache.commons.io.filefilter.FileFilterUtils.directoryFileFilter;
import static put.ci.cevo.framework.measures.PerformanceMeasureUtils.measurePerformanceForTeam;
import static put.ci.cevo.profiles.experiments.PolarisPathProvider.getProfilesDBDir;
import static put.ci.cevo.util.StatisticUtils.meanWithConfidenceIntervalPercent;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import put.ci.cevo.experiments.othello.OthelloInteraction;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.profiles.PerfProfile;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.experiments.wpc.othello.mappers.WPCOthelloPlayerMapper;
import put.ci.cevo.framework.factories.StaticPopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.individuals.loaders.FilesIndividualLoader;
import put.ci.cevo.framework.individuals.loaders.LoadersFactory;
import put.ci.cevo.framework.measures.AgainstPlayerPerformanceMeasure;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.players.OthelloRandomPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristic;
import put.ci.cevo.profiles.experiments.PolarisPathProvider;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

/** Computes the performance of the standard WPC heuristic for Othello */
public class ComputePerformanceAndProfilesForWPCPlayersAndTeams {

	private static class NamedTeam<S> {
		public final String name;
		public final List<S> players;

		public NamedTeam(String name, List<S> players) {
			this.name = name;
			this.players = new ArrayList<>(players);
		}

		@SuppressWarnings("unused")
		public NamedTeam(String name, S player) {
			this.name = name;
			this.players = Arrays.asList(player);
		}

		public static <S> NamedTeam<S> fromDirectory(FilesIndividualLoader<S> loader, File dir, String name) {
			List<S> individuals = loader.loadIndividuals(dir);
			return new NamedTeam<>(name, individuals);
		}

		public static <S> List<NamedTeam<S>> fromDirectory(FilesIndividualLoader<S> loader, File dir) {

			List<NamedTeam<S>> teams = Lists.newArrayList();
			for (File file : dir.listFiles((FileFilter) directoryFileFilter())) {
				teams.add(fromDirectory(loader, file, file.getName()));
			}
			return teams;
		}
	}

	private static final int NUM_OPPONENTS = 50000;
	private static final double FORCE_RANDOM_MOVE_PROBABILITY = 0.1;

	public static void main(String[] args) throws SerializationException, IOException {
		ThreadedContext context = new ThreadedContext(123);

		PerformanceMeasure<WPC> meuMeasure = new ExpectedUtility<>(
			new OthelloWPCInteraction(true), new StaticPopulationFactory<>(new UniformRandomPopulationFactory<>(
				new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0)), NUM_OPPONENTS, context.getRandomForThread()),
			NUM_OPPONENTS);

		// Measure is a number of games against Standard WPC Heuristic
		PerformanceMeasure<WPC> swhMeasure = new AgainstPlayerPerformanceMeasure<WPC, WPC>(
			new OthelloInteraction<>(
				new WPCOthelloPlayerMapper(), new WPCOthelloPlayerMapper(), true, FORCE_RANDOM_MOVE_PROBABILITY),
			new OthelloStandardWPCHeuristic().create(), NUM_OPPONENTS);

		// Measure is a number of games against Random Move Opponent
		PerformanceMeasure<WPC> rndMeasure = new AgainstPlayerPerformanceMeasure(
			new OthelloInteraction<>(
				new WPCOthelloPlayerMapper(), new WPCOthelloPlayerMapper(), true),
			new OthelloRandomPlayer(), NUM_OPPONENTS);

		SerializationManager serializationManager = SerializationManagerFactory.create();
//		PerfProfileDatabase<WPC> db = serializationManager.deserialize(new File(getProfilesDBDir(), "othello-pprofile-db-context-new.dump"));
		PerfProfileDatabase<WPC> db = serializationManager.deserialize(new File(
			getProfilesDBDir(), "othello-bw-profile-db-rsel-symmetric.dump"));

		// My players:
		// @formatter:off
//		List<NamedTeam<WPC>> teams = Arrays.asList(
			//new NamedTeam<>("swh", new OthelloStandardWPCHeuristic().create()),
			//new NamedTeam<>("luc06", new LucasRunnarson2006().create()),
			//new NamedTeam<>("szu09", new SzubertJaskowskiKrawiec2009().create()),
			//new NamedTeam<>("szu11", new SzubertJaskowskiKrawiec2011().create()),
			//new NamedTeam<>("vs_swh_1", evaluatedFromFile(new File(getPolarisPath(), "wj/best_swh_wpc/best_wpc_10857.dump"))),
			//new NamedTeam<>("vs_meu_1", evaluatedFromFile(new File(getPolarisPath(), "wj/best_meu_wpc/best_wpc_6842.dump"))),
//			new NamedTeam<WPC>("cel-rs", new DefaultIndividualsLoader<WPC>(
//					EvaluatedIndividualLoader.<WPC>individualLoader()).loadIndividuals(new File(getPolarisPath(),
//					"evostar2013/CEL-RS/optimize-mut/celrs-20-20-200-227-mucomma-uniform-1.0-1.0"), "run-*")),
//			new NamedTeam<WPC>("tdl", new StringWPCLoader().loadIndividuals(new File(PolarisPathProvider.getPolarisPath(),
//					"evostar2013/TDL/alpha_0.05_eps_0.05_2"), "*.ind")));
		// @formatter:on
		List<NamedTeam<WPC>> teams = NamedTeam.fromDirectory(LoadersFactory.<WPC> serializedIndividualPerFileLoader(),
			new File(PolarisPathProvider.getPolarisPath(), "ieeetec/players/othello"));

		System.out.println("Name\tMEU\tConf\tSWH\tConf\tRND\tConf");
		for (NamedTeam<WPC> team : teams) {
			// Compute and print performance and 95% confidence interval
			StatisticalSummary meu = measurePerformanceForTeam(team.players, meuMeasure, context);
			StatisticalSummary swh = measurePerformanceForTeam(team.players, swhMeasure, context);
			StatisticalSummary rnd = measurePerformanceForTeam(team.players, rndMeasure, context);
			System.out.println(String.format("%s\t%s\t%s\t%s", team.name,
				meanWithConfidenceIntervalPercent(meu, 0.05, "%.2f\t%.2f"),
				meanWithConfidenceIntervalPercent(swh, 0.05, "%.2f\t%.2f"),
				meanWithConfidenceIntervalPercent(rnd, 0.05, "%.2f\t%.2f")));
		}

		// Compute performance profile
		for (NamedTeam<WPC> team : teams) {
			PerfProfile profile = PerfProfile.createForPlayerTeam(db, new OthelloWPCInteraction(true), team.players,
				context);
			profile.saveAsCSV(new File(team.name + ".profile"));
		}
	}

	private static WPC evaluatedFromFile(File file) {
		EvaluatedIndividual<WPC> evaluated = SerializationManagerFactory.create().deserializeWrapExceptions(file);
		return evaluated.getIndividual();
	}
}
