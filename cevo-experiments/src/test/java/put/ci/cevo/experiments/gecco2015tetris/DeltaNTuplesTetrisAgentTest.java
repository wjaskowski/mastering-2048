package put.ci.cevo.experiments.gecco2015tetris;

import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.experiments.tetris.TetrisNTuplesSystematicFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuplesStateValueFunction;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisBoard;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.games.tetris.Tetromino;
import put.ci.cevo.games.tetris.agents.DeltaNTuplesTetrisActionValueFunction;
import put.ci.cevo.games.tetris.agents.DeltaNTuplesTetrisAgent;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.util.RandomUtils;

public class DeltaNTuplesTetrisAgentTest {

	// This test is quite weak
	@Test
	public void testSameAsNonDeltaAgent() throws Exception {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		for (int i = 0; i < 10; ++i) {
			TetrisBoard board = new TetrisBoard();
			for (int c = 0; c < board.getWidth(); ++c) {
				for (int r = board.getHeight() - 1; r > board.getHeight() - 3; --r) {
					board.setValue(r, c, random.nextInt(0, 1));
				}
			}
			TetrisState state = new TetrisState(board, Tetromino.Z);
			Tetris tetris = new Tetris();
			NTuples ntuples = new TetrisNTuplesSystematicFactory(-1, 1, "1").createRandomIndividual(random);
			DeltaNTuplesTetrisAgent deltaAgent = new DeltaNTuplesTetrisAgent(ntuples);

			TetrisAction actualAction = deltaAgent.chooseAction(state, tetris.getPossibleActions(state), new RandomDataGenerator(new MersenneTwister(
					123))).getAction();

			Agent<TetrisState, TetrisAction> normalAgent = new AfterstateFunctionAgent<>(
					new NTuplesStateValueFunction<>(ntuples), tetris
			);

			TetrisAction expectedAction = normalAgent.chooseAction(state, tetris.getPossibleActions(state), new RandomDataGenerator(new MersenneTwister(
					123))).getAction();

			Assert.assertEquals(expectedAction, actualAction);
		}
	}

	@Test
	public void testEvaluations() throws Exception {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		TetrisNTuplesSystematicFactory ntuplesFactory = new TetrisNTuplesSystematicFactory(0, 0, "111|111|111");
		NTuples nTuples = ntuplesFactory.createRandomIndividual(random);
		Tetris tetris = new Tetris();
		NTuplesStateValueFunction<TetrisState> valueFunction = new NTuplesStateValueFunction<>(nTuples);

		DeltaNTuplesTetrisActionValueFunction deltaEvaluator = new DeltaNTuplesTetrisActionValueFunction(nTuples);

		for (int i = 0; i < 1000; i++) {
			TetrisState state = tetris.sampleInitialStateDistribution(random);
			while (!tetris.isTerminal(state)) {
				List<TetrisAction> actions = tetris.getPossibleActions(state);
				for (TetrisAction action : actions) {
					double deltaEval = deltaEvaluator.getValue(state, action);
					AgentTransition<TetrisState, TetrisAction> agentTransition = tetris.getAgentTransition(state,
							action);
					double afterstateEval = valueFunction.getValue(agentTransition.getAfterState()) + agentTransition.getReward();
					Assert.assertEquals(afterstateEval, deltaEval, 1e-6);
				}
				TetrisAction tetrisAction = RandomUtils.pickRandom(actions, random);
				AgentTransition<TetrisState, TetrisAction> agentTransition = tetris.getAgentTransition(state,
						tetrisAction);
				state = tetris.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
			}
		}
	}
}