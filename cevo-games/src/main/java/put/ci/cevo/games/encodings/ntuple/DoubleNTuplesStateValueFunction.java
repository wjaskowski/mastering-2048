package put.ci.cevo.games.encodings.ntuple;

import put.ci.cevo.games.TwoPlayerGameState;
import put.ci.cevo.games.board.BoardState;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import org.apache.commons.lang.NotImplementedException;

public class DoubleNTuplesStateValueFunction<S extends TwoPlayerGameState & BoardState>
		implements LearnableStateValueFunction<S> {

	private final NTuplesStateValueFunction<S> firstStateValueFunction;
	private final NTuplesStateValueFunction<S> secondStateValueFunction;

	public DoubleNTuplesStateValueFunction(DoubleNTuples ntuples) {
		firstStateValueFunction = new NTuplesStateValueFunction<>(ntuples.first());
		secondStateValueFunction = new NTuplesStateValueFunction<>(ntuples.second());
	}

	@Override
	public double getValue(S state) {
		return state.isFirstPlayerToMove() ?
				firstStateValueFunction.getValue(state) :
				secondStateValueFunction.getValue(state);
	}

	@Override
	public void increase(S state, double delta) {
		if (state.isFirstPlayerToMove()) {
			firstStateValueFunction.increase(state, delta);
		} else {
			secondStateValueFunction.increase(state, delta);
		}
	}

	@Override
	public int getActiveFeaturesCount() {
		throw new NotImplementedException();
	}

	@Override
	public double getActiveWeight(S state, int idx) {
		throw new NotImplementedException();
	}

	@Override
	public void setActiveWeight(S state, int idx, double value) {
		throw new NotImplementedException();
	}

	@Override
	public void increaseActiveWeight(S state, int idx, double delta) {
		throw new NotImplementedException();
	}
}

