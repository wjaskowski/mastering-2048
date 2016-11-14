package put.ci.cevo.experiments.tetris;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuplesStateValueFunction;
import put.ci.cevo.games.tetris.*;
import put.ci.cevo.games.tetris.agents.DeltaNTuplesTetrisAgent;
import put.ci.cevo.rl.learn.TDAfterstateValueLearning;

public class TetrisTDLNTuplesRegressionTest {

	@Test
	public void testSimulatePerformance() {
		TetrisNTuplesSystematicFactory ntuplesFactory = new TetrisNTuplesSystematicFactory(0, 0, "111|111|111|111");
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		NTuples nTuples = ntuplesFactory.createRandomIndividual(random);

		Tetris tetris = Tetris.newSZTetris();
		NTuplesStateValueFunction<TetrisState> valueFunction = new NTuplesStateValueFunction<>(nTuples);
		DeltaNTuplesTetrisAgent deltaTetrisAgent = new DeltaNTuplesTetrisAgent(nTuples);
		TDAfterstateValueLearning<TetrisState, TetrisAction> algorithm = new TDAfterstateValueLearning<>(tetris,
				valueFunction, deltaTetrisAgent);

		for (int i = 0; i <= 100; i++) {
			algorithm.fastLearningEpisode(0.1, 0.001 * nTuples.getAll().size(), random);
		}
		double value = valueFunction.getValue(new TetrisState(new TetrisBoard(), Tetromino.S));
		Assert.assertEquals(7.13344739, value, 1e-3);
	}
}
