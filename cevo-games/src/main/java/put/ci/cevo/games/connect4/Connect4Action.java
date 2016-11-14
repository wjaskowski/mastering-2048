package put.ci.cevo.games.connect4;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import put.ci.cevo.rl.environment.Action;

public class Connect4Action implements Action {

	private final int col;

	public Connect4Action(int col) {
		this.col = col;
	}

	public int getCol() {
		return col;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(col).build();
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
		Connect4Action o = (Connect4Action) other;
		return (col == o.col);
	}

	@Override
	public String toString() {
		return "(" + col + ")";
	}

	@Override
	public double[] getDescription() {
		return new double[] { col };
	}
}
