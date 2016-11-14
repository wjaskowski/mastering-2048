package put.ci.cevo.games.othello.evaluators;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTupleUtils;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.carrotsearch.hppc.cursors.IntCursor;

/**
 * A quicker version of {@link OthelloNTuplesMoveEvaluator}. It computes not the state value, but just a difference
 * between current board and the board after the move.
 */
public class OthelloNTuplesMoveDeltaEvaluator implements BoardMoveEvaluator<OthelloBoard> {

	private final NTuples tuples;
	private final IntArrayList[] tuplesForPosition;

	public OthelloNTuplesMoveDeltaEvaluator(NTuples ntuples) {
		this.tuples = ntuples;
		this.tuplesForPosition = NTupleUtils.getTuplesForPositions(ntuples, OthelloBoard.BUFFER_SIZE);
	}

	/**
	 * Returns function evaluation value for a given board (its delta - only for the things that changed
	 */
	@Override
	public double evaluateMove(OthelloBoard board, int move, int player) {
		IntArrayList positions = board.simulateMove(move, player);
		if (positions == null) {
			return BoardMoveEvaluator.INVALID_MOVE;
		}

		double eval = 0.0;

		IntDoubleLinkedSet deltaTuples = new IntDoubleLinkedSet(OthelloBoard.BUFFER_SIZE, tuples.size());

		// DeltaTuples contains all tuples which value changes when making move
		for (int i = 0; i < positions.size(); ++i) {
			int pos = positions.buffer[i];
			for (IntCursor cur : tuplesForPosition[pos]) {
				deltaTuples.add(cur.value);
			}
		}

		// From eval remove values for tuples that will change
		for (int i = 0; i < deltaTuples.elementsCount; ++i) {
			NTuple tuple = tuples.getTuple(deltaTuples.dense[i]);
			eval -= tuple.getValue(board);
		}

		// Make move...
		board.makeMove(positions, player);

		// Add to eval new values for tuples that changed
		for (int i = 0; i < deltaTuples.elementsCount; ++i) {
			NTuple tuple = tuples.getTuple(deltaTuples.dense[i]);
			eval += tuple.getValue(board);
		}

		// ... and revert the move
		board.makeMove(positions, OthelloBoard.opponent(player));
		board.setValue(move, Board.EMPTY);

		return eval;
	}
}
