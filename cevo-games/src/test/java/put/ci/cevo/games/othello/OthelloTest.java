package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;

public class OthelloTest {

	@Test
	public void testPlayImplOthelloPlayerOthelloPlayerOthelloBoardIntRandomDataGenerator() throws Exception {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

		Othello othello = new Othello();
		{
			// @formatter:off
    		OthelloBoard initialBoard = new OthelloBoard(new int[][] {
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 0, 1 },
    		});
    		OthelloBoard expectedBoard = new OthelloBoard(new int[][] {
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    		});
    		// @formatter:on

			OthelloWPCPlayer player = new OthelloStandardWPCHeuristicPlayer().create();

			OthelloBoard result = othello.playImpl(player, player, initialBoard, Board.BLACK, random);
			Assert.assertEquals(expectedBoard, result);
		}

		{
			// @formatter:off
    		OthelloBoard board = new OthelloBoard(new int[][] {
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 1 },
    		});
    		OthelloBoard expectedBoard = new OthelloBoard(new int[][] {
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    		});
    		// @formatter:on

			OthelloWPCPlayer player = new OthelloStandardWPCHeuristicPlayer().create();
			OthelloBoard result = new Othello().playImpl(player, player, board, Board.WHITE, random);

			Assert.assertEquals(expectedBoard, result);
		}

		{
			// @formatter:off
    		OthelloBoard initialBoard = new OthelloBoard(new int[][] {
    			{ 1, 2, 0, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 0, 1 },
    		});
    		OthelloBoard expectedBoard = new OthelloBoard(new int[][] {
    			{ 0, 0, 0, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    		});
    		// @formatter:on

			OthelloWPCPlayer player = new OthelloStandardWPCHeuristicPlayer().create();

			OthelloBoard result = new Othello().playImpl(player, player, initialBoard, Board.BLACK, random);
			Assert.assertEquals(expectedBoard, result);
		}

		{
			// @formatter:off
    		OthelloBoard initialBoard = new OthelloBoard(new int[][] {
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 1, 0, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 0, 1 },
    		});
    		OthelloBoard expectedBoard = new OthelloBoard(new int[][] {
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 2 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 2, 0 },
    			{ 2, 2, 2, 2, 2, 2, 0, 0 },
    		});
    		// @formatter:on

			OthelloWPCPlayer player = new OthelloStandardWPCHeuristicPlayer().create();

			OthelloBoard result = new Othello().playImpl(player, player, initialBoard, Board.WHITE, random);
			Assert.assertEquals(expectedBoard, result);
		}
	}
}
