package put.ci.cevo.games.tetris;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.util.ArrayUtils;

public class TetrisSimulateMoveBenchmark extends AbstractBenchmark {

	private TetrisBoard board;
	@Before
	public void setup() {
		board = new TetrisBoard();
		board.placeTetromino(Tetromino.S, 0, 1);
		board.placeTetromino(Tetromino.S, 2, 1);
		board.placeTetromino(Tetromino.S, 4, 1);

		board.placeTetromino(Tetromino.S, 0, 1);
		board.placeTetromino(Tetromino.S, 2, 1);
		board.placeTetromino(Tetromino.S, 4, 1);

		board.placeTetromino(Tetromino.S, 4, 1);
		board.placeTetromino(Tetromino.Z, 0, 0);
		board.placeTetromino(Tetromino.Z, 0, 1);
		board.placeTetromino(Tetromino.Z, 2, 0);
		board.placeTetromino(Tetromino.S, 1, 0);

		board.placeTetromino(Tetromino.S, 6, 0);
		board.placeTetromino(Tetromino.S, 8, 1);
		board.placeTetromino(Tetromino.S, 8, 1);
		board.placeTetromino(Tetromino.Z, 8, 1);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testSimulatePerformance() {
		for (int i = 0; i < 100000; i++) {
			board.simulatePlaceTetromino(Tetromino.Z, 6, 1);
			board.simulatePlaceTetromino(Tetromino.Z, 7, 1);
			board.simulatePlaceTetromino(Tetromino.Z, 1, 0);
			board.simulatePlaceTetromino(Tetromino.Z, 8, 1);
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testSimulateNaivePerformance() {
		for (int i = 0; i < 100000; i++) {
			board.simulatePlaceTetrominoNaive(Tetromino.Z, 6, 1);
			board.simulatePlaceTetrominoNaive(Tetromino.Z, 7, 1);
			board.simulatePlaceTetrominoNaive(Tetromino.Z, 1, 0);
			board.simulatePlaceTetrominoNaive(Tetromino.Z, 8, 1);
		}
	}
}
