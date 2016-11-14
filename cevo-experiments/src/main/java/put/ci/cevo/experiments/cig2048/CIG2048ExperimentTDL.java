package put.ci.cevo.experiments.cig2048;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.experiments.ntuple.NTuples2x3and4RandomIndividualFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.game2048.Game2048Board;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class CIG2048ExperimentTDL {

	public static void main(String[] args) {
		SerializationManager serializer = SerializationManagerFactory.create();
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(1221));

		Game2048TDLearning tdlGame2048 = new Game2048TDLearning();

		//NTuples vFunction = new NTuples2x3and4RandomIndividualFactory(14, 0, 0).createRandomIndividual(random);
//		NTuples lines = new NTuplesAll4StraightRandomIndividualFactory(14, 0, 0).createRandomIndividual(random);
//		NTuples squares = new NTuplesAllSquaresRandomIndividualFactory(2, 4, 14, 0, 0, new IdentitySymmetryExpander())
//			.createRandomIndividual(random);
//		NTuples vFunction = lines.add(squares);
//		
		
		
		//NTuples vFunction = serializer.deserializeWrapExceptions(new File("run-2-0.005.bin"));
		
		NTuples vFunction = new NTuples2x3and4RandomIndividualFactory(Game2048Board.RECT, 15, 0, 0, new RotationMirrorSymmetryExpander(Game2048Board.RECT)).createRandomIndividual(random);
		System.out.println(vFunction);
		System.out.println(vFunction.totalWeights());
		
		//evaluatePerformance(tdlGame2048, vFunction, 10000, random, 0);
		
		
//		for (int i = 0; i <= 100000; i++) {
//			tdlGame2048.TDAfterstateLearn(vFunction, 0.001, 0.01, random);
//			if (i % 5000 == 0) {
//				evaluatePerformance(tdlGame2048, vFunction, 1000, random, i);
//			}
//		}
//
//		List<NTuple> main = vFunction.getMain();
//		for (NTuple tuple : main) {
//			int countZero = 0;
//			double[] weights = tuple.getWeights();
//
//			for (double w : weights) {
//				if (w == 0) {
//					countZero++;
//				}
//			}
//
//			System.out.println(countZero + " of " + weights.length);
//		}
//
//		serializer.serializeWrapExceptions(vFunction, new File("last.dump"));

		
	}

	private static void evaluatePerformance(Game2048TDLearning game, RealFunction vFunction, int numEpisodes,
			RandomDataGenerator random, int e) {
		double performance = 0;
		double ratio = 0;
		int maxTile = 0;
		for (int i = 0; i < numEpisodes; i++) {
			// Pair<Integer, Integer> res = game.playByExpectimax(vFunction, random);
			Game2048Outcome res = game.playByAfterstates(vFunction, random);

			performance += res.score();
			ratio += (res.getLastState().getMaxTile() >= 2048) ? 1 : 0;
			maxTile = Math.max(maxTile, res.getLastState().getMaxTile());
		}

		System.out.println(String.format("After %5d: %8.2f, ratio = %4.4f, maxTile = %5d", e,
			performance / numEpisodes, ratio / numEpisodes, maxTile));
	}
}
