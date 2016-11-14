package put.ci.cevo.games.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Preconditions;
import org.apache.commons.collections15.iterators.ArrayIterator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/** Represents a list of position on a rectangular board. */
public final class BoardPosList implements Iterable<BoardPos> {
	private final BoardPos[] positions;

	public BoardPos get(int idx) {
		return positions[idx];
	}

	/**
	 * A shortcut to the constructor {@link BoardPosList(String[]) where rows are separated by '|'. It is required to
	 * use the constructor from the configuration}
	 */
	public BoardPosList(String matrix) {
		this(matrix.split("\\|"));
	}

	/**
	 * @param matrix Each String represents one row. 1 in string means position whereas 0 mean no position. Have not be
	 *               rectangular.
	 */
	// TODO: Consider to move it as a static factory fromCharMatrix() (but config needs to handle this)
	public BoardPosList(String[] matrix) {
		for (String row : matrix) {
			for (char c : row.toCharArray()) {
				Preconditions.checkArgument(c == '0' || c == '1', "found `" + c + "'");
			}
		}

		Preconditions.checkArgument(matrix.length > 0);
		ArrayList<BoardPos> list = new ArrayList<>();
		for (int r = 0; r < matrix.length; ++r) {
			for (int c = 0; c < matrix[r].length(); ++c) {
				if (matrix[r].charAt(c) == '1') {
					list.add(new BoardPos(r, c));
				}
			}
		}
		this.positions = list.toArray(new BoardPos[list.size()]);
	}

	public BoardPosList(Collection<BoardPos> positions) {
		this(positions.toArray(new BoardPos[positions.size()]));
	}

	public BoardPosList(BoardPos[] positions) {
		// No defensive copy
		this.positions = positions;
	}

	/** All positions are shifted to the left and up, so that some of them are in row 0 and some of them are on column 0 */
	public BoardPosList getAligned() {
		BoardPos minPos = getMinCorner();
		return getShifted(-minPos.row(), -minPos.column());
	}

	/** All positions are shifted to the right and down by shift. Shifts may be negative. */
	public BoardPosList getShifted(int shiftRow, int shiftColumn) {
		BoardPos[] shifted = new BoardPos[positions.length];
		for (int i = 0; i < positions.length; ++i) {
			shifted[i] = positions[i].add(new BoardPos(shiftRow, shiftColumn));
		}
		return new BoardPosList(shifted);
	}

	/** Minimal corner = minimal row and minimal column */
	private BoardPos getMinCorner() {
		int minRow = positions[0].row();
		int minColumn = positions[0].column();
		for (BoardPos pos : positions) {
			minRow = Math.min(minRow, pos.row());
			minColumn = Math.min(minColumn, pos.column());
		}
		return new BoardPos(minRow, minColumn);
	}

	/** Locations are margin-based. */
	public int[] toLocations(RectSize boardSize) {
		int[] locations = new int[positions.length];
		for (int i = 0; i < positions.length; ++i) {
			locations[i] = BoardUtils.toMarginPos(boardSize, positions[i]);
		}
		return locations;
	}

	/** Whether all positions fit on the board */
	public boolean fitOnBoard(RectSize boardSize) {
		for (BoardPos pos : positions) {
			if (!boardSize.contains(pos)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
		BoardPosList other = (BoardPosList) obj;
		return new EqualsBuilder().append(this.positions, other.positions).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(positions).build();
	}

	@Override
	public Iterator<BoardPos> iterator() {
		return new ArrayIterator<>(positions);
	}

	public int size() {
		return positions.length;
	}
}