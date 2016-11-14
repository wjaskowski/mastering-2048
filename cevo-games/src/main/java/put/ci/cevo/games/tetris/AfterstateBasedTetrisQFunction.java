package put.ci.cevo.games.tetris;

import com.carrotsearch.hppc.IntArrayList;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.rl.evaluation.StateValueFunction;

public class AfterstateBasedTetrisQFunction implements ActionValueFunction<TetrisState, TetrisAction> {

	private final StateValueFunction<TetrisState> stateValueFunction;

	public AfterstateBasedTetrisQFunction(StateValueFunction<TetrisState> stateValueFunction) {
		this.stateValueFunction = stateValueFunction;
	}

	@Override
	public double getValue(TetrisState state, TetrisAction action) {
		TetrisBoard.SimulationResult result = Tetris.simulateAction(state, action);
		IntArrayList changedPositions = result.changedPositions;
		double reward = result.reward;

		double eval = -stateValueFunction.getValue(state);

		// Make the action...
		Tetris.swapValues(state, changedPositions);

		eval += stateValueFunction.getValue(state);

		// ... and revert it
		Tetris.swapValues(state, changedPositions);

		return eval + reward;
	}
}
