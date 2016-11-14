package put.ci.cevo.experiments.cig2048.sanitykey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.RandomAgent;

final class Game {
	public static final int DEFAULT_ROWS = 4;
	public static final int DEFAULT_COLUMNS = 4;
	public static final int DEFAULT_WINNING_TILE = 2048;
	public static final int[] DEFAULT_TILES = new int[] { 2, 4 };
	public static final double[] DEFAULT_PROBABILITIES = new double[] { 0.9d, 0.1d };
	public static final int EMPTY = 0;
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int[] ACTIONS = new int[] { UP, RIGHT, DOWN, LEFT };

	private final Random random;
	private final int[][] grid;
	private final int[] tiles;
	private final double[] probabilities;
	private int winningTile;
	private int bestScore;
	private int score;
	private Boolean hasWon;
	private int game;
	private Player player;
	private Player currentPlayer;

	public Game(final Random random, final int rows, final int columns, final int[] tiles,
			final double[] probabilities, final int winningTile) {
		this.random = random;
		grid = new int[rows][columns];
		this.tiles = tiles;
		this.probabilities = probabilities;
		this.winningTile = winningTile;
		bestScore = 0;
		game = -1;
		reset();
	}

	public Game(final Random random) {
		this(random, DEFAULT_ROWS, DEFAULT_COLUMNS, DEFAULT_TILES, DEFAULT_PROBABILITIES, DEFAULT_WINNING_TILE);
	}

	public final Random getRandom() {
		return random;
	}

	public final int[][] getGrid() {
		return grid;
	}

	public final int[] getTiles() {
		return tiles;
	}

	public final double[] getProbabilities() {
		return probabilities;
	}

	public final int getWinningTile() {
		return winningTile;
	}

	public final void setWinningTile(final int winningTile) {
		this.winningTile = winningTile;
	}

	public final int getBestScore() {
		return bestScore;
	}

	public final void setBestScore(final int bestScore) {
		this.bestScore = bestScore;
	}

	public final int getScore() {
		return score;
	}

	public final void setScore(final int score) {
		this.score = score;
	}

	public final Boolean getHasWon() {
		return hasWon;
	}

	public final void setHasWon(final Boolean hasWon) {
		this.hasWon = hasWon;
	}

	public final int getGame() {
		return game;
	}

	public final void setGame(final int game) {
		this.game = game;
	}

	public final Player getPlayer() {
		return player;
	}

	public final void setPlayer(final Player player) {
		this.player = player;
	}

	public final Player getCurrentPlayer() {
		return currentPlayer;
	}

	public final void setCurrentPlayer(final Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public final void reset() {
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				grid[row][column] = EMPTY;
			}
		}
		score = 0;
		hasWon = null;
		addRandomTile(random, grid, probabilities[0]);
		addRandomTile(random, grid, probabilities[0]);
		game++;
	}

	public final void playGame() {
		while (hasWon == null) {
			synchronized (this) {
				currentPlayer = player;
			}
			if (currentPlayer != null) {
				score += move(grid, currentPlayer.getAction());
				if (score > bestScore) {
					bestScore = score;
				}
				addRandomTile(random, grid, probabilities[0]);
				if (hasWon(grid, winningTile)) {
					hasWon = true;
				} else if (hasLost(grid)) {
					hasWon = false;
				}
			}
		}
	}

	public static final List<int[]> getEmptyTiles(final int[][] grid) {
		final List<int[]> emptyTiles = new ArrayList<int[]>();
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (grid[row][column] == EMPTY) {
					emptyTiles.add(new int[] { row, column });
				}
			}
		}
		return emptyTiles;
	}

	public static final void addRandomTile(final Random random, final int[][] grid, final double probabilityOfTwo) {
		final List<int[]> emptyTiles = getEmptyTiles(grid);
		if (!emptyTiles.isEmpty()) {
			final int[] randomEmptyTile = emptyTiles.get(random.nextInt(emptyTiles.size()));
			final int value;
			if (random.nextDouble() < probabilityOfTwo) {
				value = 2;
			} else {
				value = 4;
			}
			grid[randomEmptyTile[0]][randomEmptyTile[1]] = value;
		}
	}

	public static final int moveUp(final int[][] grid) {
		int score = 0;
		for (int column = 0; column < grid[0].length; column++) {
			final int[] tiles = new int[grid.length];
			int i = 0;
			for (int row = grid.length - 1; row >= 0; row--) {
				tiles[i] = grid[row][column];
				i++;
			}
			score += mergeRight(tiles);
			i = 0;
			for (int row = grid.length - 1; row >= 0; row--) {
				grid[row][column] = tiles[i];
				i++;
			}
		}
		return score;
	}

	public static final int moveRight(final int[][] grid) {
		int score = 0;
		for (int row = 0; row < grid.length; row++) {
			score += mergeRight(grid[row]);
		}
		return score;
	}

	public static final int moveDown(final int[][] grid) {
		int score = 0;
		for (int column = 0; column < grid[0].length; column++) {
			final int[] tiles = new int[grid.length];
			int i = 0;
			for (int row = 0; row < grid.length; row++) {
				tiles[i] = grid[row][column];
				i++;
			}
			score += mergeRight(tiles);
			i = 0;
			for (int row = 0; row < grid.length; row++) {
				grid[row][column] = tiles[i];
				i++;
			}
		}
		return score;
	}

	public static final int moveLeft(final int[][] grid) {
		int score = 0;
		for (int row = 0; row < grid.length; row++) {
			final int[] tiles = new int[grid[row].length];
			int i = 0;
			for (int column = grid[row].length - 1; column >= 0; column--) {
				tiles[i] = grid[row][column];
				i++;
			}
			score += mergeRight(tiles);
			i = 0;
			for (int column = grid[row].length - 1; column >= 0; column--) {
				grid[row][column] = tiles[i];
				i++;
			}
		}
		return score;
	}

	public static final int move(final int[][] grid, final int direction) {
		switch (direction) {
		case UP: {
			return moveUp(grid);
		}
		case RIGHT: {
			return moveRight(grid);
		}
		case DOWN: {
			return moveDown(grid);
		}
		case LEFT: {
			return moveLeft(grid);
		}
		default: {
			return 0;
		}
		}
	}

	public static final boolean hasWon(final int[][] grid, final int winningTile) {
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (grid[row][column] == winningTile) {
					return true;
				}
			}
		}
		return false;
	}

	public static final boolean hasEqualNeighbour(final int[][] grid, final int row, final int column) {
		if ((row > 0 && grid[row - 1][column] == grid[row][column])
			|| (column < grid[row].length - 1 && grid[row][column + 1] == grid[row][column])
			|| (row < grid.length - 1 && grid[row + 1][column] == grid[row][column])
			|| (column > 0 && grid[row][column - 1] == grid[row][column])) {
			return true;
		} else {
			return false;
		}
	}

	public static final boolean hasLost(final int[][] grid) {
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (grid[row][column] == EMPTY) {
					return false;
				}
			}
		}
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				if (hasEqualNeighbour(grid, row, column)) {
					return false;
				}
			}
		}
		return true;
	}

	public static final int mergeRight(final int[] tiles) {
		for (int pos = tiles.length - 2; pos >= 0; pos--) {
			int p = pos;
			while (p < tiles.length - 1) {
				if (tiles[p + 1] == EMPTY) {
					tiles[p + 1] = tiles[p];
					tiles[p] = 0;
				}
				p++;
			}
		}
		int score = 0;
		for (int pos = tiles.length - 2; pos >= 0; pos--) {
			if (tiles[pos + 1] == tiles[pos]) {
				tiles[pos + 1] = tiles[pos + 1] + tiles[pos];
				score += tiles[pos + 1];
				tiles[pos] = 0;
			}
		}
		for (int pos = tiles.length - 2; pos >= 0; pos--) {
			int p = pos;
			while (p < tiles.length - 1) {
				if (tiles[p + 1] == EMPTY) {
					tiles[p + 1] = tiles[p];
					tiles[p] = 0;
				}
				p++;
			}
		}
		return score;
	}

	public static void main(String[] args) {
		final Random random = new Random();
		final Game game = new Game(random);
		game.setPlayer(new RandomPlayer(random));

		double sumScores = 0;
		int numGames = 1000;
		for (int i = 0; i < numGames; i++) {
			game.reset();
			game.playGame();
			sumScores += game.getScore();
		}
		System.out.println(sumScores / numGames);

		RandomDataGenerator rdg = new RandomDataGenerator();
		Game2048 game2048 = new Game2048();
		Agent<State2048, Action2048> randomPlayer2048 = new RandomAgent<>();

		sumScores = 0;
		for (int i = 0; i < numGames; i++) {
			Game2048Outcome result = game2048.playGame(randomPlayer2048, rdg);
			sumScores += result.score();
		}
		System.out.println(sumScores / numGames);
	}
}