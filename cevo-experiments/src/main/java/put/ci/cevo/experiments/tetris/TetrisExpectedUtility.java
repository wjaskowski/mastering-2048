package put.ci.cevo.experiments.tetris;

import put.ci.cevo.experiments.rl.AgentExpectedUtility;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * Computes the average reward over multiple Tetris episodes
 */
public class TetrisExpectedUtility implements PerformanceMeasure<Agent<TetrisState, TetrisAction>> {

	private final AgentExpectedUtility<TetrisState, TetrisAction> measure;

	public TetrisExpectedUtility(Tetris tetris, int measureGames) {
		measure = new AgentExpectedUtility<>(tetris, measureGames);
	}

	@Override
	public Measurement measure(RandomFactory<Agent<TetrisState, TetrisAction>> agentFactory, ThreadedContext context) {
		return measure.measure(agentFactory, context);
	}
}
