package put.ci.cevo.games.player.minmax;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singletonList;

import java.util.List;

import put.ci.cevo.games.board.Board;

public class NodeState {

	private final List<Integer> moves;
	private final double score;

	public NodeState(double score) {
		this(Board.EMPTY, score);
	}

	public NodeState(int move, double score) {
		this(singletonList(move), score);
	}

	public NodeState(List<Integer> moves, double score) {
		this.moves = moves;
		this.score = score;
	}

	public int getMove() {
		return getOnlyElement(moves);
	}

	public List<Integer> getMoves() {
		return moves;
	}

	public double getScore() {
		return score;
	}

}
