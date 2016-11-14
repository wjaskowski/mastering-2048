package put.ci.cevo.games.tetris;

import com.carrotsearch.hppc.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.util.Pair;

import java.util.Arrays;

import static java.lang.Math.min;

public class TetrisBoard implements Board {

	public static final int EMPTY = 0;
	public static final int PIECE = 1;
	public static final int NUM_VALUES = 2;

	public static final int MARGIN_WIDTH = BoardUtils.MARGIN_WIDTH;
	public static final int BOARD_WIDTH = 10;
	public static final int BOARD_HEIGHT = 20;

	public static final int WIDTH = 10 + 2 * MARGIN_WIDTH;
	public static final int HEIGHT = 20 + 2 * MARGIN_WIDTH;
	public static final RectSize DEFAULT_BOARD_SIZE = new RectSize(BOARD_HEIGHT, BOARD_WIDTH);

	static final int BUFFER_SIZE = HEIGHT * WIDTH;

	private static final int TEMP_PIECE = -1;
	private static final char[] chars = "# XSZLZTIO".toCharArray();

	/** internal board representation */
	final int[] buf;
	/** skyline is meant as the height of particular columns */
	final int[] skyline;
	/** holds number of pieces in each row */
	final int[] rowWidth;

	private int maxColumnHeight;
	private boolean heightExceeded;

	public static class SimulationResult {
		public final IntArrayList changedPositions;
		public final int reward;
		public final boolean isTerminal;

		public SimulationResult(IntArrayList changedPositions, int reward, boolean isTerminal) {
			this.changedPositions = changedPositions;
			this.reward = reward;
			this.isTerminal = isTerminal;
		}
	}

	public TetrisBoard() {
		this.buf = new int[BUFFER_SIZE];
		this.skyline = new int[BOARD_WIDTH];
		this.rowWidth = new int[BOARD_HEIGHT];
	}

	private TetrisBoard(TetrisBoard board) {
		this.maxColumnHeight = board.maxColumnHeight;
		this.heightExceeded = board.heightExceeded;

		this.skyline = new int[board.skyline.length];
		System.arraycopy(board.skyline, 0, skyline, 0, board.skyline.length);

		this.rowWidth = new int[board.rowWidth.length];
		System.arraycopy(board.rowWidth, 0, rowWidth, 0, board.rowWidth.length);

		this.buf = new int[BUFFER_SIZE];
		System.arraycopy(board.buf, 0, buf, 0, board.buf.length);
	}

	/** Internally transform row and col to pos. No margin here, however internal padding is taken into account. */
	private static int toPos(int row, int col) {
		return (row + MARGIN_WIDTH) * WIDTH + (col + MARGIN_WIDTH);
	}

	public RectSize getSize() {
		return DEFAULT_BOARD_SIZE;
	}

	public int getMaxColumnHeight() {
		return maxColumnHeight;
	}

	public int[] getSkyline() {
		return skyline;
	}

	public boolean isHeightExceeded() {
		return heightExceeded;
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
	public int getValue(int row, int col) {
		return buf[toPos(row, col)];
	}

	@Override
	public void setValue(int row, int col, int color) {
		buf[toPos(row, col)] = color;
	}

	@Override
	public int getValue(int pos) {
		return buf[pos];
	}

	@Override
	public void setValue(int pos, int color) {
		buf[pos] = color;
	}

	@Override
	public void invert() {
		throw new IllegalStateException("Does not make any sense here");
	}

	@Override
	public Board createAfterState(int row, int col, int player) {
		throw new IllegalStateException("Does not make any sense here");
	}

	@Override
	public TetrisBoard clone() {
		return new TetrisBoard(this);
	}

	/**
	 * Places a tetromino in the given column and returns a list of affected positions in the margin-based format
	 */
	public IntArrayList placeTetromino(Tetromino tetromino, int column, int rotation) {
		return placeTetrominoCustom(tetromino, column, rotation, PIECE);
	}

	/**
	 * With variable piece - used for animation only
	 */
	public IntArrayList placeTetrominoCustom(Tetromino tetromino, int column, int rotation, int piece) {
		int landingHeight = calculateLandingHeight(tetromino, column, rotation);
		return placeTetromino(tetromino, column, rotation, landingHeight, piece);
	}

	IntArrayList placeTetromino(Tetromino tetromino, int column, int rotation, int landingHeight) {
		return placeTetromino(tetromino, column, rotation, landingHeight, PIECE);
	}

	private IntArrayList placeTetromino(Tetromino tetromino, int column, int rotation, int landingHeight, int piece) {
		IntArrayList positions = new IntArrayList(4);
		if (landingHeight > BOARD_HEIGHT) {
			heightExceeded = true;
			// The game ends immediately, so we assume no change thus return []
			return positions;
		}

		int[][] tiles = tetromino.getTiles(rotation);
		for (int tileRow = 0; tileRow < tiles.length; tileRow++) {
			for (int tileCol = 0; tileCol < tiles[tileRow].length; tileCol++) {
				if (tiles[tileRow][tileCol] != 0) {
					final int boardCol = column + tileCol;
					final int boardRow = BOARD_HEIGHT - landingHeight + tileRow;

					placePiece(landingHeight - tileRow, piece, boardCol, boardRow);
					positions.add(toPos(boardRow, boardCol));
				}
			}
		}
		return positions;
	}

	private void placePiece(int landingHeight, int piece, int boardCol, int boardRow) {
		setValue(boardRow, boardCol, piece);

		if (skyline[boardCol] < landingHeight) {
			skyline[boardCol] = landingHeight;
		}

		if (maxColumnHeight < skyline[boardCol]) {
			maxColumnHeight = skyline[boardCol];
		}
		rowWidth[boardRow]++;
	}

	/**
	 * Computes list of all margin-based positions that would change if the given tetromino was placed on the board.
	 * <p>
	 * Slightly slower but much more concise
	 *
	 * @return the list and the reward
	 */
	public Pair<IntArrayList, Integer> simulatePlaceTetrominoNaive(Tetromino tetromino, int column, int rotation) {
		TetrisBoard simulated = this.clone();
		IntArrayList changedPositions = simulated.placeTetromino(tetromino, column, rotation);
		int simulatedMaxColHeight = simulated.getMaxColumnHeight();

		IntSortedSet erased = simulated.eraseCompleteLines(changedPositions);
		if (erased.isEmpty()) {
			return Pair.create(changedPositions, 0);
		}

		int lowerBound = toPos(BOARD_HEIGHT - simulatedMaxColHeight, 0);
		int upperBound = min(toPos(erased.lastInt() + 3, BOARD_WIDTH - 1),
				toPos(BOARD_HEIGHT - 1, BOARD_WIDTH - 1));

		IntArrayList diffs = new IntArrayList();
		for (int pos = lowerBound; pos <= upperBound; pos++) {
			if (getValue(pos) != simulated.getValue(pos)) {
				diffs.add(pos);
			}
		}
		return Pair.create(diffs, erased.size());
	}

	/**
	 * Computes list of all margin-based positions that would change if the given tetromino was placed on the board.
	 *
	 * @return the list and the reward
	 */
	public SimulationResult simulatePlaceTetromino(Tetromino tetromino, int column, int rotation) {
		final IntArrayList changed = new IntArrayList();
		final int landingHeight = calculateLandingHeight(tetromino, column, rotation);
		if (landingHeight > BOARD_HEIGHT) {
			return new SimulationResult(changed, 0, true);
		}

		final IntArrayList posToUndo = new IntArrayList(4);
		final IntAVLTreeSet rowsToErase = new IntAVLTreeSet();

		int newMaxColumnHeight = maxColumnHeight;
		final int[][] tiles = tetromino.getTiles(rotation);
		for (int tileRow = 0; tileRow < tiles.length; tileRow++) {
			for (int tileCol = 0; tileCol < tiles[tileRow].length; tileCol++) {
				if (tiles[tileRow][tileCol] != 0) {
					final int boardCol = column + tileCol;
					final int boardRow = BOARD_HEIGHT - landingHeight + tileRow;

					// the tile is marked as changed, however it might change later
					int pos = toPos(boardRow, boardCol);
					changed.add(pos);

					// temporarily place the tile
					setValue(pos, TEMP_PIECE);
					posToUndo.add(pos);
					rowWidth[boardRow]++;
					int newSkyline = skyline[boardCol] < landingHeight - tileRow ? landingHeight - tileRow : skyline[boardCol];
					if (newSkyline > newMaxColumnHeight) {
						newMaxColumnHeight = newSkyline;
					}

					// check for full rows
					if (rowWidth[boardRow] == BOARD_WIDTH) {
						rowsToErase.add(boardRow);
					}
				}
			}
		}

		if (!rowsToErase.isEmpty()) {
			final int startRow = BOARD_HEIGHT - newMaxColumnHeight;
			final int endRow = rowsToErase.lastInt();

			boolean isGroup = false;
			int rowsInGroup = 0;

			int lastDeletedRow = Integer.MIN_VALUE;
			int lastGroupSize = Integer.MIN_VALUE;
			int numRowsToDelete = rowsToErase.size();

			for (int row = startRow; row <= endRow; row++) {
				int rowsToLookAbove = numRowsToDelete;
				if (rowsToErase.contains(row)) {
					rowsInGroup += 1;
					isGroup = true;
				} else if (isGroup) {
					lastGroupSize = rowsInGroup;
					rowsToLookAbove -= rowsInGroup;
					numRowsToDelete -= rowsInGroup;
					lastDeletedRow = row - 1;
					rowsInGroup = 0;
					isGroup = false;
				}

				int rowAbove = row - rowsToLookAbove;
				if (rowAbove <= lastDeletedRow) {
					rowsToLookAbove += lastGroupSize;
				}

				for (int pos = toPos(row, 0); pos <= toPos(row, BOARD_WIDTH - 1); pos++) {
					int posAbove = pos - rowsToLookAbove * WIDTH;
					checkPositionChanged(changed, pos, posAbove);
				}
			}
		}
		undoBoardChanges(posToUndo);

		return new SimulationResult(changed, rowsToErase.size(), false);
	}

	/**
	 * Checks if the given position changes as a result of deleting lines.
	 */
	private void checkPositionChanged(IntArrayList changed, int pos, int posAbove) {
		// posAbove is out of the board
		if (posAbove < 0) {
			if (buf[pos] == PIECE) {
				changed.add(pos);
			} else if (buf[pos] == TEMP_PIECE) {
				changed.removeFirstOccurrence(pos);
			}
			return;
		}
		if (buf[pos] == TEMP_PIECE && buf[posAbove] != TEMP_PIECE) {
			changed.removeFirstOccurrence(pos);
		} else if ((buf[pos] == EMPTY && buf[posAbove] != EMPTY) || (buf[pos] != EMPTY && buf[posAbove] == EMPTY)) {
			changed.add(pos);
		}
	}

	/**
	 * Undo placing the tetromino and revert width of the rows
	 */
	private void undoBoardChanges(IntArrayList posToUndo) {
		final int[] buffer = posToUndo.buffer;
		for (int i = 0; i < posToUndo.size(); i++) {
			setValue(buffer[i], EMPTY);
			rowWidth[BoardUtils.rowFromPos(buffer[i], BOARD_WIDTH)]--;
		}
	}

	/**
	 * @return the height of the highest column after placing a given tetromino (but only for columns were the tetromino
	 * is be placed)
	 */
	int calculateLandingHeight(Tetromino tetromino, int position, int rotation) {
		int highestColumn = Integer.MIN_VALUE;
		int[] bottomTiles = tetromino.getBottomTiles(rotation);
		for (int col = 0; col < bottomTiles.length; col++) {
			int height = skyline[position + col] + bottomTiles[col];
			highestColumn = Math.max(highestColumn, height);
		}
		return highestColumn;
	}

	/**
	 * Takes as input an array of margin-based positions where last tetromino was placed and returns a sorted set of
	 * rows.
	 */
	public IntSortedSet eraseCompleteLines(IntArrayList changedPositions) {
		IntAVLTreeSet erased = new IntAVLTreeSet();
		for (int i = 0; i < changedPositions.size(); i++) {
			int rowToErase = BoardUtils.rowFromPos(changedPositions.get(i), BOARD_WIDTH);
			if (rowWidth[rowToErase] == BOARD_WIDTH) {
				int highestRow = BOARD_HEIGHT - maxColumnHeight;
				int pos = BoardUtils.toMarginPos(DEFAULT_BOARD_SIZE, highestRow, 0);

				// there is nothing above the row, simply fill it with zeroes as nothing needs to be copied
				if (rowToErase == highestRow) {
					Arrays.fill(buf, pos, pos + BOARD_WIDTH, EMPTY);
				} else {
					// copy pieces just one row below
					int newPos = BoardUtils.toMarginPos(DEFAULT_BOARD_SIZE, highestRow + 1, 0);
					System.arraycopy(buf, pos, buf, newPos, WIDTH * (rowToErase - highestRow));
					Arrays.fill(buf, pos, newPos, EMPTY);
				}

				System.arraycopy(rowWidth, highestRow, rowWidth, highestRow + 1, rowToErase - highestRow);
				rowWidth[highestRow] = 0;

				erased.add(rowToErase);
				maxColumnHeight--;

				for (int j = 0; j < skyline.length; j++) {
					skyline[j] = computeColumnHeight(j);
				}
			}
		}
		return erased;
	}

	private int computeColumnHeight(int col) {
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			if (buf[toPos(row, col)] != 0) {
				return BOARD_HEIGHT - row;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			s.append("##");
			for (int col = 0; col < BOARD_WIDTH; col++) {
				s.append(chars[buf[toPos(row, col)] + 1]);
			}
			s.append("## ").append(rowWidth[row]).append("\n");
		}

		for (int col = 0; col < BOARD_WIDTH + 4; col++) {
			s.append("#");
		}
		s.append("\n");
		for (int col = 0; col < BOARD_WIDTH + 4; col++) {
			if (col > 1 && col < BOARD_WIDTH + 2) {
				s.append(skyline[col - 2]);
			} else {
				s.append(" ");
			}
		}
		s.append(" ").append(maxColumnHeight).append("\n");

		return s.toString();
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
		TetrisBoard other = (TetrisBoard) obj;
		return new EqualsBuilder().append(buf, other.buf).append(skyline, other.skyline)
				.append(rowWidth, other.rowWidth).append(maxColumnHeight, other.maxColumnHeight).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(buf).append(skyline).append(rowWidth).append(maxColumnHeight).toHashCode();
	}

	/** Hole = empty piece below skyline */
	public int getHolesCount() {
		//TODO: Might be updated per action
		int holes = 0;

		for (int c = 0; c < getWidth(); ++c) {
			int columnHeight = Math.min(getSkyline()[c], getHeight());
			for (int r = getHeight() - 1; r > getHeight() - columnHeight; --r) {
				holes += (getValue(r, c) == TetrisBoard.EMPTY) ? 1 : 0;
			}
		}
		return holes;
	}
}
