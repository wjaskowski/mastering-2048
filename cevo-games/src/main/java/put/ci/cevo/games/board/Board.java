package put.ci.cevo.games.board;

public interface Board {

	int WHITE = 0;
	int EMPTY = 1;
	int BLACK = 2;

	// is this really necessary here?
	int[][] DIRS = { new int[] { -1, -1 }, new int[] { -1, 0 }, new int[] { -1, 1 },
		new int[] { 0, -1 }, new int[] { 0, 1 }, new int[] { 1, -1 }, new int[] { 1, 0 }, new int[] { 1, 1 } };

	int getWidth();

	int getHeight();

	/**
	 * @param row
	 *            0-based index
	 * @param col
	 *            0-based index
	 */
	int getValue(int row, int col);

	void setValue(int row, int col, int color);

	/**
	 * @param pos
	 *            pos is not 0-based. It is currently implementation dependent
	 */
	int getValue(int pos);

	void setValue(int pos, int color);

	/** Inverts each BLACK to WHITE and vice versa */
	default void invert() {
		throw new IllegalStateException("invert not implemented");
	}

	// TODO: Currently Board has the logic of a game. I consider making Board a simple class that has no game logic.
	// This way game logic could be grouped in particular game class. To do this, I need to remove createAfterState from
	// here (this method stops me from doing that)
	Board createAfterState(int row, int col, int player);

	Board clone();

}
