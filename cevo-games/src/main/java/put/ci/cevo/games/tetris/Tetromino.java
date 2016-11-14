package put.ci.cevo.games.tetris;

public enum Tetromino {
	S(new int[][] { { 0, 1, 1 }, { 1, 1, 0 }, { 0, 0, 0 } }, 2),
	Z(new int[][] { { 1, 1, 0 }, { 0, 1, 1 }, { 0, 0, 0 } }, 2),
	L(new int[][] { { 1, 1, 1 }, { 1, 0, 0 }, { 0, 0, 0 } }, 4),
	J(new int[][] { { 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 0 } }, 4),
	T(new int[][] { { 1, 1, 1 }, { 0, 1, 0 }, { 0, 0, 0 } }, 4),
	I(new int[][] { { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } }, 2),
	O(new int[][] { { 1, 1 }, { 1, 1 } }, 1);

	public static final int NUM_ROTATIONS = 4;

	private final int size;
	private final int[][][] tiles;
	private final int[] totalWidth;
	private final int[][] bottomTiles;

	private final int possibleRotations;

	private Tetromino(int[][] baseTiles, int possibleRotations) {
		this.possibleRotations = possibleRotations;
		this.tiles = new int[possibleRotations][][];
		this.totalWidth = new int[possibleRotations];
		this.bottomTiles = new int[possibleRotations][];
		this.size = baseTiles.length;

		for (int rot = 0; rot < possibleRotations; rot++) {
			this.tiles[rot] = computeRotatedTiles(rot, baseTiles);
			this.totalWidth[rot] = computeWidth(rot);
			this.bottomTiles[rot] = computeBottomTiles(rot);
		}
	}

	public int getPossibleRotations() {
		return possibleRotations;
	}

	public int[][] getTiles(int rotation) {
		return tiles[rotation];
	}

	public int getWidth(int rotation) {
		return totalWidth[rotation];
	}

	/**
	 * E.g., for vertical S it returns [2, 1]
	 */
	public int[] getBottomTiles(int rotation) {
		return bottomTiles[rotation];
	}

	/**
	 * E.g., for vertical S it returns [3, 2]
	 */
	public int[] getTopTiles(int rotation) {
		return bottomTiles[rotation];
	}

	private int computeWidth(int rot) {
		for (int col = size - 1; col >= 0; col--) {
			for (int row = 0; row < size; row++) {
				if (tiles[rot][row][col] != 0) {
					return col + 1;
				}
			}
		}
		return 0;
	}

	private int[] computeBottomTiles(int rot) {
		int[] heights = new int[totalWidth[rot]];
		for (int col = 0; col < totalWidth[rot]; col++) {
			for (int row = 0; row < size; row++) {
				if (tiles[rot][row][col] != 0) {
					heights[col] = row + 1;
				}
			}
		}
		return heights;
	}

	private int[][] computeRotatedTiles(int rot, int[][] baseTiles) {
		int row, col;
		int[][] rotated = new int[size][size];
		switch (rot) {
		case 0:
			for (row = 0; row < size; row++)
				for (col = 0; col < size; col++)
					rotated[row][col] = baseTiles[row][col];
			break;
		case 1:
			for (row = 0; row < size; row++)
				for (col = 0; col < size; col++)
					rotated[row][col] = baseTiles[col][size - 1 - row];
			break;
		case 2:
			for (row = 0; row < size; row++)
				for (col = 0; col < size; col++)
					rotated[row][col] = baseTiles[size - 1 - row][size - 1 - col];
			break;
		case 3:
			for (row = 0; row < size; row++)
				for (col = 0; col < size; col++)
					rotated[row][col] = baseTiles[size - 1 - col][row];
			break;
		}

		int[][] shifted = new int[size][size];
		int emptyCols = 0;
		int emptyRows = 0;

		emptyRowLoop:
		for (row = 0; row < size; row++) {
			for (col = 0; col < size; col++) {
				if (rotated[row][col] != 0)
					break emptyRowLoop;
			}
			emptyRows++;
		}

		emptyColLoop:
		for (col = 0; col < size; col++) {
			for (row = 0; row < size; row++) {
				if (rotated[row][col] != 0)
					break emptyColLoop;
			}
			emptyCols++;
		}

		for (row = emptyRows; row < size; row++) {
			for (col = emptyCols; col < size; col++)
				shifted[row - emptyRows][col - emptyCols] = rotated[row][col];
		}
		return shifted;
	}
}
