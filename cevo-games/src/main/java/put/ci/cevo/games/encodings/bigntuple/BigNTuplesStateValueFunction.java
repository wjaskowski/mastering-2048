package put.ci.cevo.games.encodings.bigntuple;

import org.apache.commons.lang.NotImplementedException;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardState;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public class BigNTuplesStateValueFunction<S extends BoardState> implements LearnableStateValueFunction<S> {

	public BigNTuples getNtuples() {
		return ntuples;
	}

	private final BigNTuples ntuples;

	public BigNTuplesStateValueFunction(BigNTuples ntuples) {
		this.ntuples = ntuples;
	}

	@Override
	public double getValue(S state) {
		Board board = state.getBoard();
		//TODO: Get rid of copying the board

		float value = 0;
		for (BigNTuple tuple : ntuples.getAll()) {
			value += tuple.valueFor(board);
		}
		return value;
	}

	@Override
	public void increase(S state, double delta) {
		Board board = state.getBoard();
		for (BigNTuple tuple : ntuples.getAll()) {
			tuple.add(tuple.address(board), (float) delta);
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
