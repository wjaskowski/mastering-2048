package put.ci.cevo.games.tetris;

import com.google.common.base.Preconditions;
import put.ci.cevo.rl.environment.Action;

public class TetrisAction implements Action {

	private final int position;
	private final int rotation;

	public TetrisAction(int position, int rotation) {
		Preconditions.checkArgument(0 <= position);
		Preconditions.checkArgument(0 <= rotation && rotation < Tetromino.NUM_ROTATIONS);
		this.position = position;
		this.rotation = rotation;
	}

	public int getRotation() {
		return rotation;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public double[] getDescription() {
		return new double[] { position, rotation };
	}

	@Override
	public String toString() {
		return "position: " + position + " , rotation: " + rotation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TetrisAction that = (TetrisAction) o;

		if (position != that.position)
			return false;
		if (rotation != that.rotation)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = position;
		result = 31 * result + rotation;
		return result;
	}
}
