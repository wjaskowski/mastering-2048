package put.ci.cevo.games.tetris;

import org.junit.Assert;
import org.junit.Test;

public class TetrominoTest {

	@Test
	public void testGetBaseTetrominoTiles() throws Exception {
		int[][] baseTiles = Tetromino.S.getTiles(0);
		Assert.assertArrayEquals(baseTiles[0], new int[] { 0, 1, 1 });
		Assert.assertArrayEquals(baseTiles[1], new int[] { 1, 1, 0 });
		Assert.assertArrayEquals(baseTiles[2], new int[] { 0, 0, 0 });

		baseTiles = Tetromino.Z.getTiles(0);
		Assert.assertArrayEquals(baseTiles[0], new int[] { 1, 1, 0 });
		Assert.assertArrayEquals(baseTiles[1], new int[] { 0, 1, 1 });
		Assert.assertArrayEquals(baseTiles[2], new int[] { 0, 0, 0 });
	}

	@Test
	public void testGetRotatedTetrominoTiles() throws Exception {
		int[][] rotatedTiles = Tetromino.S.getTiles(1);
		Assert.assertArrayEquals(rotatedTiles[0], new int[] { 1, 0, 0 });
		Assert.assertArrayEquals(rotatedTiles[1], new int[] { 1, 1, 0 });
		Assert.assertArrayEquals(rotatedTiles[2], new int[] { 0, 1, 0 });

		rotatedTiles = Tetromino.Z.getTiles(1);
		Assert.assertArrayEquals(rotatedTiles[0], new int[] { 0, 1, 0 });
		Assert.assertArrayEquals(rotatedTiles[1], new int[] { 1, 1, 0 });
		Assert.assertArrayEquals(rotatedTiles[2], new int[] { 1, 0, 0 });
	}

	@Test
	public void testGetWidth() {
		Assert.assertEquals(3, Tetromino.S.getWidth(0));
		Assert.assertEquals(3, Tetromino.Z.getWidth(0));

		Assert.assertEquals(2, Tetromino.S.getWidth(1));
		Assert.assertEquals(2, Tetromino.Z.getWidth(1));
	}

	@Test
	public void testGetBottomTile() {
		Assert.assertArrayEquals(new int[] { 2, 2, 1 }, Tetromino.S.getBottomTiles(0));
		Assert.assertArrayEquals(new int[] { 2, 3 }, Tetromino.S.getBottomTiles(1));

		Assert.assertArrayEquals(new int[] { 1, 2, 2 }, Tetromino.Z.getBottomTiles(0));
		Assert.assertArrayEquals(new int[] { 3, 2 }, Tetromino.Z.getBottomTiles(1));
	}
}