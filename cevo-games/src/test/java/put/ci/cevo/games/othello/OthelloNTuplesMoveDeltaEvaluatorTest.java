package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.NTupleRandomFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.othello.evaluators.OthelloNTuplesMoveDeltaEvaluator;
import put.ci.cevo.games.othello.evaluators.OthelloNTuplesMoveEvaluator;
import put.ci.cevo.games.othello.players.OthelloMoveEvaluatorPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.util.Lists;

public class OthelloNTuplesMoveDeltaEvaluatorTest {

	@Test
	public void testEvaluateMove() throws Exception {
		RandomDataGenerator mainRandom = new RandomDataGenerator(new MersenneTwister(123));

		for (int i = 0; i < 50; ++i) {
			NTupleRandomFactory ntupleFactory = new NTupleRandomFactory(10, 5, OthelloBoard.SIZE, -10, +10);
			NTuples ntuples = new NTuples(
				Lists.fromFactory(9, ntupleFactory, mainRandom), new RotationMirrorSymmetryExpander(
					OthelloBoard.SIZE));

			OthelloPlayer deltaEvaluatorPlayer = new OthelloMoveEvaluatorPlayer(new OthelloNTuplesMoveDeltaEvaluator(
				ntuples), BoardEvaluationType.OUTPUT_NEGATION);
			OthelloPlayer fullEvaluatorPlayer = new OthelloMoveEvaluatorPlayer(new OthelloNTuplesMoveEvaluator(
				ntuples), BoardEvaluationType.OUTPUT_NEGATION);

			OthelloWPCPlayer swh = new OthelloStandardWPCHeuristicPlayer().create();

			int seed = mainRandom.getRandomGenerator().nextInt();
			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(seed));

			Othello othello = new Othello(new MorePointsGameResultEvaluator(1, 0, 0.5));

			OthelloBoard board1 = othello.playImpl(deltaEvaluatorPlayer, swh, random);

			random = new RandomDataGenerator(new MersenneTwister(seed));

			OthelloBoard board2 = othello.playImpl(fullEvaluatorPlayer, swh, random);

			Assert.assertEquals(board1, board2);
		}
	}
}
