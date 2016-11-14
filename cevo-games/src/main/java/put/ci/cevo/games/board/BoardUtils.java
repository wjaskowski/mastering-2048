package put.ci.cevo.games.board;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryUtils;

import java.util.*;

import static put.ci.cevo.games.board.Board.BLACK;
import static put.ci.cevo.games.board.Board.WHITE;

public final class BoardUtils {
	// TODO: Most of the methods in this class could operate on BoardSize class
	// Some methods could be members of Board
	// TODO: Made cohesive names: cols/rows vs. boardWidth/boardHeight

	public static final int MARGIN_WIDTH = 1;
	public static final int TOTAL_MARGIN = 2 * MARGIN_WIDTH;

	private BoardUtils() {
		// A static class
	}

	public static double[] getValues(Board board) {
		int rows = board.getHeight();
		int cols = board.getWidth();
		double[] values = new double[rows * cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				values[r * cols + c] = board.getValue(r, c);
			}
		}
		return values;
	}

	// TODO: Remove it from here. It is game dependent!
	public static int countDepth(Board board) {
		int count = 0;
		int rows = board.getHeight();
		int cols = board.getWidth();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (board.getValue(r, c) != Board.EMPTY) {
					count += 1;
				}
			}
		}
		return count - 4;
	}

	/**
	 * @return Number of pieces on the board
	 */

	public static int countPieces(Board board) {
		return countPieces(board, ImmutableSet.of(BLACK, WHITE));
	}

	public static int countPieces(Board board, int color) {
		return countPieces(board, Collections.singleton(color));
	}

	/**
	 * @return Number of pieces of given colors on the board
	 */
	public static int countPieces(Board board, Set<Integer> colors) {
		int count = 0;
		int rows = board.getHeight();
		int cols = board.getWidth();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (colors.contains(board.getValue(r, c))) {
					count += 1;
				}
			}
		}
		return count;
	}

	/**
	 * Returned position is margin-based (not 0-based)
	 *
	 * row and col are 0-based
	 */
	// TODO: Make it deprecated?
	public static int toMarginPos(int boardWidth, int row, int col) {
		return toMarginPos(new RectSize(boardWidth), new BoardPos(row, col));
	}

	/**
	 * Returned position is margin-based (not 0-based)
	 */
	// TODO: Make it deprecated?
	public static int toMarginPos(RectSize boardSize, int row, int col) {
		assert 0 <= row && row < boardSize.rows();
		assert 0 <= col && col < boardSize.columns();
		return toMarginPos(boardSize, new BoardPos(row, col));
	}

	public static int toMarginPos(RectSize boardSize, int zeroBasedPos) {
		return toMarginPos(boardSize, new BoardPos(zeroBasedPos, boardSize));
	}

	/**
	 * Returned position is margin-based (not 0-based)
	 */
	public static int toMarginPos(RectSize boardSize, BoardPos pos) {
		return (pos.row() + 1) * (boardSize.width() + TOTAL_MARGIN) + (pos.column() + 1);
	}

	/**
	 * Converts margin-based position to 0-based position (like in Othello League). Square board assumed
	 **/
	public static int marginPosToPos(int pos, int boardSize) {
		return marginPosToPos(pos, boardSize, boardSize);
	}

	/**
	 * Converts margin-based position to 0-based position (like in Othello League)
	 **/
	public static int marginPosToPos(int pos, int rows, int cols) {
		Preconditions.checkArgument(isValidPosition(pos, rows, cols));
		int row = rowFromPos(pos, cols);
		int col = colFromPos(pos, cols);
		return row * cols + col;
	}

	/**
	 * Checks whether position is a valid margin-based board position (inside board)
	 *
	 * @param pos
	 *            margin-based position
	 * @param boardSize
	 *            board size without margins (e.g., 8 for Othello)
	 *
	 *            Assuming square board
	 */
	public static boolean isValidPosition(int pos, int boardSize) {
		return isValidPosition(pos, boardSize, boardSize);
	}

	public static boolean isValidPosition(int pos, int rows, int cols) {
		int row = rowFromPos(pos, cols);
		int col = colFromPos(pos, cols);
		return 0 <= row && row < rows && 0 <= col && col < cols;
	}

	/**
	 * @param pos
	 *            is margin-based position
	 * @return 0-based row
	 */
	public static int rowFromPos(int pos, int boardWidth) {
		return pos / (boardWidth + TOTAL_MARGIN) - 1;
	}

	/**
	 * @param pos
	 *            is margin-based position
	 * @return 0-based column (i.e. the first column is 0, the last is boardSize-1)
	 */
	public static int colFromPos(int pos, int boardWidth) {
		return pos % (boardWidth + TOTAL_MARGIN) - 1;
	}

	public static char pieceToChar(int piece) {
		if (piece == Board.EMPTY) {
			return '-';
		}
		if (piece == BLACK) {
			return 'b';
		}
		if (piece == WHITE) {
			return 'w';
		}

		assert false;
		return '?';
	}

	public static String posToString(int pos, Board board) {
		int row = BoardUtils.rowFromPos(pos, board.getWidth());
		int col = BoardUtils.colFromPos(pos, board.getWidth());
		return (char) ('A' + row) + "" + (col + 1);
	}

	public static String toString(Board board) {
		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		for (int i = 0; i < board.getWidth(); i++) {
			builder.append((char) ('A' + i));
		}
		builder.append("\n");

		for (int r = 0; r < board.getHeight(); r++) {
			builder.append(r + 1 + " ");
			for (int c = 0; c < board.getWidth(); c++) {
				builder.append(BoardUtils.pieceToChar(board.getValue(r, c)));
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	/**
	 * For a given board, create symmetric ones according to the expander
	 */
	public static List<int[][]> createSymmetricBoards(Board board, SymmetryExpander expander) {
		IntArrayList fakeNTuple = new IntArrayList();
		for (int r = 0; r < board.getHeight(); ++r)
			for (int c = 0; c < board.getWidth(); ++c)
				fakeNTuple.add(toMarginPos(board.getWidth(), r, c));

		List<int[][]> symmetricBoards = new ArrayList<>();

		List<int[]> symmetries = SymmetryUtils.createSymmetric(fakeNTuple.toArray(), expander);
		for (int[] symmetric : symmetries) {
			int[][] newBoard = new int[board.getHeight()][board.getWidth()];
			for (int r = 0; r < board.getHeight(); ++r)
				for (int c = 0; c < board.getWidth(); ++c)
					newBoard[r][c] = board.getValue(symmetric[board.getWidth() * r + c]);
			symmetricBoards.add(newBoard);
		}
		return symmetricBoards;
	}

	public static int[][] boardFromString(String string, int height, int width) {
		return boardFromString(string, new RectSize(height, width));
	}

	public static int[][] boardFromString(String string, RectSize size) {
		final Scanner scanner = new Scanner(string);
		int[][] board = new int[size.height()][size.width()];
		for (int i = 0; i < size.height() ; i++) {
			for (int j = 0; j < size.width(); ++j) {
				board[i][j] = scanner.nextInt();
			}
		}
		return board;
	}


}
