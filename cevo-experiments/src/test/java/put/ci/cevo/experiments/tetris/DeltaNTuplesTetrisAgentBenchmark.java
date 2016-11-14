package put.ci.cevo.experiments.tetris;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuplesStateValueFunction;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.games.tetris.agents.DeltaNTuplesTetrisAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.learn.TDAfterstateValueLearning;

public class DeltaNTuplesTetrisAgentBenchmark extends AbstractBenchmark {

	private Tetris tetris;
	private NTuplesStateValueFunction<TetrisState> valueFunction;
	private Agent<TetrisState, TetrisAction> deltaTetrisAgent;
	private Agent<TetrisState, TetrisAction> tetrisAgent;

	@Before
	public void setup() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		TetrisNTuplesSystematicFactory ntuplesFactory = new TetrisNTuplesSystematicFactory(0, 0, "111|111|111");
		NTuples nTuples = ntuplesFactory.createRandomIndividual(random);

		this.tetris = new Tetris();
		this.valueFunction = new NTuplesStateValueFunction<>(nTuples);
		this.tetrisAgent = new AfterstateFunctionAgent<>(valueFunction, tetris);
		this.deltaTetrisAgent = new DeltaNTuplesTetrisAgent(nTuples);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testAfterstateLearning() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		TDAfterstateValueLearning<TetrisState, TetrisAction> algorithm = new TDAfterstateValueLearning<>(tetris,
				valueFunction, tetrisAgent);

		for (int i = 0; i < 1000; i++) {
			algorithm.fastLearningEpisode(0.1, 0.001, random);
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testDeltaLearning() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		TDAfterstateValueLearning<TetrisState, TetrisAction> algorithm = new TDAfterstateValueLearning<>(tetris,
				valueFunction, deltaTetrisAgent);

		for (int i = 0; i < 1000; i++) {
			algorithm.fastLearningEpisode(0.1, 0.001, random);
		}
	}

}
