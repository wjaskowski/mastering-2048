package put.ci.cevo.experiments.cig2048.sanitykey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

final class IDDFSABPlayer extends AbstractPlayer {
	public static final double DEFAULT_SMOOTHNESS_WEIGHT = 0.1d;
	public static final double DEFAULT_MONOTONICITY_WEIGHT = 1.0d;
	public static final double DEFAULT_EMPTY_WEIGHT = 2.7d;
	public static final double DEFAULT_MAX_WEIGHT = 1.0d;
	public static final int DEFAULT_WINNING_SCORE = 10000;
	public static final int DEFAULT_SCORE_CHECK = 9900;
	public static final double DEFAULT_ALPHA = -10000.0d;
	public static final double DEFAULT_BETA = 10000.0d;

	public static final int[] UP = new int[] { -1, 0 };
	public static final int[] RIGHT = new int[] { 0, 1 };
	public static final int[] DOWN = new int[] { 1, 0 };
	public static final int[] LEFT = new int[] { 0, -1 };
	public static final int[][] VECTORS = new int[][] { UP, RIGHT, DOWN, LEFT };

	private final Game game;
	private int action;

	public IDDFSABPlayer(final Random random, final Game game) {
		super(random);
		this.game = game;
		action = -1;
	}

	@Override
	public final int getAction() {
		final double[] best = iterativeDeep(game.getGrid(), DEFAULT_ALPHA, DEFAULT_BETA, game.getWinningTile(), 100);
		if (best != null && best[0] != -1) {
			action = (int) best[0];
		} else {
			action = Game.ACTIONS[random.nextInt(Game.ACTIONS.length)];
		}
		return action;
	}

	public static final boolean isTileInGrid(final int[][] grid, final int row, final int column) {
		return row > -1 && row < grid.length && column > -1 && column < grid[row].length;
	}

	public static final boolean isTileEmpty(final int[][] grid, final int row, final int column) {
		return grid[row][column] == Game.EMPTY;
	}

	public static final int[] findFarthestTile(final int[][] grid, int row, int column, final int[] vector) {
		do {
			row = row + vector[0];
			column = column + vector[1];
		} while (isTileInGrid(grid, row, column) && isTileEmpty(grid, row, column));
		return new int[] { row, column };
	}

	public static final double getSmoothness(final int[][] grid) {
		double smoothness = 0.0d;
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (!isTileEmpty(grid, row, column)) {
					final int power = (int) (Math.log(grid[row][column]) / Math.log(2));
					for (int direction = Game.RIGHT; direction <= Game.DOWN; direction++) {
						final int[] targetTile = findFarthestTile(grid, row, column, VECTORS[direction]);
						if (isTileInGrid(grid, targetTile[0], targetTile[1])
							&& !isTileEmpty(grid, targetTile[0], targetTile[1])) {
							final int targetPower = (int) (Math.log(grid[targetTile[0]][targetTile[1]]) / Math.log(2));
							smoothness -= Math.abs(power - targetPower);
						}
					}
				}
			}
		}
		return smoothness;
	}

	public static final double getMonotonicity(final int[][] grid) {
		final double[] totals = new double[Game.ACTIONS.length];
		for (int row = 0; row < grid.length; row++) {
			int currentColumn = 0;
			int nextColumn = currentColumn + 1;
			while (nextColumn < grid[row].length) {
				while (nextColumn < grid[row].length - 1 && isTileEmpty(grid, row, nextColumn)) {
					nextColumn++;
				}
				final int currentPower = !isTileEmpty(grid, row, currentColumn) ? (int) (Math
					.log(grid[row][currentColumn]) / Math.log(2)) : 0;
				final int nextPower = !isTileEmpty(grid, row, nextColumn) ? (int) (Math.log(grid[row][nextColumn]) / Math
					.log(2)) : 0;
				if (currentPower > nextPower) {
					totals[0] += nextPower - currentPower;
				} else if (nextPower > currentPower) {
					totals[1] += currentPower - nextPower;
				}
				currentColumn = nextColumn;
				nextColumn++;
			}
		}
		for (int column = 0; column < grid[0].length; column++) {
			int currentRow = 0;
			int nextRow = currentRow + 1;
			while (nextRow < grid.length) {
				while (nextRow < grid.length - 1 && isTileEmpty(grid, nextRow, column)) {
					nextRow++;
				}
				final int currentPower = !isTileEmpty(grid, currentRow, column) ? (int) (Math
					.log(grid[currentRow][column]) / Math.log(2)) : 0;
				final int nextPower = !isTileEmpty(grid, nextRow, column) ? (int) (Math.log(grid[nextRow][column]) / Math
					.log(2)) : 0;
				if (currentPower > nextPower) {
					totals[2] += nextPower - currentPower;
				} else if (nextPower > currentPower) {
					totals[3] += currentPower - nextPower;
				}
				currentRow = nextRow;
				nextRow++;
			}
		}
		return Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);
	}

	public static final int countEmptyTiles(final int[][] grid) {
		int count = 0;
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (grid[row][column] == Game.EMPTY) {
					count++;
				}
			}
		}
		return count;
	}

	public static final int getMaxValue(final int[][] grid) {
		int max = Integer.MIN_VALUE;
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (grid[row][column] > max) {
					max = grid[row][column];
				}
			}
		}
		return (int) (Math.log(max) / Math.log(2));
	}

	public static final double evaluation(final int[][] grid, final double smoothnessWeight,
			final double monotonicityWeight, final double emptyWeight, final double maxWeight) {
		return getSmoothness(grid) * smoothnessWeight + getMonotonicity(grid) * monotonicityWeight
			+ Math.log(countEmptyTiles(grid)) * emptyWeight + getMaxValue(grid) * maxWeight;
	}

	public static final double evaluation(final int[][] grid) {
		return evaluation(grid, DEFAULT_SMOOTHNESS_WEIGHT, DEFAULT_MONOTONICITY_WEIGHT, DEFAULT_EMPTY_WEIGHT,
			DEFAULT_MAX_WEIGHT);
	}

	public static final int[][] copy(final int[][] grid) {
		final int[][] copy = new int[grid.length][];
		for (int row = 0; row < grid.length; row++) {
			copy[row] = Arrays.copyOf(grid[row], grid[row].length);
		}
		return copy;
	}

	public static final void mark(final boolean[][] marked, final int[][] grid, final int row, final int column,
			final int value) {
		if (isTileInGrid(grid, row, column) && !isTileEmpty(grid, row, column) && grid[row][column] == value
			&& !marked[row][column]) {
			marked[row][column] = true;
			for (final int[] vector : VECTORS) {
				mark(marked, grid, row + vector[0], column + vector[1], value);
			}
		}
	}

	public static final int getIslands(final int[][] grid) {
		int islands = 0;
		final boolean[][] marked = new boolean[grid.length][];
		for (int r = 0; r < grid.length; r++) {
			marked[r] = new boolean[grid[r].length];
			Arrays.fill(marked[r], false);
		}
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (!isTileEmpty(grid, row, column) && !marked[row][column]) {
					islands++;
					mark(marked, grid, row, column, grid[row][column]);
				}
			}
		}
		return islands;
	}

	public static final double[] search(final int[][] grid, final int depth, final double alpha, final double beta,
			int positions, int cutoffs, final boolean playerTurn, final int winningTile) {
		double bestScore;
		int bestAction = -1;
		double[] result;
		if (playerTurn) {
			bestScore = alpha;
			for (int direction = Game.UP; direction <= Game.LEFT; direction++) {
				final int[][] clone = copy(grid);
				Game.move(clone, direction);
				if (!Arrays.deepEquals(clone, grid)) {
					positions++;
					if (Game.hasWon(clone, winningTile)) {
						return new double[] { direction, DEFAULT_WINNING_SCORE, positions, cutoffs };
					} else {
						if (depth == 0) {
							result = new double[] { direction, evaluation(clone) };
						} else {
							result = search(clone, depth - 1, bestScore, beta, positions, cutoffs, !playerTurn,
								winningTile);
							if (result[1] > DEFAULT_SCORE_CHECK) {
								result[1]--;
							}
							positions = (int) result[2];
							cutoffs = (int) result[3];
						}
						if (result[1] > bestScore) {
							bestScore = result[1];
							bestAction = direction;
						}
						if (bestScore > beta) {
							cutoffs++;
							return new double[] { bestAction, beta, positions, cutoffs };
						}
					}
				}
			}
		} else {
			bestScore = beta;
			final int[][] clone = copy(grid);
			final List<int[]> emptyTiles = Game.getEmptyTiles(clone);
			final double[][] scores = new double[2][emptyTiles.size()];
			double maxScore = Double.NEGATIVE_INFINITY;
			final List<int[]> candidates = new ArrayList<int[]>();
			for (int value = 0; value < scores.length; value++) {
				for (int tile = 0; tile < scores[value].length; tile++) {
					final int[] emptyTile = emptyTiles.get(tile);
					final int v = (value + 1) * 2;
					clone[emptyTile[0]][emptyTile[1]] = v;
					scores[value][tile] = -getSmoothness(clone) * getIslands(clone);
					clone[emptyTile[0]][emptyTile[1]] = Game.EMPTY;
					if (maxScore < scores[value][tile]) {
						maxScore = scores[value][tile];
					}
				}
			}
			for (int value = 0; value < scores.length; value++) {
				for (int tile = 0; tile < scores[value].length; tile++) {
					final int[] emptyTile = emptyTiles.get(tile);
					final int v = (value + 1) * 2;
					if (scores[value][tile] == maxScore) {
						candidates.add(new int[] { emptyTile[0], emptyTile[1], v });
					}
				}
			}
			for (final int[] candidate : candidates) {
				final int[][] c = copy(grid);
				c[candidate[0]][candidate[1]] = candidate[2];
				positions++;
				result = search(c, depth, alpha, bestScore, positions, cutoffs, !playerTurn, winningTile);
				positions = (int) result[2];
				cutoffs = (int) result[3];
				if (result[1] < bestScore) {
					bestScore = result[1];
				}
				if (bestScore < alpha) {
					cutoffs++;
					return new double[] { -1, alpha, positions, cutoffs };
				}
			}
		}
		return new double[] { bestAction, bestScore, positions, cutoffs };
	}

	public static final double[] iterativeDeep(final int[][] grid, final double alpha, final double beta,
			final int winningTile, final long maxSearchTime) {
		final long startTime = System.currentTimeMillis();
		int depth = 0;
		double[] best = null;
		do {
			final double[] newBest = search(grid, depth, alpha, beta, 0, 0, true, winningTile);
			if (newBest != null && newBest[0] == -1) {
				break;
			} else {
				best = newBest;
			}
			depth++;
		} while (System.currentTimeMillis() - startTime < maxSearchTime);
		return best;
	}
}