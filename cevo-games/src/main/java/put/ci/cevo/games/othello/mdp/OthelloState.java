package put.ci.cevo.games.othello.mdp;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import put.ci.cevo.games.TwoPlayerGameState;
import put.ci.cevo.games.board.BoardState;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.OthelloBoard;

public class OthelloState implements TwoPlayerGameState, BoardState, Serializable {

	private static final long serialVersionUID = -6286363206021152568L;

	private final OthelloBoard board;
	private final int playerToMove;

	public OthelloState(OthelloBoard board, int playerToMove) {
		Preconditions.checkArgument(playerToMove == OthelloBoard.BLACK || playerToMove == OthelloBoard.WHITE);
		this.playerToMove = playerToMove;
		this.board = board;
	}

	public OthelloState(OthelloState other) {
		this(other.board, other.getPlayerToMove())	;
	}

	/** Standard Othello Board position with black playing first **/
	public OthelloState() {
		this(new OthelloBoard(), OthelloBoard.BLACK);
	}

	@Override
	public double[] getFeatures() {
		return board.getFeatures();
	}

	public OthelloBoard getBoard() {
		return board;
	}

	@Override
	public String toString() {
		return "Player to move: " + playerToMove + "\n" + board.toString();
	}

	public int getDepth() {
		return BoardUtils.countDepth(board);
	}

	public OthelloState clone() {
		return new OthelloState(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		OthelloState that = (OthelloState) o;

		return playerToMove == that.playerToMove && board.equals(that.board);

	}

	@Override
	public int hashCode() {
		int result = board.hashCode();
		result = 31 * result + playerToMove;
		return result;
	}

	@Override
	public int getPlayerToMove() {
		return playerToMove;
	}

	@Override
	public boolean isFirstPlayerToMove() {
		return playerToMove == OthelloBoard.BLACK;
	}
}
