package put.ci.cevo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;

public class CollectionUtils {

	public static double sum(Collection<Double> arr) {
		double s = 0;
		for (Double a : arr) {
			s += a;
		}
		return s;
	}

	public static double average(Collection<Double> arr) {
		if (arr.size() > 0) {
			return sum(arr) / arr.size();
		} else {
			return 0;
		}
	}

	public static <T> List<T> concat(Collection<T> arr1, Collection<T> arr2) {
		ArrayList<T> res = new ArrayList<>(arr1);
		res.addAll(arr2);
		return res;
	}

	public static <T> List<T> flatten(Collection<? extends Collection<T>> arr) {
		ArrayList<T> res = new ArrayList<>();
		for (Collection<T> list : arr) {
			res.addAll(list);
		}
		return res;
	}

	public static List<Integer> range(int max) {
		Preconditions.checkArgument(0 <= max);
		ArrayList<Integer> arr = new ArrayList<>(max);
		for (int i = 0; i < max; ++i)
			arr.add(i);
		return arr;
	}
}
