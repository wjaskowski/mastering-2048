package put.ci.cevo.experiments.gecco2015tetris;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.experiments.tetris.TetrisNTuplesSystematicFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuplesStateValueFunction;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.games.tetris.agents.DeltaNTuplesTetrisAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.learn.TDAfterstateValueLearning;

public class TDLTetrisTestExperiment {

	public static void main(String[] args) {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		//TetrisNTuplesSystematicFactory ntuplesFactory = new TetrisNTuplesSystematicFactory(0, 0, "111|111|111");
		TetrisNTuplesSystematicFactory ntuplesFactory = new TetrisNTuplesSystematicFactory(0, 0,
				"111111|111111|111111");
		NTuples nTuples = ntuplesFactory.createRandomIndividual(random);

		System.out.println(nTuples);
		System.out.println(nTuples.totalWeights());

		Tetris tetris = Tetris.newSZTetris();
		NTuplesStateValueFunction<TetrisState> valueFunction = new NTuplesStateValueFunction<>(nTuples);

		//Agent<TetrisState, TetrisAction> tetrisAgent = new AfterstateTetrisAgent(tetris, valueFunction);
		Agent<TetrisState, TetrisAction> tetrisAgent = new DeltaNTuplesTetrisAgent(nTuples);
		TDAfterstateValueLearning<TetrisState, TetrisAction> algorithm = new TDAfterstateValueLearning<>(tetris,
				valueFunction, tetrisAgent);

		for (int i = 0; i <= 1000000; i++) {
			algorithm.fastLearningEpisode(0.1, 0.001, random);
			if (i % 5000 == 0) {
				evaluatePerformance(tetris, tetrisAgent, 1000, random, i);
			}
		}
	}

	private static void evaluatePerformance(Tetris game, Agent<TetrisState, TetrisAction> agent, int numEpisodes, RandomDataGenerator random,
			int e) {

		double performance = 0;
		for (int i = 0; i < numEpisodes; i++) {
			double score = game.runEpisode(agent, random);
			performance += score;
		}

		System.out.println(String.format("After %5d: %8.2f", e, performance / numEpisodes));
	}
}
