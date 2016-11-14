package put.ci.cevo.games.connect4.evaluators;

import put.ci.cevo.games.connect4.Connect4Action;
import put.ci.cevo.games.connect4.Connect4State;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

public class Connect4NTuplesQFunction implements ActionValueFunction<Connect4State, Connect4Action> {

	private final Connect4NTuplesMoveDeltaEvaluator evaluator;

	public Connect4NTuplesQFunction(NTuples ntuples) {
		this.evaluator = new Connect4NTuplesMoveDeltaEvaluator(ntuples);
	}

	@Override
	public double getValue(Connect4State state, Connect4Action action) {
		return evaluator.evaluateMove(state.getBoard(), action.getCol(), state.getPlayerToMove());
	}
}