package put.ci.cevo.util;

import static com.google.common.primitives.Ints.asList;
import static com.google.common.primitives.Ints.toArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomUtils {

	/**
	 * Returns a sample without replacement. Complexity: O(m). This is an iterative implementation of the Floyd's random
	 * sampling algorithm (cf. http://dl.acm.org/citation.cfm?id=315746&dl=ACM&coll=DL). It's much faster than the other
	 * implementations, especially for small <code>m</code> and large <code>Lists</code>.
	 */
	public static <T> List<T> sample(List<T> items, int m, RandomDataGenerator random) {
		Preconditions.checkArgument(0 <= m, "Sample size cannot be negative");
		Preconditions.checkArgument(m <= items.size(), "Sample size cannot be greater than list size!");
		final IntDoubleLinkedSet indices = new IntDoubleLinkedSet();
		final List<T> res = new ArrayList<>(m);
		final int n = items.size();
		for (int i = n - m; i < n; i++) {
			int pos = nextInt(0, i, random);
			if (indices.contains(pos)) {
				indices.add(i);
				res.add(items.get(i));
			} else {
				indices.add(pos);
				res.add(items.get(pos));
			}
		}
		return res;
	}

	/**
	 * Returns a sample without replacement. Use it when m can be greater than items.size()
	 */
	public static <T> List<T> sampleMore(List<T> items, int m, RandomDataGenerator random) {
		final List<T> res = new ArrayList<>(m);
		int remainder = m;
		while (remainder >= items.size()) {
			res.addAll(items);
			remainder -= items.size();
		}
		res.addAll(sample(items, remainder, random));
		RandomUtils.shuffle(res, random);
		return res;
	}

	/** Returns a sample without replacement. Forbidden elements are guaranteed not to be sampled. */
	public static <T> List<T> sample(List<T> items, int m, Set<T> forbidden, RandomDataGenerator random) {
		List<T> copy = Lists.newArrayList(items);
		copy.removeAll(forbidden);
		if (copy.isEmpty()) {
			return Collections.emptyList();
		}
		return sample(copy, m, random);
	}

	/** Returns a sample with replacement. */
	public static <T> List<T> sampleWithReplacement(List<T> items, int m, RandomDataGenerator random) {
		final List<T> res = new ArrayList<>(m);
		for (int i = 0; i < m; i++) {
			res.add(pickRandom(items, random));
		}
		return res;
	}

	/** Shuffles an array of integers. Implements Fisher-Yates algorithm. */
	public static int[] shuffleInts(int[] ar, RandomDataGenerator random) {
		return toArray(shuffle(asList(ar), random));
	}

	/**
	 * Shuffles a list. An implementation of a variant of Fisher-Yates-Knuth algorithm (cf.
	 * http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle)
	 */
	public static <T> List<T> shuffle(List<T> items, RandomDataGenerator random) {
		return shuffle(items, items.size() - 1, random);
	}

	/**
	 * Get a sample without replacement. There is slightly quicker algorithm by Knuth (Algorithm 3.4.2S of Knuth's book
	 * Seminumeric Algorithms), implemented here http://www.javamex.com/tutorials/random_numbers/random_sample.shtml
	 * 
	 * Note: Method sample() is quicker by ca. 30% for arrays of size less than 1000. For larger arrays (e.g. 10000) and
	 * big samples (e.g. 8000) this one is quicker (e.g. 2 times)
	 */
	public static <T> List<T> randomSampleViaShuffle(Iterable<T> items, int sampleSize, RandomDataGenerator random) {
		List<T> copy = Lists.newArrayList(items);
		shuffle(copy, sampleSize, random);
		return copy.subList(0, sampleSize);
	}

	/**
	 * Shuffles only first numFirstElements. The rest of the array also may be changed, but we don't care how
	 */
	private static <T> List<T> shuffle(List<T> items, int numFirstElements, RandomDataGenerator random) {
		final int firstElements = Math.max(0, Math.min(numFirstElements, items.size() - 1));
		for (int i = 0; i < firstElements; ++i) {
			int nextPos = nextInt(i, items.size() - 1, random);
			T tmp = items.get(nextPos);
			items.set(nextPos, items.get(i));
			items.set(i, tmp);
		}
		return items;
	}

	/** Same as {@link RandomDataGenerator#nextInt(int, int)}, but does not throw exception when lower == upper */
	public static int nextInt(int lower, int upper, RandomDataGenerator random) {
		return lower == upper ? lower : random.nextInt(lower, upper);
	}

	/** Same as random.nextDouble(lower, upper, true), but does not throw exception when lower == upper */
	public static double nextUniform(double lower, double upper, RandomDataGenerator random) {
		return lower == upper ? lower : random.nextUniform(lower, upper, true);
	}

	/** Picks a random item from a list */
	public static <T> T pickRandom(List<T> items, RandomDataGenerator random) {
		Preconditions.checkArgument(items.size() > 0);
		return items.size() == 1 ? items.get(0) : items.get(nextInt(0, items.size() - 1, random));
	}

	/** Picks a random item from an int array */
	public static int pickRandom(int[] arr, RandomDataGenerator random) {
		Preconditions.checkArgument(arr.length > 0);
		return arr[nextInt(0, arr.length - 1, random)];
	}

	/** Picks a random item from an T array */
	public static <T> T pickRandom(T[] arr, RandomDataGenerator random) {
		Preconditions.checkArgument(arr.length > 0);
		return arr[nextInt(0, arr.length - 1, random)];
	}

	// TODO: rename to nextUniformDoubleVector
	public static double[] randomDoubleVector(int n, double minValue, double maxValue, RandomDataGenerator random) {
		double[] vector = new double[n];
		for (int i = 0; i < n; i++) {
			vector[i] = nextUniform(minValue, maxValue, random);
		}
		return vector;
	}

	public static float[] nextFloatVector(int n, float minValue, float maxValue, RandomDataGenerator random) {
		float[] vector = new float[n];
		for (int i = 0; i < n; i++) {
			vector[i] = (float)nextUniform(minValue, maxValue, random);
		}
		return vector;
	}

	/** Returns index. Assuming that probabilities sum up to 1 */
	public static int sampleAccordingToDistribution(RandomDataGenerator random, double[] probabilities) {
		double rand = nextUniform(0, 1, random);
		double sum = 0.0;
		for (int i = 0; i < probabilities.length; ++i) {
			sum += probabilities[i];
			if (rand < sum)
				return i;
		}
		return probabilities.length - 1;
	}
}