package put.ci.cevo.games.connect4;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static put.ci.cevo.games.board.Board.BLACK;
import static put.ci.cevo.games.board.Board.EMPTY;
import static put.ci.cevo.games.board.Board.WHITE;
import static put.ci.cevo.games.connect4.thill.c4.ConnectFour.PLAYER1;
import static put.ci.cevo.games.connect4.thill.c4.ConnectFour.PLAYER2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.connect4.thill.c4.ConnectFour;

public class Connect4BoardTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testBoardConversion() {
		ConnectFour connectFour = new ConnectFour();
		connectFour.putPiece(PLAYER1, 3);
		connectFour.putPiece(PLAYER2, 2);
		connectFour.putPiece(PLAYER1, 2);
		connectFour.putPiece(PLAYER2, 1);
		connectFour.putPiece(PLAYER1, 1);

		Connect4Board connect4Board = Connect4Board.fromThillBoard(connectFour.getBoard());
		assertArrayEquals(connectFour.getColHeight(), connect4Board.colHeights);

		assertEquals(WHITE, connect4Board.getValue(5, 1));
		assertEquals(WHITE, connect4Board.getValue(5, 2));
		assertEquals(BLACK, connect4Board.getValue(5, 3));
		assertEquals(BLACK, connect4Board.getValue(4, 1));
		assertEquals(BLACK, connect4Board.getValue(4, 2));
		assertEquals(EMPTY, connect4Board.getValue(4, 3));
		assertArrayEquals(new int[] { 0, 2, 2, 1, 0, 0, 0 }, connect4Board.colHeights);

		assertArrayEquals(connectFour.getBoard(), new ConnectFour(connect4Board.toThillBoard()).getBoard());
		assertArrayEquals(connectFour.generateMoves(PLAYER1), new ConnectFour(connect4Board.toThillBoard()).generateMoves(PLAYER1));

		connect4Board = new Connect4Board();
		connect4Board.makeMove(3, BLACK);
		connect4Board.makeMove(2, WHITE);
		connect4Board.makeMove(2, BLACK);
		connect4Board.makeMove(1, WHITE);
		connect4Board.makeMove(1, BLACK);

		System.out.println(connect4Board.toString());
		assertArrayEquals(connectFour.getBoard(), connect4Board.toThillBoard());
	}

	@Test
	public void undoMoveTest() {
		Connect4Board board = createTestBoard();
		assertArrayEquals(new int[] { 0, 0, 1, 2, 3, 3, 1 }, board.colHeights);

		board.setValue(0, 2, EMPTY);
		assertEquals(EMPTY, board.getValue(0, 2));
		assertArrayEquals(new int[] { 0, 0, 0, 2, 3, 3, 1 }, board.colHeights);

		exception.expect(IllegalArgumentException.class);
		board.makeMove(1, EMPTY);

	}

	@Test
	public void overrideMoveTest() {
		Connect4Board board = createTestBoard();
		assertArrayEquals(new int[] { 0, 0, 1, 2, 3, 3, 1 }, board.colHeights);

		board.makeMove(4, BLACK);
		board.makeMove(4, BLACK);
		assertArrayEquals(new int[] { 0, 0, 1, 2, 5, 3, 1 }, board.colHeights);

		exception.expect(IllegalArgumentException.class);
		board.makeMove(4, BLACK);
		assertArrayEquals(new int[] { 0, 0, 1, 2, 6, 3, 1 }, board.colHeights);

		assertArrayEquals(new int[] { 0, 1, 2, 3, 5, 6 }, board.getValidMoves().toArray());
	}

	@Test
	public void testEmptyBoard() {
		Connect4Board board = new Connect4Board();
		assertArrayEquals(new int[] { 0, 0, 0, 0, 0, 0, 0 }, board.colHeights);
		assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6 }, board.getValidMoves().toArray());
	}

	@Test
	public void testValidMoves() {
		Connect4Board board = createTestBoard();
		assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6 }, board.getValidMoves().toArray());
	}

	@Test
	public void testMakeMove() {
		Connect4Board board = createTestBoard();
		assertEquals(Board.EMPTY, board.getValue(52));

		board.makeMove(6, Board.BLACK);
		assertEquals(Board.BLACK, board.getValue(52));
		assertEquals(Board.BLACK, board.getValue(4, 6));
	}

	@Test
	public void testMoves() {
		Connect4Board board = createTestBoard();
		Connect4Board testBoard = new Connect4Board();
		testBoard.makeMove(2, BLACK);
		testBoard.makeMove(3, WHITE);
		testBoard.makeMove(6, BLACK);
		testBoard.makeMove(4, WHITE);
		testBoard.makeMove(3, BLACK);
		testBoard.makeMove(5, WHITE);
		testBoard.makeMove(4, BLACK);
		testBoard.makeMove(5, WHITE);
		testBoard.makeMove(4, BLACK);
		testBoard.makeMove(5, WHITE);
		assertEquals(board, testBoard);
	}

	@Test
	public void testOccupiedCells() {
		Connect4Board board = createTestBoard();
		assertArrayEquals(new int[] { 41, 49, 50, 57, 61 }, board.getOccupiedCells(Board.BLACK));
		assertArrayEquals(new int[] { 42, 51, 58, 59, 60 }, board.getOccupiedCells(Board.WHITE));
	}

	private static Connect4Board createTestBoard() {
		int[][] b = new int[Connect4Board.BOARD_HEIGHT][Connect4Board.BOARD_WIDTH];
		for (int i = 0; i < Connect4Board.BOARD_HEIGHT; ++i) {
			for (int j = 0; j < Connect4Board.BOARD_WIDTH; ++j) {
				b[i][j] = Board.EMPTY;
			}
		}
		b[5][2] = Board.BLACK;
		b[5][6] = Board.BLACK;
		b[4][3] = Board.BLACK;
		b[4][4] = Board.BLACK;
		b[3][4] = Board.BLACK;

		b[5][3] = Board.WHITE;
		b[5][4] = Board.WHITE;
		b[5][5] = Board.WHITE;
		b[4][5] = Board.WHITE;
		b[3][5] = Board.WHITE;
		System.out.println(new Connect4Board(b));
		return new Connect4Board(b);
	}

}
