package put.ci.cevo.games.tetris.agents;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.tetris.BertsekasIoffeTetrisFeaturesExtractor;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.policies.GreedyAfterstatePolicy;
import put.ci.cevo.rl.evaluation.LinearFeaturesStateValueFunction;
import put.ci.cevo.rl.evaluation.StateValueFunction;
import put.ci.cevo.util.vectors.DoubleVector;

public class GECCO2015BestBICMAESSZTetrisAgent implements Factory<Agent<TetrisState, TetrisAction>> {

	private static final DoubleVector PARAMETERS = DoubleVector.of(
			70.8, -76.2, -5.5, -2.4, -11.9, 0.58, -15.7, 60.3, 72.2, -22.0, 27.3, -0.9, -41.8, -2.3,
			-44.4, 0.75, -28.3, 55.7, -56.2, 93.1, -55.5, 0.0);
	private static final StateValueFunction<TetrisState> STATE_STATE_VALUE_FUNCTION =
			new LinearFeaturesStateValueFunction<>(
					new BertsekasIoffeTetrisFeaturesExtractor(), PARAMETERS);

	@Override
	public Agent<TetrisState, TetrisAction> create() {
		return new AfterstateFunctionAgent<>(STATE_STATE_VALUE_FUNCTION, new GreedyAfterstatePolicy<>(
				Tetris.newSZTetris(), true));
	}
}
