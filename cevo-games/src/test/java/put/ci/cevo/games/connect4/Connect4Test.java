package put.ci.cevo.games.connect4;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.connect4.players.Connect4Player;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static put.ci.cevo.games.board.Board.BLACK;
import static put.ci.cevo.games.board.BoardUtils.boardFromString;
import static put.ci.cevo.games.connect4.Connect4Board.BOARD_HEIGHT;
import static put.ci.cevo.games.connect4.Connect4Board.BOARD_WIDTH;

public class Connect4Test {

	private class MockConnect4Player implements Connect4Player {

		private final List<Integer> blackMoves = ImmutableList.of(2, 3, 4, 6, 4, 5);
		private final List<Integer> whiteMoves = ImmutableList.of(3, 4, 5, 5, 5, 6);

		private int turn = 0;

		@Override
		public int getMove(Connect4Board board, int player, int[] validMoves, RandomDataGenerator random) {
			return player == Board.BLACK ? blackMoves.get(turn) : whiteMoves.get(turn++);
		}
	}

	@Test
	public void testConnect4Game() {
		Connect4 connect4 = new Connect4();
		MockConnect4Player player = new MockConnect4Player();
		GameOutcome outcome = connect4.play(player, player, null);
		assertEquals(new GameOutcome(1.0, 0.0), outcome);
	}

	@Test
	public void testIsConnect4() {
		Connect4Board board = createTestBoard();
		Assert.assertEquals(false, Connect4.isConnect4(Board.BLACK, board));

		// make winning move
		board.makeMove(5, Board.BLACK);
		Assert.assertEquals(true, Connect4.isConnect4(Board.BLACK, board));

		// undo the last move
		board.setValue(3, 5, Board.EMPTY);
		Assert.assertEquals(true, Connect4.isConnect4(board.moveToPos(5), Board.BLACK, board));
	}

	@Test
	public void testWinningMoves() {
		final String boardString =
				"1 1 2 1 2 1 1 \n" +
				"1 1 0 1 0 1 0 \n" +
				"1 2 0 1 2 1 2 \n" +
				"2 0 2 1 2 0 0 \n" +
				"2 0 0 1 0 0 2 \n" +
				"0 2 2 0 2 2 0 ";

		Connect4Board board = new Connect4Board(boardFromString(boardString, BOARD_HEIGHT, BOARD_WIDTH));
		assertTrue(Connect4.isWinningMove(3, BLACK, board));
	}

	private static Connect4Board createTestBoard() {
		int[][] b = new int[BOARD_HEIGHT][BOARD_WIDTH];
		for (int i = 0; i < BOARD_HEIGHT; ++i) {
			for (int j = 0; j < BOARD_WIDTH; ++j) {
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
		return new Connect4Board(b);
	}

}
