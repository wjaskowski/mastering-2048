package put.ci.cevo.games.encodings.ntuple;

import static put.ci.cevo.games.board.BoardUtils.toMarginPos;
import static put.ci.cevo.util.ArrayUtils.sorted;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryUtils;
import put.ci.cevo.util.ArrayUtils;

public class NTupleUtilsTest {

	@Test
	public void testCreateSymmetricIntArraySymmetryExpander8() throws Exception {
		RectSize BS = new RectSize(8);
		int[] tuple = { toMarginPos(BS, 1, 1), toMarginPos(BS, 2, 1), toMarginPos(BS, 3, 1), toMarginPos(BS, 3, 2) };

		int[][] expanded = SymmetryUtils.createSymmetric(tuple, new RotationMirrorSymmetryExpander(BS)).toArray(new int[0][]);

		// @formatter:off
		int[][] expected = {
			{ toMarginPos(BS, 1, 1), toMarginPos(BS, 2, 1), toMarginPos(BS, 3, 1), toMarginPos(BS, 3, 2) },
			{ toMarginPos(BS, 1, 1), toMarginPos(BS, 1, 2), toMarginPos(BS, 1, 3), toMarginPos(BS, 2, 3) },
			{ toMarginPos(BS, 1, 6), toMarginPos(BS, 1, 5), toMarginPos(BS, 1, 4), toMarginPos(BS, 2, 4) },
			{ toMarginPos(BS, 1, 6), toMarginPos(BS, 2, 6), toMarginPos(BS, 3, 6), toMarginPos(BS, 3, 5) },
			{ toMarginPos(BS, 6, 1), toMarginPos(BS, 5, 1), toMarginPos(BS, 4, 1), toMarginPos(BS, 4, 2) },
			{ toMarginPos(BS, 6, 1), toMarginPos(BS, 6, 2), toMarginPos(BS, 6, 3), toMarginPos(BS, 5, 3) },
			{ toMarginPos(BS, 6, 6), toMarginPos(BS, 5, 6), toMarginPos(BS, 4, 6), toMarginPos(BS, 4, 5) },
			{ toMarginPos(BS, 6, 6), toMarginPos(BS, 6, 5), toMarginPos(BS, 6, 4), toMarginPos(BS, 5, 4) },
		};
		// @formatter:on

		Assert.assertArrayEquals(expanded[0], expected[0]);

		sortSomehow(expanded);
		sortSomehow(expected);

		Assert.assertEquals(expected.length, expanded.length);
		for (int i = 0; i < expected.length; ++i) {
			Assert.assertArrayEquals(expected[i], expanded[i]);
		}
	}

	@Test
	public void testCreateSymmetricIntArraySymmetryExpander4() throws Exception {
		RectSize BS = new RectSize(8);
		int[] tuple = { toMarginPos(BS, 1, 1), toMarginPos(BS, 2, 2) };

		int[][] expanded = SymmetryUtils.createSymmetric(tuple, new RotationMirrorSymmetryExpander(BS)).toArray(new int[0][]);

		// @formatter:off
		int[][] expected = {
			{ toMarginPos(BS, 1, 1), toMarginPos(BS, 2, 2) },
			{ toMarginPos(BS, 1, 6), toMarginPos(BS, 2, 5) },
			{ toMarginPos(BS, 6, 1), toMarginPos(BS, 5, 2) },
			{ toMarginPos(BS, 6, 6), toMarginPos(BS, 5, 5) },
		};
		// @formatter:on

		Assert.assertArrayEquals(expanded[0], expected[0]);

		sortSomehow(expanded);
		sortSomehow(expected);

		Assert.assertEquals(expected.length, expanded.length);
		for (int i = 0; i < expected.length; ++i) {
			Assert.assertArrayEquals(expected[i], expanded[i]);
		}
	}

	@Test
	public void testCreateSymmetricIntArraySymmetryExpanderMiddle() throws Exception {
		RectSize BS = new RectSize(8);
		int[] tuple = { toMarginPos(BS, 3, 3), toMarginPos(BS, 3, 4) };

		int[][] expanded = SymmetryUtils.createSymmetric(tuple, new RotationMirrorSymmetryExpander(BS)).toArray(new int[0][]);

		// @formatter:off
		int[][] expected = {
			{ toMarginPos(BS, 3, 3), toMarginPos(BS, 3, 4) },
			{ toMarginPos(BS, 3, 4), toMarginPos(BS, 4, 4) },
			{ toMarginPos(BS, 4, 3), toMarginPos(BS, 4, 4) },
			{ toMarginPos(BS, 4, 3), toMarginPos(BS, 3, 3) },
			{ toMarginPos(BS, 3, 4), toMarginPos(BS, 3, 3) },
			{ toMarginPos(BS, 4, 4), toMarginPos(BS, 3, 4) },
			{ toMarginPos(BS, 4, 4), toMarginPos(BS, 4, 3) },
			{ toMarginPos(BS, 3, 3), toMarginPos(BS, 4, 3) },
		};
		// @formatter:on

		Assert.assertArrayEquals(expanded[0], expected[0]);

		sortSomehow(expanded);
		sortSomehow(expected);

		Assert.assertEquals(expected.length, expanded.length);
		for (int i = 0; i < expected.length; ++i) {
			Assert.assertArrayEquals(sorted(expected[i]), sorted(expanded[i]));
		}
	}

	@Test
	public void testCreateSymmetricIntArraySymmetryExpanderSmall() throws Exception {
		RectSize BS = new RectSize(2);
		int[] tuple = { toMarginPos(BS, 0, 0), toMarginPos(BS, 1, 1) };

		int[][] expanded = SymmetryUtils.createSymmetric(tuple, new RotationMirrorSymmetryExpander(BS)).toArray(new int[0][]);

		// @formatter:off
		int[][] expected = {
			{ toMarginPos(BS, 0, 0), toMarginPos(BS, 1, 1) },
			{ toMarginPos(BS, 0, 1), toMarginPos(BS, 1, 0) },
			{ toMarginPos(BS, 1, 1), toMarginPos(BS, 0, 0) },
			{ toMarginPos(BS, 1, 0), toMarginPos(BS, 0, 1) },
		};
		// @formatter:on

		Assert.assertArrayEquals(expanded[0], expected[0]);

		sortSomehow(expanded);
		sortSomehow(expected);

		Assert.assertEquals(expected.length, expanded.length);
		for (int i = 0; i < expected.length; ++i) {

			Assert.assertArrayEquals(sorted(expected[i]), sorted(expanded[i]));
		}
	}

	private static void sortSomehow(int[][] arr2d) {
		Arrays.sort(arr2d, new Comparator<int[]>() {
			@Override
			public int compare(int[] o1, int[] o2) {
				return Arrays.hashCode(sorted(o1)) - Arrays.hashCode(sorted(o2));
			}
		});
	}
}
