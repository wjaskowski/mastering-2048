package put.ci.cevo.games.board;

import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.util.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class BoardUtilsTest {
	@Test
	public void testSymmetric() throws Exception {
		Board board = new OthelloBoard(new int[][] {
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,0,0,0,0,0,0,},
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
		});
		List<int[][]> symmetric = BoardUtils.createSymmetricBoards(board,
			new RotationMirrorSymmetryExpander(OthelloBoard.SIZE));

		List<int[][]> expected = Arrays.asList(new int[][] {
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,0,0,0,0,0,0,},
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
		}, new int[][] {
			{1,1,1,1,1,1,1,1,},
			{1,1,1,2,1,1,1,1,},
			{1,1,1,2,1,1,1,1,},
			{1,1,1,2,1,1,1,1,},
			{1,1,1,2,1,1,1,1,},
			{0,0,0,0,0,0,1,1,},
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
		}, new int[][] {
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
			{1,1,0,0,0,0,0,0,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,2,1,1,1,},
			{1,1,1,1,1,1,1,1,},
		}, new int[][] {
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
			{0,0,0,0,0,0,1,1,},
			{1,1,1,2,1,1,1,1,},
			{1,1,1,2,1,1,1,1,},
			{1,1,1,2,1,1,1,1,},
			{1,1,1,2,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
		}, new int[][] {
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,2,2,2,2,0,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,1,1,1,1,0,1,1,},
		}, new int[][] {
			{1,1,1,1,1,0,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,2,2,2,2,0,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,1,1,1,1,0,1,1,},
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
		}, new int[][] {
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,0,2,2,2,2,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,0,1,1,1,1,1,},
		}, new int[][] {
			{1,1,0,1,1,1,1,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,0,2,2,2,2,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,0,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
			{1,1,1,1,1,1,1,1,},
		});

		for (int i =0; i<8; ++i) {
			// Actually the order may change after some refactorization... TODO
			Assert.assertArrayEquals(ArrayUtils.flatten(expected.get(i)), ArrayUtils.flatten(symmetric.get(i)));
		}
	}
}