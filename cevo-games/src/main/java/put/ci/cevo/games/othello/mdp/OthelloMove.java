package put.ci.cevo.games.othello.mdp;

import java.util.Objects;

import put.ci.cevo.rl.environment.Action;

public class OthelloMove implements Action {

	private final int row;
	private final int col;

	/** TODO: this field seems to be unnecessary (not used anywhere). */
	private int player;

	public OthelloMove(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getPlayer() {
		return player;
	}

	public int compareTo(Action a) {
		OthelloMove o = (OthelloMove) a;
		if (getRow() != o.getRow()) {
			return getRow() - o.getRow();
		} else {
			return getCol() - o.getCol();
		}
	}

	@Override
	public String toString() {
		return "(" + row + ", " + col + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		OthelloMove o = (OthelloMove) other;
		return (row == o.row && col == o.col && player == o.player);
	}

	@Override
	public int hashCode() {
		// TODO: This is probably not very efficient
		return Objects.hash(row, col, player);
	}

	@Override
	public double[] getDescription() {
		return new double[] { row, col, player };
	}
}
