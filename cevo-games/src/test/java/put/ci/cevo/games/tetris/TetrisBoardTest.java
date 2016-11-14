package put.ci.cevo.games.tetris;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.TreeSet;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.tetris.agents.GECCO2015BestBICMAESSZTetrisAgent;
import put.ci.cevo.games.tetris.agents.GECCO2015BestCMAESVD3x3NTupleSZTetrisAgent;
import put.ci.cevo.games.tetris.agents.GECCO2015BestTDL4x4NTupleSZTetrisAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.util.ArrayUtils;
import put.ci.cevo.util.RandomUtils;

public class TetrisBoardTest {

	private TetrisBoard board;

	@Before
	public void setup() {
		board = new TetrisBoard();
	}

	@Test
	public void initTest() {
		int[] skyline = new int[board.getWidth()];
		assertArrayEquals(skyline, board.getSkyline());
	}

	@Test
	public void testFindLeastSquaresLeft() throws Exception {
		Assert.assertEquals(2, board.calculateLandingHeight(Tetromino.S, 0, 0));
		Assert.assertEquals(3, board.calculateLandingHeight(Tetromino.S, 0, 1));

		Assert.assertEquals(2, board.calculateLandingHeight(Tetromino.Z, 0, 0));
		Assert.assertEquals(3, board.calculateLandingHeight(Tetromino.Z, 0, 1));
	}

	@Test
	public void testPlaceTetromino() throws Exception {
		board.placeTetromino(Tetromino.S, 0, 0);
		board.placeTetromino(Tetromino.S, 0, 0);
		board.placeTetromino(Tetromino.Z, 5, 1);
		IntArrayList positions = board.placeTetromino(Tetromino.Z, 8, 1);

		assertArrayEquals(new int[] { BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 17, 9),
				BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 8),
				BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 9),
				BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 19, 8) }, positions.toArray());

		assertArrayEquals(new int[] { 3, 4, 4, 0, 0, 2, 3, 0, 2, 3 }, board.getSkyline());
	}

	@Test
	public void testEraseCompleteLines() throws Exception {
		board.placeTetromino(Tetromino.S, 0, 0);
		board.placeTetromino(Tetromino.Z, 0, 1);
		board.placeTetromino(Tetromino.Z, 3, 1);
		board.placeTetromino(Tetromino.S, 5, 1);
		board.placeTetromino(Tetromino.S, 7, 1);
		IntArrayList changed = board.placeTetromino(Tetromino.S, 8, 1);

		assertArrayEquals(new int[] { 3, 4, 2, 2, 3, 3, 2, 3, 4, 3 }, board.getSkyline());
		int lines = board.eraseCompleteLines(changed).size();
		Assert.assertEquals(1, lines);

		assertArrayEquals(new int[] { 2, 3, 0, 1, 2, 2, 1, 2, 3, 2 }, board.getSkyline());
	}

	@Test
	public void testGetValue() {
		board.placeTetromino(Tetromino.S, 0, 0);

		Assert.assertEquals(1, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 1)));
		Assert.assertEquals(0, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 3)));
		Assert.assertEquals(1, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 19, 0)));
		Assert.assertEquals(1, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 19, 1)));
		Assert.assertEquals(0, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 0)));
	}

	@Test
	public void testSetValue() {
		board.setValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 1), 1);
		board.setValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 3), 0);
		board.setValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 19, 0), 1);
		board.setValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 19, 1), 1);
		board.setValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 0), 0);

		Assert.assertEquals(1, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 1)));
		Assert.assertEquals(0, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 3)));
		Assert.assertEquals(1, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 19, 0)));
		Assert.assertEquals(1, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 19, 1)));
		Assert.assertEquals(0, board.getValue(BoardUtils.toMarginPos(TetrisBoard.DEFAULT_BOARD_SIZE, 18, 0)));

	}

	@Test
	public void testBoardState() {
		board.placeTetromino(Tetromino.S, 4, 1);
		board.placeTetromino(Tetromino.Z, 8, 1);
		board.placeTetromino(Tetromino.Z, 6, 1);
		board.placeTetromino(Tetromino.S, 2, 1);

		IntArrayList toErase = board.placeTetromino(Tetromino.S, 0, 1);

		assertArrayEquals(new int[] { 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 5 }, board.rowWidth);
		assertEquals(18, board.eraseCompleteLines(toErase).firstInt());

		assertArrayEquals(new int[] { 2, 1, 2, 1, 2, 1, 1, 2, 1, 2 }, board.getSkyline());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5 },
				board.rowWidth);
		assertEquals(2, board.getMaxColumnHeight());

		board.placeTetromino(Tetromino.Z, 8, 1);
		board.placeTetromino(Tetromino.Z, 6, 1);
		board.placeTetromino(Tetromino.S, 4, 1);
		board.placeTetromino(Tetromino.S, 2, 1);
		board.placeTetromino(Tetromino.Z, 8, 1);
		board.placeTetromino(Tetromino.Z, 6, 1);
		board.placeTetromino(Tetromino.Z, 8, 1);

		toErase = board.placeTetromino(Tetromino.S, 0, 1);

		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 7, 10, 10, 5 }, board.rowWidth);
		assertArrayEquals(new int[] {17, 18}, board.eraseCompleteLines(toErase).toIntArray());

		assertArrayEquals(new int[] { 2, 1, 2, 1, 2, 1, 3, 4, 5, 6 }, board.getSkyline());
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 7, 5 }, board.rowWidth);
		assertEquals(6, board.getMaxColumnHeight());
	}

	@Test
	public void testSimulatePlaceTetrominoOneRowErased() {
		board.placeTetromino(Tetromino.S, 0, 0);
		board.placeTetromino(Tetromino.Z, 0, 1);
		board.placeTetromino(Tetromino.Z, 3, 1);
		board.placeTetromino(Tetromino.Z, 5, 0);
		board.placeTetromino(Tetromino.Z, 2, 0);
		board.placeTetromino(Tetromino.S, 6, 1);
		board.placeTetromino(Tetromino.Z, 0, 0);
		board.placeTetromino(Tetromino.S, 4, 0);
		board.placeTetromino(Tetromino.S, 5, 1);

		TetrisBoard newBoard = this.board.clone();

		IntSortedSet erased = newBoard.eraseCompleteLines(newBoard.placeTetromino(Tetromino.S, 8, 1));
		assertArrayEquals(new int[] { 18 }, erased.toIntArray());

		int[] expectedDiffs = compareBoards(board, newBoard).toArray();
		TetrisBoard beforeSimulation = this.board.clone();
		int[] simulatedDiffs = board.simulatePlaceTetromino(Tetromino.S, 8, 1).changedPositions.toArray();

		Assert.assertEquals(beforeSimulation, board);
		Assert.assertEquals(new TreeSet<>(Ints.asList(expectedDiffs)), new TreeSet<>(Ints.asList(simulatedDiffs)));
	}

	@Test
	public void testSimulatePlaceTetrominoTwoRowsErased() {
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

		assertArrayEquals(new int[] { 9, 11, 12, 12, 8, 6, 1, 2, 8, 9 }, board.getSkyline());

		TetrisBoard newBoard = this.board.clone();

		IntSortedSet erased = newBoard.eraseCompleteLines(newBoard.placeTetromino(Tetromino.Z, 6, 1));
		assertArrayEquals(new int[] { 17, 18 }, erased.toIntArray());

		int[] expectedDiffs = compareBoards(board, newBoard).toArray();
		TetrisBoard beforeSimulation = this.board.clone();
		int[] simulatedDiffs = board.simulatePlaceTetromino(Tetromino.Z, 6, 1).changedPositions.toArray();

		Assert.assertEquals(beforeSimulation, board);
		Assert.assertEquals(new TreeSet<>(Ints.asList(expectedDiffs)), new TreeSet<>(Ints.asList(simulatedDiffs)));
	}

	@Test
	public void testSimulatePlaceTetromino() {
		board.placeTetromino(Tetromino.S, 0, 1);
		board.placeTetromino(Tetromino.S, 0, 1);
		board.placeTetromino(Tetromino.Z, 0, 1);
		board.placeTetromino(Tetromino.Z, 8, 1);
		board.placeTetromino(Tetromino.Z, 5, 1);
		board.placeTetromino(Tetromino.Z, 3, 0);
		board.placeTetromino(Tetromino.S, 3, 0);
		board.placeTetromino(Tetromino.S, 2, 1);
		board.placeTetromino(Tetromino.S, 7, 1);
		board.placeTetromino(Tetromino.S, 7, 0);
		board.placeTetromino(Tetromino.S, 7, 1);
		board.placeTetromino(Tetromino.Z, 6, 0);
		board.placeTetromino(Tetromino.S, 6, 1);
		board.placeTetromino(Tetromino.S, 7, 1);
		board.placeTetromino(Tetromino.S, 6, 0);
		board.placeTetromino(Tetromino.S, 5, 1);

		TetrisBoard newBoard = this.board.clone();
		IntSortedSet erased = newBoard.eraseCompleteLines(newBoard.placeTetromino(Tetromino.Z, 3, 0));
		Assert.assertTrue(erased.isEmpty());

		int[] expectedDiffs = compareBoards(board, newBoard).toArray();
		TetrisBoard beforeSimulation = this.board.clone();
		int[] simulatedDiffs = board.simulatePlaceTetromino(Tetromino.Z, 3, 0).changedPositions.toArray();

		Assert.assertEquals(beforeSimulation, board);
		Assert.assertEquals(new HashSet<>(Ints.asList(expectedDiffs)), new HashSet<>(Ints.asList(simulatedDiffs)));

	}

	@Test
	public void testSimulatePlaceTetrominoTop() {
		board.placeTetromino(Tetromino.S, 2, 1);
		board.placeTetromino(Tetromino.Z, 3, 0);
		board.placeTetromino(Tetromino.Z, 4, 1);
		board.placeTetromino(Tetromino.Z, 3, 0);
		board.placeTetromino(Tetromino.S, 2, 1);
		board.placeTetromino(Tetromino.S, 2, 0);
		board.placeTetromino(Tetromino.Z, 2, 0);
		board.placeTetromino(Tetromino.S, 0, 0);
		board.placeTetromino(Tetromino.S, 1, 1);
		board.placeTetromino(Tetromino.Z, 2, 0);
		board.placeTetromino(Tetromino.S, 4, 1);
		board.placeTetromino(Tetromino.Z, 5, 0);
		board.placeTetromino(Tetromino.Z, 7, 0);

		assertEquals(21, board.calculateLandingHeight(Tetromino.Z, 0, 1));
		assertArrayEquals(new int[] { }, board.simulatePlaceTetromino(Tetromino.Z, 0, 1).changedPositions.toArray());
	}

	@Test
	public void testRemoveFourRows() {
		board.placeTetromino(Tetromino.I, 0, 1);
		board.placeTetromino(Tetromino.I, 1, 1);
		board.placeTetromino(Tetromino.I, 2, 1);
		board.placeTetromino(Tetromino.I, 3, 1);
		board.placeTetromino(Tetromino.I, 4, 1);

		board.placeTetromino(Tetromino.I, 6, 1);
		board.placeTetromino(Tetromino.I, 7, 1);
		board.placeTetromino(Tetromino.I, 8, 1);
		board.placeTetromino(Tetromino.I, 9, 1);

		TetrisBoard newBoard = this.board.clone();
		IntSortedSet erased = newBoard.eraseCompleteLines(newBoard.placeTetromino(Tetromino.I, 5, 1));

		assertEquals(4, erased.size());
		assertArrayEquals(new int[newBoard.buf.length], newBoard.buf);
	}

	private IntArrayList compareBoards(TetrisBoard board, TetrisBoard newBoard) {
		IntArrayList diffs = new IntArrayList();
		for (int pos = 0; pos < board.buf.length; pos++) {
			if (board.getValue(pos) != newBoard.getValue(pos)) {
				diffs.add(pos);
			}
		}
		return diffs;
	}

	@Test
	public void testSimulatePlaceTetrominoMultiple1() {
		for (int i = 0; i < 100; ++i) {
			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(i));
			testSimulatePlaceTetrominoOne(random, new GECCO2015BestTDL4x4NTupleSZTetrisAgent().create());
		}
	}

	@Test
	public void testSimulatePlaceTetrominoMultiple2() {
		for (int i = 0; i < 100; ++i) {
			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(i));
			testSimulatePlaceTetrominoOne(random, new GECCO2015BestBICMAESSZTetrisAgent().create());
		}
	}

	@Test
	public void testSimulatePlaceTetrominoMultiple3() {
		for (int i = 0; i < 100; ++i) {
			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(i));
			testSimulatePlaceTetrominoOne(random, new GECCO2015BestCMAESVD3x3NTupleSZTetrisAgent().create());
		}
	}

	public void testSimulatePlaceTetrominoOne(RandomDataGenerator random, Agent<TetrisState, TetrisAction> agent) {
		Tetris tetris = new Tetris();

		tetris.runEpisode(agent, random, transition -> {
			TetrisBoard board = transition.getState().getBoard();

			Tetromino tetromino = RandomUtils.pickRandom(tetris.getTetrominoes(), random);
			int rotation = RandomUtils.nextInt(0, tetromino.getPossibleRotations() - 1, random);
			int column = RandomUtils.nextInt(0, tetris.getBoardSize().width() - tetromino.getWidth(rotation), random);

			IntArrayList fastChanged = board.simulatePlaceTetromino(tetromino, column, rotation).changedPositions;
			IntArrayList naiveChanged = board.simulatePlaceTetrominoNaive(tetromino, column, rotation).first();

			//			System.out.println(board);
			//			System.out.println(tetromino + " " + rotation + " " + column);
			//			System.out.println(Arrays.toString(ArrayUtils.sorted(fastChanged.toArray())));
			//			System.out.println(Arrays.toString(ArrayUtils.sorted(naiveChanged.toArray())));

			Assert.assertArrayEquals(ArrayUtils.sorted(naiveChanged.toArray()), ArrayUtils.sorted(
					fastChanged.toArray()));
		});
	}
}