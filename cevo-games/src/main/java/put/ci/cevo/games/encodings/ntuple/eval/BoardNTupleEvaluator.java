package put.ci.cevo.games.encodings.ntuple.eval;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;

public class BoardNTupleEvaluator {
	public double evaluate(NTuples tuples, Board board) {
		double result = 0;
		for (NTuple tuple : tuples.getAll()) {
			result += tuple.getValue(board);
		}
		return result;
	}

}
