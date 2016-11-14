package put.ci.cevo.games.connect4.evaluators;

import com.carrotsearch.hppc.IntArrayList;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTupleUtils;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.player.BoardMoveEvaluator;

public class Connect4NTuplesMoveDeltaEvaluator implements BoardMoveEvaluator<Connect4Board> {

	private final NTuples tuples;
	private final IntArrayList[] tuplesForPosition;

	public Connect4NTuplesMoveDeltaEvaluator(NTuples ntuples) {
		this.tuples = ntuples;
		this.tuplesForPosition = NTupleUtils.getTuplesForPositions(ntuples, Connect4Board.BUFFER_SIZE);
	}

	@Override
	public double evaluateMove(Connect4Board board, int move, int player) {
		double eval = 0.0;

		final int pos = board.moveToPos(move);
		IntArrayList deltaTuples = tuplesForPosition[pos];

		// From eval remove values for tuples that will change
		for (int i = 0; i < deltaTuples.elementsCount; ++i) {
			NTuple tuple = tuples.getTuple(deltaTuples.buffer[i]);
			eval -= tuple.getValue(board);
		}

		// Make move...

		//TODO: simply setValue(pos, player)
		board.makeMove(move, player);

		//TODO: Hacky:
		double terminalValue = 0;
		boolean isTerminal = board.isGameOver();
		if (isTerminal) {
			if (board.getWinner() == player) {
				terminalValue = Double.MAX_VALUE;
			} else {
				assert board.getWinner() == -1: "Must be draw. I cannot lose on my move";
				terminalValue = Double.MAX_VALUE / 2;
			}
		}

		// Add to eval new values for tuples that changed
		for (int i = 0; i < deltaTuples.elementsCount; ++i) {
			NTuple tuple = tuples.getTuple(deltaTuples.buffer[i]);
			eval += tuple.getValue(board);
		}

		// ... and revert the move
		//TODO: simply setValue(pos, Board.EMPTY) [but just update the buffer]
		board.setValue(pos, Board.EMPTY);

		//TODO: Will it work with output negation?
		return isTerminal ? terminalValue : eval;
	}
}
