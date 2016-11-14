package put.ci.cevo.games.connect4;

import java.io.Serializable;

import put.ci.cevo.games.InvertibleTwoPlayerBoardGameState;
import put.ci.cevo.games.board.BoardUtils;

public class Connect4State implements InvertibleTwoPlayerBoardGameState, Serializable {

	private static final long serialVersionUID = -8501715510623454420L;

	private final Connect4Board board;
	private int playerToMove;

	public Connect4State(Connect4Board board, int playerToMove) {
		this.playerToMove = playerToMove;
		this.board = board;
	}

	@Override
	public double[] getFeatures() {
		return BoardUtils.getValues(board);
	}

	@Override
	public Connect4Board getBoard() {
		return board;
	}

	@Override
	public String toString() {
		return "( " + playerToMove + " ,\n" + board.toString() + " )";
	}

	public Connect4State makeMove(int col) {
		Connect4Board newBoard = board.clone();
		return new Connect4State(newBoard.makeMove(col, getPlayerToMove()), Connect4Board.opponent(getPlayerToMove()));
	}

	public boolean isTerminal() {
		return board.isGameOver();
	}

	@Override
	public int getPlayerToMove() {
		return playerToMove;
	}

	@Override
	public boolean isFirstPlayerToMove() {
		return playerToMove == Connect4Board.BLACK;
	}

	@Override
	public void invert() {
		board.invert();
		playerToMove = Connect4Board.opponent(playerToMove);
	}
}
