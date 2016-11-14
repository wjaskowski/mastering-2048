package put.ci.cevo.util;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilsTest {

	@Test
	public void testFlatten() throws Exception {
		double[][] arr2d = new double[][] { { 1, 2, 3 }, { 4, 5 }, { 6, 7, 8, 9 }, {} };
		double[] arr1d = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		Assert.assertArrayEquals(arr1d, ArrayUtils.flatten(arr2d), 0);
	}

	@Test
	public void testTransposed() {
		int[][] arr = new int[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, };
		int[][] transposed = ArrayUtils.transposed(arr);

		Assert.assertArrayEquals(new int[][] { { 1, 4, 7 }, { 2, 5, 8 }, { 3, 6, 9 } }, transposed);
	}

	@Test
	public void testTransposed2() {
		int[][] arr = new int[][] { { 0 } };
		Assert.assertArrayEquals(new int[][] { { 0 } }, ArrayUtils.transposed(arr));
	}
}
