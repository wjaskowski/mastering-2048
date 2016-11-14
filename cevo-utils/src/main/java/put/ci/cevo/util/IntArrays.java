package put.ci.cevo.util;

import com.google.common.base.Preconditions;

public class IntArrays {

	private IntArrays() {

	}

	/**
	 * Creates an integer array: [from,from+1,...,toInclusive-1,toInclusive]
	 */
	public static int[] range(int from, int toInclusive) {
		Preconditions.checkArgument(from <= toInclusive);
		int arr[] = new int[toInclusive - from + 1];
		int cnt = 0;
		for (int i = from; i <= toInclusive; ++i) {
			arr[cnt++] = i;
		}
		return arr;
	}

}
