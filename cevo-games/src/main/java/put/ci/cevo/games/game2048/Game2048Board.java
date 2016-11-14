package put.ci.cevo.games.game2048;

import java.io.Serializable;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.board.RectSize;

public class Game2048Board implements Board, Serializable {

	private static final long serialVersionUID = -644521001307920823L;

	public static final int SIZE = State2048.SIZE;
	public static final RectSize RECT = new RectSize(SIZE);
	public static final int NUM_VALUES = State2048.getNumValues();
	public static final int NUM_FIELDS = SIZE * SIZE;

	static final int MARGIN_WIDTH = BoardUtils.MARGIN_WIDTH;
	static final int WIDTH = SIZE + 2 * MARGIN_WIDTH;
	static final int BUFFER_SIZE = WIDTH * WIDTH;
	static final int WALL = -2;

	final int[] buffer;

	public Game2048Board() {
		buffer = new int[BUFFER_SIZE];
		initBoard();
	}

	public Game2048Board(int[][] board) {
		assert board.length == SIZE;
		buffer = new int[BUFFER_SIZE];
		initMargins();
		for (int r = 0; r < board.length; r++) {
			assert board[r].length == SIZE;
			for (int c = 0; c < board[r].length; c++) {
				setValue(r, c, board[r][c]);
			}
		}
	}

	private Game2048Board(int[] buffer) {
		Preconditions.checkArgument(buffer.length == BUFFER_SIZE);
		this.buffer = buffer.clone();
	}

	public Game2048Board(double[] input) {
		Preconditions.checkArgument(input.length == SIZE * SIZE);
		this.buffer = new int[BUFFER_SIZE];
		initMargins();
		for (int r = 0; r < SIZE; ++r) {
			for (int c = 0; c < SIZE; c++) {
				setValue(r, c, inputToBoardValue(input, r, c));
			}
		}
	}

	private static int inputToBoardValue(double[] input, int r, int c) {
		return (int) (input[r * SIZE + c] + 0.5);
	}

	private void initMargins() {
		for (int i = 0; i < WIDTH; ++i) {
			setValueInternal(0, i, WALL);
			setValueInternal(WIDTH - 1, i, WALL);
			setValueInternal(i, 0, WALL);
			setValueInternal(i, WIDTH - 1, WALL);
		}
	}

	private static int toPosInternal(int row, int col) {
		return row * WIDTH + col;
	}

	private void initBoard() {
		Arrays.fill(buffer, Board.EMPTY);
		initMargins();
	}

	public int getSize() {
		return SIZE;
	}

	@Override
	public void setValue(int row, int col, int color) {
		buffer[toPos(row, col)] = color;
	}

	@Override
	public void setValue(int pos, int color) {
		buffer[pos] = color;
	}

	private void setValueInternal(int row, int col, int color) {
		buffer[toPosInternal(row, col)] = color;
	}

	@Override
	public int getValue(int row, int col) {
		return buffer[toPos(row, col)];
	}

	@Override
	public int getValue(int pos) {
		return buffer[pos];
	}

	public boolean isEmpty(int row, int col) {
		return isEmpty(toPos(row, col));
	}

	boolean isEmpty(int pos) {
		return buffer[pos] == EMPTY;
	}

	boolean isInBoard(int pos) {
		return buffer[pos] != WALL;
	}

	boolean isWall(int pos) {
		return buffer[pos] == WALL;
	}

	/**
	 * Watch out: this position is margin-based (not 0-based)
	 * 
	 * TODO: Should be package private
	 */
	public static int toPos(int row, int col) {
		return (row + 1) * WIDTH + (col + 1);
	}

	public static String posToString(int pos) {
		int row = BoardUtils.rowFromPos(pos, SIZE);
		int col = BoardUtils.colFromPos(pos, SIZE);
		return (char) ('A' + row) + "" + (col + 1);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		/*builder.append("  ");
		for (int i = 0; i < getSize(); i++) {
			builder.append((char) ('A' + i));
		}*/
		builder.append("\n");

		for (int r = 0; r < getSize(); r++) {
			//builder.append(r).append(" ");
			for (int c = 0; c < getSize(); c++) {
				builder.append(String.format("%2d", getValue(r, c)));
			}
			builder.append("\n");
		}
		return builder.toString();
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
		Game2048Board other = (Game2048Board) obj;
		return Arrays.equals(buffer, other.buffer);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(buffer);
	}

	public String toJavaArrayString() {
		StringBuilder str = new StringBuilder();
		str.append("new int[][] {\n");
		for (int r = 0; r < SIZE; r++) {
			str.append("{");
			for (int c = 0; c < SIZE; c++) {
				str.append(getValue(r, c)).append(",");
			}
			str.append("},\n");
		}
		str.append("}");
		return str.toString();
	}

	@Override
	public Game2048Board clone() {
		return new Game2048Board(this.buffer);
	}

	@Override
	public Board createAfterState(int row, int col, int player) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getWidth() {
		return getSize();
	}

	@Override
	public int getHeight() {
		return getSize();
	}

	public long asLong() {
		final int BASE = 16;
		long total = 0;
		for (int row = 0; row < SIZE; ++row) {
			for (int col = 0; col < SIZE; ++col) {
				total *= BASE;
				total += getValue(row, col);
			}
		}
		return total;
	}
}
