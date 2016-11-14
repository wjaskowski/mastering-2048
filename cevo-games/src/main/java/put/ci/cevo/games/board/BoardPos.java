package put.ci.cevo.games.board;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/** Represents a position on a rectangular board. Position is 0-based */
//TODO: Is BoardPos a good name? It should be read asa "Position on the board" instead of "Board Position"
public final class BoardPos {
	private final int row;
	private final int column;

	public int row() {
		return row;
	}

	public int column() {
		return column;
	}

	public int y() {
		return row;
	}

	public int x() {
		return column;
	}

	/**
	 * row or column can be negative
	 */
	public BoardPos(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public BoardPos(int zeroBasedPosition, RectSize boardSize) {
		this(zeroBasedPosition / boardSize.height(), zeroBasedPosition % boardSize.height());
		Preconditions.checkArgument(0 <= zeroBasedPosition && zeroBasedPosition < boardSize.area());
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
		BoardPos other = (BoardPos) obj;
		return (this.row == other.row && this.column == other.column);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(row).append(column).build();
	}

	public BoardPos substract(BoardPos pos) {
		return new BoardPos(row - pos.row, column - pos.column);
	}

	public BoardPos add(BoardPos pos) {
		return new BoardPos(row + pos.row, column + pos.column);
	}
}
