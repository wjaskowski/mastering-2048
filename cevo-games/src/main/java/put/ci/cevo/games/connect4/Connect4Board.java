package put.ci.cevo.games.connect4;

import static put.ci.cevo.games.board.BoardUtils.pieceToChar;

import java.util.Arrays;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.connect4.thill.c4.ConnectFour;

public class Connect4Board implements Board {

	//TODO: Use only BOARD_SIZE
	public static final int BOARD_HEIGHT = 6;
	public static final int BOARD_WIDTH = 7;
	public static final RectSize SIZE = new RectSize(BOARD_HEIGHT, BOARD_WIDTH);

	public static final RectSize BOARD_SIZE = new RectSize(BOARD_HEIGHT, BOARD_WIDTH);

	public static final int NUM_VALUES = 3; // i.e., EMPTY, WHITE, BLACK

	static final int MARGIN_WIDTH = BoardUtils.MARGIN_WIDTH;
	static final int WIDTH = BOARD_WIDTH + 2 * MARGIN_WIDTH;
	static final int HEIGHT = BOARD_HEIGHT + 2 * MARGIN_WIDTH;

	public static final int BUFFER_SIZE = WIDTH * HEIGHT;
	static final int WALL = -2;
	static final int SYM_DIRS[][] = { { -WIDTH - 1, WIDTH + 1 }, { -WIDTH, WIDTH }, { -WIDTH + 1, WIDTH - 1 }, { -1, 1 } };

	final int[] buffer;
	final int[] colHeights;

	private boolean gameOver = false;
	private int winner = -1;

	public Connect4Board() {
		this.buffer = new int[BUFFER_SIZE];
		this.colHeights = new int[BOARD_WIDTH];
		initBoard();
	}

	public Connect4Board(int[][] board) {
		Preconditions.checkArgument(board.length == BOARD_HEIGHT);
		this.buffer = new int[BUFFER_SIZE];
		this.colHeights = new int[BOARD_WIDTH];
		initMargins();
		for (int r = 0; r < board.length; r++) {
			Preconditions.checkArgument(board[r].length == BOARD_WIDTH);
			for (int c = 0; c < board[r].length; c++) {
				initBoardCell(r, c, board[r][c]);
			}
		}
	}

	public Connect4Board(double[] board, int rows, int cols) {
		this.buffer = new int[BUFFER_SIZE];
		this.colHeights = new int[BOARD_WIDTH];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				initBoardCell(r, c, (int) board[r * cols + c]);
			}
		}

	}

	private Connect4Board(int[] buffer, int[] colHeights, boolean gameOver, int winner) {
		Preconditions.checkArgument(buffer.length == BUFFER_SIZE);
		this.buffer = buffer.clone();
		this.colHeights = colHeights.clone();
		this.winner = winner;
		this.gameOver = gameOver;
	}

	private void initBoard() {
		Arrays.fill(buffer, Board.EMPTY);
		Arrays.fill(colHeights, 0);
		initMargins();
	}

	private void initMargins() {
		for (int col = 0; col < WIDTH; ++col) {
			setValueInternal(0, col, WALL);
			setValueInternal(HEIGHT - 1, col, WALL);
		}
		for (int row = 0; row < HEIGHT; ++row) {
			setValueInternal(row, 0, WALL);
			setValueInternal(row, WIDTH - 1, WALL);

		}
	}

	private void initBoardCell(int row, int col, int color) {
		buffer[toPos(row, col)] = color;
		if (color != EMPTY) {
			colHeights[col]++;
		}
	}

	private void setValueInternal(int row, int col, int color) {
		buffer[toPosInternal(row, col)] = color;
	}

	private static int toPosInternal(int row, int col) {
		return row * WIDTH + col;
	}

	private void updateColHeight(int col, int color) {
		if (color != EMPTY) {
			if (colHeights[col] < BOARD_HEIGHT) {
				colHeights[col]++;
			}
		} else if (colHeights[col] > 0 ) {
			colHeights[col]--;
		}
	}

	@Override
	public int getWidth() {
		return BOARD_WIDTH;
	}

	@Override
	public int getHeight() {
		return BOARD_HEIGHT;
	}

	@Override
	public void setValue(int row, int col, int color) {
		buffer[toPos(row, col)] = color;
		updateColHeight(col, color);

		if (color == EMPTY) {
			if (Connect4.isConnect4(toPos(row, col), BLACK, this) || Connect4.isConnect4(toPos(row, col), WHITE, this)) {
				gameOver = false;
				winner = -1;
			}
		} else {
			if (Connect4.isConnect4(toPos(row, col), color, this)) {
				gameOver = true;
				winner = color;
			}
		}
	}

	private void setValueInv(int row, int col, int color) {
		buffer[toPos(invertRow(row), col)] = color;
		updateColHeight(col, color);

		if (Connect4.isConnect4(toPos(invertRow(row), col), color, this)) {
			gameOver = true;
			winner = color;
		}
	}

	@Override
	public void setValue(int pos, int color) {
		buffer[pos] = color;
		int col = BoardUtils.colFromPos(pos, BOARD_WIDTH);
		updateColHeight(col, color);
//
		if (color == EMPTY) {
			if (Connect4.isConnect4(pos, BLACK, this) || Connect4.isConnect4(pos, WHITE, this)) {
				gameOver = false;
				winner = -1;
			}
		}
	}

	@Override
	public int getValue(int row, int col) {
		return buffer[toPos(row, col)];
	}

	public int getValueInv(int row, int col) {
		return buffer[toPos(invertRow(row), col)];
	}

	@Override
	public int getValue(int pos) {
		return buffer[pos];
	}

	@Override
	public void invert() {
		for (int i = 0; i < BUFFER_SIZE; ++i) {
			if (BoardUtils.isValidPosition(i, BOARD_HEIGHT, BOARD_WIDTH)) {
				if (buffer[i] != EMPTY) {
					buffer[i] = opponent(buffer[i]);
				}
			}
		}
		if (winner != -1)
			winner = opponent(winner);
	}

	@Override
	public Connect4Board createAfterState(int row, int col, int player) {
		Connect4Board clonedBoard = clone();
		clonedBoard.setValue(row, col, player);
		return clonedBoard;
	}

	/**
	 * Makes a move by placing a piece in a given colum (not an encoded position). Piece is always placed in the lowest
	 * possible row as indicated by heights of the columns. Cannot be used to overwrite or delete any other other move.
	 */
	public Connect4Board makeMove(int col, int player) {
		Preconditions.checkArgument(player == Board.BLACK || player == Board.WHITE);
		Preconditions.checkArgument(!gameOver);

		final int pos = moveToPos(col);
		if (!isEmpty(pos)) {
			throw new IllegalStateException("Attempt to override board state with move: " + pos);
		}

		buffer[pos] = player;
		colHeights[col]++;

		if (Connect4.isConnect4(pos, player, this)) {
			gameOver = true;
			winner = player;
		}

		return this;
	}

	public int moveToPos(int col) {
		int row = colHeights[col];
		return toPos(invertRow(row), col);
	}

	/** Inverts a given row so that the highest one becomes the lowest. */
	private int invertRow(int row) {
		return (BOARD_HEIGHT - 1 - row);
	}

	public boolean isEmpty(int row, int col) {
		return isEmpty(toPos(row, col));
	}

	/** Valid moves are columns in which a disk can be placed */
	public IntArrayList getValidMoves() {
		IntArrayList moves = new IntArrayList(BOARD_WIDTH);
		for (int i = 0; i < BOARD_WIDTH; i++) {
			if (isValidMove(i)) {
				moves.add(i);
			}
		}
		return moves;
	}

	public boolean isValidMove(int col) {
		return colHeights[col] < BOARD_HEIGHT;
	}

	/** Returned indices are margin-based */
	public int[] getOccupiedCells(int playerColor) {
		IntArrayList indices = new IntArrayList();
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == playerColor) {
				indices.add(i);
			}
		}
		return indices.toArray();
	}

	public boolean isGameOver() {
		return gameOver || getValidMoves().isEmpty();
	}

	public int getWinner() {
		return winner;
	}

	boolean isEmpty(int pos) {
		return buffer[pos] == EMPTY;
	}

	/**
	 * Watch out: this position is margin-based (not 0-based)
	 */
	static int toPos(int row, int col) {
		return (row + 1) * WIDTH + (col + 1);
	}

	static int opponent(int player) {
		return NUM_VALUES - 1 - player;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Connect4Board other = (Connect4Board) obj;
		return Arrays.equals(buffer, other.buffer);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(buffer);
	}

	@Override
	public Connect4Board clone() {
		return new Connect4Board(this.buffer, this.colHeights, this.gameOver, this.winner);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int r = 0; r < BOARD_HEIGHT; r++) {
			builder.append(r + 1).append(" ");
			for (int c = 0; c < BOARD_WIDTH; c++) {
				builder.append(pieceToChar(getValue(r, c)));
			}
			builder.append("\n");
		}

		builder.append("  ");
		for (int i = 0; i < BOARD_WIDTH; i++) {
			builder.append((char) ('A' + i));
		}
		builder.append("\n");
		return builder.toString();
	}

	public static double playerValue(int player) {
		int result = player - 1; // according to the current BLACK and WHITE values
		assert Math.abs(result) == 1;
		return result;
	}

	public int[][] toThillBoard() {
		final int[][] board = new int[BOARD_WIDTH][BOARD_HEIGHT];
		for (int col = 0; col < BOARD_WIDTH; col++) {
			for (int row = 0; row < BOARD_HEIGHT; row++) {
				int value = getValueInv(row, col);
				if (value == BLACK) {
					board[col][row] = ConnectFour.PLAYER1;
				} else if (value == WHITE) {
					board[col][row] = ConnectFour.PLAYER2;
				}
			}
		}
		return board;
	}

	/** Creates a {@link Connect4Board} from {@link ConnectFour} Thill's board. Keep in mind that Thill's board is 7x6
	 *  and is implicitly transposed to 6x7. Players colors are also encoded to comply with those of {@link Board}.
	 */
	public static Connect4Board fromThillBoard(int[][] thillBoard) {
		Preconditions.checkArgument(thillBoard.length == BOARD_WIDTH);
		final Connect4Board board = new Connect4Board();
		for (int col = 0; col < thillBoard.length; col++) {
			Preconditions.checkArgument(thillBoard[col].length == BOARD_HEIGHT);
			for (int row = 0; row < thillBoard[0].length; row++) {
				if (thillBoard[col][row] == ConnectFour.PLAYER2) {
					board.setValueInv(row, col, WHITE);
				} else if (thillBoard[col][row] == ConnectFour.PLAYER1) {
					board.setValueInv(row, col, BLACK);
				}
			}
		}
		return board;
	}
}
