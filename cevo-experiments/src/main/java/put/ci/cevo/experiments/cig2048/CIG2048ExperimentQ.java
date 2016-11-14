package put.ci.cevo.experiments.cig2048;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.experiments.ntuple.NTuplesAllStraightFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.IdentitySymmetryExpander;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.Pair;

public class CIG2048ExperimentQ {
	public static void main(String[] args) {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		Game2048QLearning game = new Game2048QLearning();

		RealFunction[] functions = new RealFunction[4];
		for (int i = 0; i < 4; i++) {
			NTuples lines = new NTuplesAllStraightFactory(
				4, State2048.BOARD_SIZE, 14, 0, 0, new IdentitySymmetryExpander())
				.createRandomIndividual(random);
			NTuples squares = new NTuplesAllStraightFactory(
				2, State2048.BOARD_SIZE, 14, 0, 0, new IdentitySymmetryExpander())
				.createRandomIndividual(random);
			functions[i] = lines.add(squares);
		}

		ActionValueFunction2048 qFunction = new ActionValueFunction2048(functions);

		for (int i = 0; i < 100000000; i++) {
			game.QLearn(qFunction, 0.001, 0.01, random);
			if (i % 5000 == 0) {
				evaluatePerformance(game, qFunction, 1000, random, i);
			}
		}
	}

	private static void evaluatePerformance(Game2048QLearning game, ActionValueFunction2048 qFunction, int numEpisodes,
			RandomDataGenerator random, int e) {
		double performance = 0;
		double ratio = 0;
		int maxTile = 0;
		for (int i = 0; i < numEpisodes; i++) {
			Pair<Integer, Integer> res = game.play(qFunction, random);

			performance += res.first();
			ratio += (res.second() >= 2048) ? 1 : 0;
			maxTile = Math.max(maxTile, res.second());
		}

		System.out.println(String.format("After %5d: %8.2f, ratio = %4.2f, maxTile = %5d", e,
			performance / numEpisodes, ratio / numEpisodes, maxTile));
	}
}
