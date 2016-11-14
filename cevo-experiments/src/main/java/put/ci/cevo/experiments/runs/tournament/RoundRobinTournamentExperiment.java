package put.ci.cevo.experiments.runs.tournament;

import org.apache.log4j.Logger;
import put.ci.cevo.framework.individuals.loaders.FilesIndividualLoader;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.random.ThreadedRandom;

import java.io.File;
import java.util.*;

import static put.ci.cevo.experiments.runs.tournament.RoundRobinTournamentExperiment.Config.*;
import static put.ci.cevo.util.configuration.Configuration.getConfiguration;

//TODO: Should use RoundRobinTournamentForTeams class
public class RoundRobinTournamentExperiment<T> implements Runnable {

	public static enum Config implements ConfigurationKey {
		EVALUATOR("tournament.evaluator"),
		EXPERIMENTS_DIR("tournament.experiments_dir"),
		EXPERIMENTS("tournament.experiments"),
		WILDCARD("tournament.wildcard"),
		PLAYERS_TYPE("tournament.players_type"),
		TOURNAMENT("tournament");

		private final String key;

		private Config(String key) {
			this.key = key;
		}

		public ConfigurationKey dot(Object subKey) {
			return ConfKey.dot(this, subKey);
		}

		@Override
		public String toString() {
			return key;
		}
	}

	private static final Logger logger = Logger.getLogger(RoundRobinTournamentExperiment.class);
	private static final Configuration config = Configuration.getConfiguration();

	// TODO: We should probably take the seed from Experiment configuration, but none config entry is present
	private final ThreadedRandom random = new ThreadedRandom(123);

	private final InteractionDomain<T, T> interaction;

	@AccessedViaReflection
	public RoundRobinTournamentExperiment(InteractionDomain<T, T> interaction) {
		this.interaction = interaction;
	}

	@Override
	public void run() {
		FilesIndividualLoader<T> playersLoader = config.getObject(PLAYERS_TYPE);

		File experimentsDir = config.getFile(EXPERIMENTS_DIR);
		List<String> teamNames = config.getList(EXPERIMENTS);
		Map<String, List<T>> teams = new HashMap<String, List<T>>();

		String wildcard = config.getString(WILDCARD);
		for (String experiment : teamNames) {
			logger.info("Loading players for: " + experiment);
			teams.put(experiment, playersLoader.loadIndividuals(new File(experimentsDir, experiment), wildcard));
		}

		final int n = teams.size();
		final double[][] rr = new double[n][n];
		final double[] total = new double[n];
		final int[] totalGames = new int[n];

		for (int i = 0; i < n; i++) {
			List<T> team1 = teams.get(teamNames.get(i));
			for (int j = i + 1; j < n; j++) {
				List<T> team2 = teams.get(teamNames.get(j));
				double team1scores = 0;
				double team2scores = 0;
				int numGames = 0;
				for (T player1 : team1) {
					for (T player2 : team2) {
						InteractionResult gameResult = interaction.interact(player1, player2, random.forThread());
						team1scores += gameResult.firstResult();
						team2scores += gameResult.secondResult();
						numGames += 1;
					}
				}
				rr[i][j] = team1scores / numGames;
				rr[j][i] = team2scores / numGames;
				total[i] += team1scores;
				total[j] += team2scores;
				totalGames[i] += numGames;
				totalGames[j] += numGames;
			}
		}

		Integer[] keys = new Integer[n];
		for (int i = 0; i < n; i++) {
			keys[i] = i;
		}

		for (int i = 0; i < n; i++) {
			total[i] /= totalGames[i];
		}

		Arrays.sort(keys, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				double diff = total[o2] - total[o1];
				if (diff > 0) {
					return 1;
				}
				if (diff < 0) {
					return -1;
				}
				return 0;
			}
		});

		StringBuilder str = new StringBuilder();
		str.append("Team");
		for (int key : keys) {
			str.append("\t" + teamNames.get(key));
		}
		str.append("\tTotal\n");

		for (int key1 : keys) {
			str.append(teamNames.get(key1));
			for (int key2 : keys) {
				str.append(rr[key1][key2] > 0 ? String.format("\t%.1f%%", 100 * rr[key1][key2]) : "\t-");
			}
			str.append(String.format("\t%.1f%%\n", 100 * total[key1]));
		}
		System.out.println(str.toString());
	}

	public static <T> void main(String[] args) {
		RoundRobinTournamentExperiment<T> rr = getConfiguration().getObject(TOURNAMENT);
		rr.run();
	}

}
