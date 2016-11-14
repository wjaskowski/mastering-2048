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

public class GECCO2015BestBICEMSZTetrisAgent implements Factory<Agent<TetrisState, TetrisAction>> {

	private static final DoubleVector PARAMETERS = DoubleVector.of(
			59.194137593795226, -63.5940734614334, -2.3687704492235535, 5.9459548021642705, -8.566509757574977,
			-1.0843773815087367, 2.9022980730010777, 35.513622654680155, 28.70445689432378, 12.98884975555063,
			9.33326808884782, -0.6167593033074662, -35.78777140556006, -6.153073421643254, -38.26813726357568,
			0.6758211840572911, -15.778669475450474, 43.62252949722522, -4.035415018273773, 76.48034630535895,
			-70.66480900473923, 81.93747893125312);
	private static final StateValueFunction<TetrisState> STATE_STATE_VALUE_FUNCTION =
			new LinearFeaturesStateValueFunction<>(
					new BertsekasIoffeTetrisFeaturesExtractor(), PARAMETERS);

	@Override
	public Agent<TetrisState, TetrisAction> create() {
		return new AfterstateFunctionAgent<>(STATE_STATE_VALUE_FUNCTION, new GreedyAfterstatePolicy<>(
				Tetris.newSZTetris(), true));
	}
}
