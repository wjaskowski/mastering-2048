package put.ci.cevo.games.othello;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;

public class OthelloBoard implements Board, Serializable {

	private static final long serialVersionUID = -644521001307920823L;

	private static final int N = 8;
	public static final RectSize SIZE = new RectSize(N);

	public static final int NUM_VALUES = 3; // i.e., EMPTY, WHITE, BLACK
	public static final int NUM_FIELDS = N * N;

	public static final int MARGIN_WIDTH = BoardUtils.MARGIN_WIDTH;
	static final int WIDTH = N + 2 * MARGIN_WIDTH;
	static final int WALL = -2;

	public static final int DIRS[] = { -WIDTH - 1, -WIDTH, -WIDTH + 1, -1, +1, WIDTH - 1, WIDTH, WIDTH + 1 };
	public static final int BUFFER_SIZE = WIDTH * WIDTH;

	public final int[] buffer;

	public OthelloBoard() {
		buffer = new int[BUFFER_SIZE];
		initBoard();
	}

	public OthelloBoard(int[][] board) {
		assert board.length == N;
		buffer = new int[BUFFER_SIZE];
		initMargins();
		for (int r = 0; r < board.length; r++) {
			assert board[r].length == N;
			for (int c = 0; c < board[r].length; c++) {
				setValue(r, c, board[r][c]);
			}
		}
	}

	public OthelloBoard inverted() {
		OthelloBoard board = this.clone();
		board.invert();
		return board;
	}

	@Override
	public void invert() {
		for (int i = 0; i < BUFFER_SIZE; ++i) {
			if (BoardUtils.isValidPosition(i, N)) {
				if (buffer[i] != EMPTY) {
					buffer[i] = opponent(buffer[i]);
				}
			}
		}
	}

	private OthelloBoard(int[] buffer) {
		Preconditions.checkArgument(buffer.length == BUFFER_SIZE);
		this.buffer = buffer.clone();
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
		setValue(3, 3, Board.WHITE);
		setValue(3, 4, Board.BLACK);
		setValue(4, 4, Board.WHITE);
		setValue(4, 3, Board.BLACK);
		initMargins();
	}

	@Override
	public int getWidth() {
		return N;
	}

	@Override
	public int getHeight() {
		return N;
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

	public boolean isEmpty(int pos) {
		return buffer[pos] == EMPTY;
	}

	boolean isInBoard(int pos) {
		return buffer[pos] != WALL;
	}

	boolean isWall(int pos) {
		return buffer[pos] == WALL;
	}

	/*
	 * Return the list of positions that will be changed if a move is performed by a player or null if move is invalid.
	 * Move is a position (but be careful: it is not 0-based, because of margins)
	 * @param player is either Board.BLACK or Board.WHITE
	 */
	public IntArrayList simulateMove(int move, int player) {
		assert player == Board.BLACK || player == Board.WHITE;
		if (!isEmpty(move)) {
			return null;
		}

		IntArrayList positionsChanged = new IntArrayList();
		for (int dir : DIRS) {
			int pos = move + dir;

			while (buffer[pos] == opponent(player)) {
				pos += dir;
			}

			if (buffer[pos] == player) {
				pos -= dir;
				while (buffer[pos] == opponent(player)) {
					positionsChanged.add(pos);
					pos -= dir;
				}
			}
		}

		boolean moveIsValid = positionsChanged.size() > 0;
		if (!moveIsValid) {
			return null;
		}
		positionsChanged.add(move);
		return positionsChanged;
	}

	/** Margin-based. */
	public boolean isValidMove(int move, int player) {
		if (!isEmpty(move)) {
			return false;
		}

		for (int dir : DIRS) {
			int pos = move + dir;

			while (buffer[pos] == opponent(player)) {
				pos += dir;
			}

			if (buffer[pos] == player && buffer[pos - dir] == opponent(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param move
	 *            Must be a valid move
	 */
	public void makeMove(int move, int player) {
		IntArrayList piecesToMove = simulateMove(move, player);
		makeMove(piecesToMove, player);
	}

	public void makeMove(IntArrayList piecesToMove, int player) {
		assert player == Board.BLACK || player == Board.WHITE;

		for (int i = 0; i < piecesToMove.size(); i++) {
			int pos = piecesToMove.buffer[i];
			buffer[pos] = player;
		}
	}

	/**
	 * Watch out: this position is margin-based (not 0-based)
	 *
	 * TODO: Should be package private
	 */
	public static int toPos(int row, int col) {
		return (row + 1) * WIDTH + (col + 1);
	}

	public static int opponent(int player) {
		assert player == WHITE || player == BLACK;
		return NUM_VALUES - 1 - player;
	}

	public static String posToString(int pos) {
		int row = BoardUtils.rowFromPos(pos, N);
		int col = BoardUtils.colFromPos(pos, N);
		return (char) ('A' + row) + "" + (col + 1);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		for (int i = 0; i < getWidth(); i++) {
			builder.append((char) ('A' + i));
		}
		builder.append("\n");

		for (int r = 0; r < getWidth(); r++) {
			builder.append(r).append(" ");
			for (int c = 0; c < getWidth(); c++) {
				builder.append(pieceToChar(getValue(r, c)));
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	private char pieceToChar(int piece) {
		if (piece == EMPTY) {
			return '-';
		}
		if (piece == BLACK) {
			return 'b';
		}
		if (piece == WHITE) {
			return 'w';
		}

		throw new IllegalArgumentException("piece");
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
		OthelloBoard other = (OthelloBoard) obj;
		return Arrays.equals(buffer, other.buffer);
	}

	/**
	 * Whether boards are equal in respect to symmetries
	 * TODO: Could be generalized to GenericBoard and any symmetry kind
	 */
	public boolean equalsBySymmetry(OthelloBoard board) {
		RotationMirrorSymmetryExpander expander = new RotationMirrorSymmetryExpander(OthelloBoard.SIZE);
		List<int[][]> symmetries = BoardUtils.createSymmetricBoards(board, expander);
		for (int[][] symmetric : symmetries) {
			if (this.equals(new OthelloBoard(symmetric))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(buffer);
	}

	public String toJavaArrayString() {
		StringBuilder str = new StringBuilder();
		str.append("new int[][] {\n");
		for (int r = 0; r < N; r++) {
			str.append("{");
			for (int c = 0; c < N; c++) {
				str.append(getValue(r, c)).append(",");
			}
			str.append("},\n");
		}
		str.append("}");
		return str.toString();
	}

	@Override
	public OthelloBoard clone() {
		return new OthelloBoard(this.buffer);
	}

	@Override
	public OthelloBoard createAfterState(int row, int col, int player) {
		OthelloBoard clonedBoard = clone();
		clonedBoard.makeMove(toPos(row, col), player);
		return clonedBoard;
	}

	public double[] getFeatures() {
		int rows = getHeight();
		int cols = getWidth();
		double[] values = new double[rows * cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				values[r * cols + c] = getValue(r, c) - 1; // -1, since our features should be -1, 0 and 1 instead of 0,
														   // 1 and 2.
			}
		}
		return values;
	}

	public static double playerValue(int player) {
		int result = player - 1; // according to the current BLACK and WHITE values
		assert Math.abs(result) == 1;
		return result;
	}

	public List<OthelloBoard> createSymmetricBoards() {
		List<int[][]> symmetricBoards = BoardUtils
			.createSymmetricBoards(this, new RotationMirrorSymmetryExpander(SIZE));

		List<OthelloBoard> boards = new ArrayList<>();
		for (int[][] symmetricBoard : symmetricBoards) {
			boards.add(new OthelloBoard(symmetricBoard));
		}
		return boards;
	}
}
