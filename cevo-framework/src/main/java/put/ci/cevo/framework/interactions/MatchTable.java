package put.ci.cevo.framework.interactions;

import java.util.*;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * An alternative to InteractionTable. Handles multiple interactions between two players
 * TODO: Consider making it X, Y
 */
public class MatchTable<X> {

	private final IdentityHashMap<X, Integer> ids;
	private final int[][] games;
	private final int[] totalGames;
	private final double[][] scores;
	private final double[] totalScores;
	private final int size;

	private long totalEffort;

	public MatchTable(List<X> players) {
		size = players.size();

		ids = new IdentityHashMap<>(size);
		for (int i = 0; i < size; ++i) {
			ids.put(players.get(i), i);
		}

		games = new int[size][size];
		scores = new double[size][size];
		totalGames = new int[size];
		totalScores = new double[size];
	}

	public double averageScoreFor(X player) {
		return averageScoreFor(ids.get(player));
	}

	public double averageScoreFor(X player, X opponent) {
		return averageScoreFor(ids.get(player), ids.get(opponent));
	}

	private double averageScoreFor(Integer playerId, Integer opponentId) {
		int gam = games[playerId][opponentId];
		double sco = scores[playerId][opponentId];

		return gam == 0 ? 0 : sco / gam;
	}

	private int getGames(X player1, X player2) {
		return games[ids.get(player1)][ids.get(player2)];
	}

	private double averageScoreFor(int id) {
		double sco = totalScores[id];
		int gam = totalGames[id];

		return gam == 0 ? 0 : sco / gam;
	}

	/**
	 * Average strength of players the player played against
	 */
	//TODO: I do not like this specialized method here
	public double averageSecondaryScoreFor(X player) {
		int id = ids.get(player);

		//TODO: Could be quicker for sparse table (gamesPlayed = 0. Could be cached
		double total = 0;
		for (int i = 0; i < size; ++i) {
			// The secondary score is weighted by the number of games played against an opponent
			total += games[id][i] * averageScoreFor(i);
		}
		return totalGames[id] == 0 ? 0 : total / totalGames[id];
	}

	/**
	 * The total score weighted by my opponent's scores
	 */
	//TODO: I do not like this specialized method here
	public double weightedAverageScoreFor(X player) {
		int id = ids.get(player);

		double total = 0.0;
		double totalWeight = 0.0;
		for (int i = 0; i < size; ++i) {
			double w = averageScoreFor(i);
			total += w * scores[id][i];
			totalWeight += w * games[id][i];
		}
		return totalWeight == 0.0 ? 0.0 : total / totalWeight;
	}

	public void addSymmetricResult(X firstPlayer, X secondPlayer, InteractionResult result) {
		addResult(firstPlayer, secondPlayer, result, true);
	}

	public void addResult(X firstPlayer, X secondPlayer, InteractionResult result, boolean isSymmetricResult) {
		int id1 = ids.get(firstPlayer);
		int id2 = ids.get(secondPlayer);

		scores[id1][id2] += result.firstResult();
		totalScores[id1] += result.firstResult();
		games[id1][id2] += 1;
		totalGames[id1] += 1;

		if (isSymmetricResult) {
			scores[id2][id1] += result.secondResult();
			totalScores[id2] += result.secondResult();
			games[id2][id1] += 1;
			totalGames[id2] += 1;
		}

		totalEffort += result.getEffort();
	}

	public long getTotalEffort() {
		return totalEffort;
	}

	public String toString(Map<X, String> names) {
		return toString(names, "%.1f");
	}

	public String toString(Map<X, String> names, String numberFormat) {

		ArrayList<X> copied = new ArrayList<>(ids.keySet());

		Collections.sort(copied, new Comparator<X>() {
			@Override
			public int compare(final X o1, final X o2) {
				return new CompareToBuilder().append(averageScoreFor(o2), averageScoreFor(o1)).toComparison();
			}
		});

		StringBuilder str = new StringBuilder();
		str.append("Team");
		for (X player : copied) {
			str.append("\t").append(names.get(player));
		}
		str.append("\tTotal\n");

		for (X player1 : copied) {
			str.append(names.get(player1));
			for (X player2 : copied) {
				str.append(getGames(player1, player2) > 0 ? String.format("\t" + numberFormat, 100 * averageScoreFor(
						player1, player2)) : "\t-");
			}
			//str.append(String.format("\t%.1f%%\n", 100 * total[key1]));
			str.append(String.format("\t" + numberFormat + "\n", 100 * averageScoreFor(player1)));
		}
		return str.toString();
	}
}
