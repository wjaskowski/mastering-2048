package put.ci.cevo.games.encodings.ntuple;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.util.ArrayUtils;

public class RotationMirrorSymmetryExpanderTest {

	@Test
	public void testGetSymmetries8() throws Exception {
		RectSize B = new RectSize(8);
		int LOC = BoardUtils.toMarginPos(B, 2, 1);

		RotationMirrorSymmetryExpander exp = new RotationMirrorSymmetryExpander(B);
		int[] symmetries = exp.getSymmetries(LOC);

		// @formatter:off
		int[] expected = {
			LOC,
			BoardUtils.toMarginPos(B, 1, 2),
			BoardUtils.toMarginPos(B, 2, 6),
			BoardUtils.toMarginPos(B, 1, 5),
			BoardUtils.toMarginPos(B, 5, 1),
			BoardUtils.toMarginPos(B, 6, 2),
			BoardUtils.toMarginPos(B, 6, 5),
			BoardUtils.toMarginPos(B, 5, 6)
		};
		// @formatter:on

		Assert.assertEquals(symmetries[0], LOC);

		Assert.assertArrayEquals(ArrayUtils.sorted(expected), ArrayUtils.sorted(symmetries));
	}

	@Test
	public void testGetSymmetries4() throws Exception {
		RectSize B = new RectSize(8);
		int LOC = BoardUtils.toMarginPos(B, 3, 4);

		RotationMirrorSymmetryExpander exp = new RotationMirrorSymmetryExpander(B);
		int[] symmetries = exp.getSymmetries(LOC);

		// @formatter:off
		int[] expected = {
			LOC,
			LOC,
			BoardUtils.toMarginPos(B, 3, 3),
			BoardUtils.toMarginPos(B, 3, 3),
			BoardUtils.toMarginPos(B, 4, 3),
			BoardUtils.toMarginPos(B, 4, 3),
			BoardUtils.toMarginPos(B, 4, 4),
			BoardUtils.toMarginPos(B, 4, 4)
		};
		// @formatter:on

		Assert.assertArrayEquals(ArrayUtils.sorted(expected), ArrayUtils.sorted(symmetries));
	}
}
