package put.ci.cevo.games.encodings.ntuple;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardState;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public class NTuplesStateValueFunction<S extends BoardState> implements LearnableStateValueFunction<S> {

	public NTuples getNtuples() {
		return ntuples;
	}

	private final NTuples ntuples;

	public NTuplesStateValueFunction(NTuples ntuples) {
		this.ntuples = ntuples;
	}

	@Override
	public double getValue(S state) {
		Board board = state.getBoard();
		double value = 0;
		for (NTuple tuple : ntuples.getAll()) {
			value += tuple.getValue(board);
		}
		return value;
	}

	@Override
	public void increase(S state, double delta) {
		double oneTupleDelta = delta / ntuples.getAll().size();
		Board board = state.getBoard();
		for (NTuple tuple : ntuples.getAll()) {
			tuple.increaseValue(board, (float)oneTupleDelta);
		}
	}

	@Override
	public int getActiveFeaturesCount() {
		return ntuples.getAll().size();
	}

	@Override
	public double getActiveWeight(S state, int idx) {
		return ntuples.getTuple(idx).getValue(state.getBoard());
	}

	@Override
	public void setActiveWeight(S state, int idx, double value) {
		ntuples.getTuple(idx).setValue(state.getBoard(), (float) value);
	}

	@Override
	public void increaseActiveWeight(S state, int idx, double delta) {
		ntuples.getTuple(idx).increaseValue(state.getBoard(), (float) delta);
	}
}
