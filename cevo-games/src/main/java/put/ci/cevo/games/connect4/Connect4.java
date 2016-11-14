package put.ci.cevo.games.connect4;

import static put.ci.cevo.games.connect4.Connect4Board.SYM_DIRS;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardGame;
import put.ci.cevo.games.connect4.players.Connect4Player;

public class Connect4 implements BoardGame<Connect4Player, Connect4Player, Connect4State> {

	@Override
	public GameOutcome play(Connect4Player blackPlayer, Connect4Player whitePlayer, RandomDataGenerator random) {
		return play(blackPlayer, whitePlayer, new Connect4State(new Connect4Board(), Board.BLACK), random);
	}

	@Override
	public GameOutcome play(Connect4Player blackPlayer, Connect4Player whitePlayer, Connect4State initialState,
			RandomDataGenerator random) {
		return play(blackPlayer, whitePlayer, initialState.getBoard(), initialState.getPlayerToMove(), random);
	}

	static GameOutcome play(Connect4Player blackPlayer, Connect4Player whitePlayer, Connect4Board initialBoard,
			int playerToMove, RandomDataGenerator random) {
		Preconditions.checkArgument(playerToMove == Board.BLACK || playerToMove == Board.WHITE);

		Connect4Board board = initialBoard.clone();

		final Connect4Player[] players = playerToMove == Board.BLACK ? new Connect4Player[] { blackPlayer, whitePlayer }
			: new Connect4Player[] { whitePlayer, blackPlayer };
		final int[] playerColors = new int[] { playerToMove, Connect4Board.opponent(playerToMove) };

		boolean movePossible;
		boolean gameOver = false;
		int currentPlayer = playerColors[0];
		do {
			movePossible = false;
			for (int p = 0; p < players.length && !gameOver; p++) {
				Connect4Player player = players[p];
				currentPlayer = playerColors[p];
				int[] validMoves = board.getValidMoves().toArray();
				if (validMoves.length == 0) {
					continue;
				}
				int move = player.getMove(board, currentPlayer, validMoves, random);
				gameOver = isWinningMove(move, currentPlayer, board);
				board.makeMove(move, currentPlayer);
				movePossible = true;
			}
		} while (!gameOver && movePossible);
		// TODO: hardcoded
		if (!movePossible) {
			return new GameOutcome(0.5, 0.5);
		}
		return currentPlayer == Board.BLACK ? new GameOutcome(1, 0) : new GameOutcome(0, 1);
	}

	/**
	 * Checks if the move (column index) is a move leading to the four connected pieces.
	 */
	public static boolean isWinningMove(int move, int playerColor, Connect4Board board) {
		Preconditions.checkArgument(move < Connect4Board.BOARD_WIDTH);
		return isConnect4(board.moveToPos(move), playerColor, board);

	}

	/**
	 * Checks if a given player has four pieces connected and can be declared a winner.
	 */
	static boolean isConnect4(int playerColor, Connect4Board board) {
		for (int pos : board.getOccupiedCells(playerColor)) {
			if (isConnect4(pos, playerColor, board)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if four pieces are inline and passing through the given position (row & col) on the board.
	 */
	static boolean isConnect4(int pos, int playerColor, Connect4Board board) {
		// search for the line of 4 connected pieces, in each iteration we consider two symmetric directions
		for (int dirs[] : SYM_DIRS) {
			// one piece is connected at the beginning of the search
			int numConnected = 1;
			for (int i = 0; i < 2; i++) {
				int idx = pos + dirs[i];
				if (board.buffer[idx] != playerColor) {
					continue;
				}
				// Move along the line while there are pieces of playerColor
				do {
					numConnected++;
					idx += dirs[i];
				} while (board.buffer[idx] == playerColor);
				// if there are 4 or more connected pieces, we have a winner else check the symmetric direction
				if (numConnected >= 4) {
					return true;
				}
			}
		}
		return false;
	}

}
