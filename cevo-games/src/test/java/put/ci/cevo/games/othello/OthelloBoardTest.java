package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;

public class OthelloBoardTest {

	@Test
	public final void testIsValidMove() throws Exception {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		int[] players = new int[] { Board.BLACK, Board.WHITE };
		for (int i = 0; i < 30; ++i) {
			OthelloBoard board = createRandomBoard(random);
			for (int j = 0; j < OthelloBoard.SIZE.rows(); ++j) {
				for (int k = 0; k < OthelloBoard.SIZE.columns(); ++k) {
					for (int p : players) {
						int move = BoardUtils.toMarginPos(OthelloBoard.SIZE, j, k);
						Assert.assertEquals(board.simulateMove(move, p) != null, board.isValidMove(move, p));
					}
				}
			}
		}
	}

	private OthelloBoard createRandomBoard(RandomDataGenerator random) {
		int[][] b = new int[OthelloBoard.SIZE.rows()][OthelloBoard.SIZE.columns()];
		for (int i = 0; i < OthelloBoard.SIZE.rows(); ++i) {
			for (int j = 0; j < OthelloBoard.SIZE.columns(); ++j) {
				b[i][j] = random.nextInt(-1, 1);
			}
		}
		return new OthelloBoard(b);
	}
}
