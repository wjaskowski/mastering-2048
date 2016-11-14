package put.ci.cevo.experiments.benchmarks;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.experiments.ntuple.NTuplesAllStraightFactory;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.IdentitySymmetryExpander;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.experiments.cig2048.Game2048TDLearning;
import put.ci.cevo.games.game2048.Game2048Outcome;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

public class Game2048Benchmark extends AbstractBenchmark {

	private ThreadedRandom random;
	private Game2048TDLearning tdlGame2048;
	private final MersenneTwister mersenneTwister = new MersenneTwister(123);

	@Before
	public void setUp() {
		random = new ThreadedRandom(mersenneTwister.nextLong());
		tdlGame2048 = new Game2048TDLearning();
	}

	@BenchmarkOptions(benchmarkRounds = 3, warmupRounds = 1)
	@Test
	public void testNTuplesPlayPerformance() {
		RandomDataGenerator rdg = random.forThread();
		NTuples vFunction = new NTuplesAllStraightFactory(
			4, State2048.BOARD_SIZE, 14, 0, 0, new IdentitySymmetryExpander()).createRandomIndividual(rdg);

		int numGames = 5000;
		double sumResult = 0;
		for (int i = 0; i < numGames; i++) {
			Game2048Outcome result = tdlGame2048.playByAfterstates(vFunction, rdg);
			sumResult += result.score();
		}
		System.out.println(sumResult / numGames);
	}

	// @BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	// @Test
	public void testNTuplesLearnPerformance() {
		RandomDataGenerator rdg = random.forThread();
		NTuples vFunction = new NTuplesAllStraightFactory(
			4, State2048.BOARD_SIZE, 14, 0, 0, new IdentitySymmetryExpander()).createRandomIndividual(rdg);
		for (int i = 0; i < 1000; i++) {
			tdlGame2048.TDAfterstateLearn(vFunction, 0.001, 0.01, rdg);
		}
	}
}
