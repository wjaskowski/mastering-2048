package put.ci.cevo.games.connect4.evaluators;

import com.google.common.collect.ImmutableMap;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.player.BoardMoveEvaluator;

import java.util.Map;

public class Connect4DoubleNTuplesMoveDeltaEvaluator implements BoardMoveEvaluator<Connect4Board> {

	private final Map<Integer, Connect4NTuplesMoveDeltaEvaluator> evalators;

	public Connect4DoubleNTuplesMoveDeltaEvaluator(DoubleNTuples ntuples) {
		this.evalators = ImmutableMap.<Integer, Connect4NTuplesMoveDeltaEvaluator> builder()
				.put(Board.BLACK, new Connect4NTuplesMoveDeltaEvaluator(ntuples.first()))
				.put(Board.WHITE, new Connect4NTuplesMoveDeltaEvaluator(ntuples.second()))
			.build();
	}

	/**
	 * Returns function evaluation value for a given board (its delta - only for the things that changed
	 */
	@Override
	public double evaluateMove(Connect4Board board, int move, int player) {
		return evalators.get(player).evaluateMove(board, move, player);
	}
}
