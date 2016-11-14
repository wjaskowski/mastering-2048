package put.ci.cevo.util;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

public class RandomUtilsTest {

	@Test
	public void testShuffle() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<Integer> arr = Arrays.asList(0, 1, 2, 3, 4);
		List<Integer> copy = new ArrayList<Integer>(arr);
		RandomUtils.shuffle(copy, random);
		Collections.sort(copy);
		assertEquals(arr, copy);
	}

	@Test
	public void testRandomSampleViaShuffle() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<Integer> arr = Arrays.asList(0, 1, 2, 3, 4);
		assertEquals(3, RandomUtils.randomSampleViaShuffle(arr, 3, random).size());
		assertEquals(5, RandomUtils.randomSampleViaShuffle(arr, 5, random).size());
		assertEquals(0, RandomUtils.randomSampleViaShuffle(arr, 0, random).size());
		assertEquals(1, RandomUtils.randomSampleViaShuffle(arr, 1, random).size());
	}

	@Test
	public void testSample() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<Integer> arr = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
		assertEquals(newHashSet(0, 2, 3, 5, 6, 8, 9, 11), newHashSet(RandomUtils.sample(arr, 8, random)));
	}

	@Test
	public void testSampleMore() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<Integer> arr = Arrays.asList(0, 1);
		List<Integer> integers = RandomUtils.sampleMore(arr, 41, random);
		int cnt0 = 0, cnt1 = 0;
		for (Integer i : integers) {
			cnt0 += (i == 0 ? 1 : 0);
			cnt1 += (i == 1 ? 1 : 0);
		}
		Assert.assertEquals(1, Math.abs(cnt0 - cnt1));
	}

	@Test
	public void testDuplicatesSampling() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<Integer> arr = Arrays.asList(1, 1, 1, 1, 1);
		assertEquals(3, RandomUtils.sample(arr, 3, random).size());
	}

	@Test
	public void testPickRandomSimple() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<Integer> arr = Arrays.asList(1);
		assertEquals(1, RandomUtils.pickRandom(arr, random).intValue());
	}

	@Test
	public void testPickRandomAllPicked() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<Integer> arr = Arrays.asList(0, 1, 2, 3, 4);

		boolean picked[] = new boolean[5];
		int numPicked = 0;
		for (int i = 0; i < 10000; ++i) {
			int number = RandomUtils.pickRandom(arr, random);
			if (!picked[number]) {
				numPicked += 1;
			}
			picked[number] = true;
			if (numPicked == 5) {
				break;
			}
		}
		assertEquals(5, numPicked);
	}

	@Test
	public void testNextInt() {
		RandomDataGenerator random1 = new RandomDataGenerator(new MersenneTwister(123));
		RandomDataGenerator random2 = new RandomDataGenerator(new MersenneTwister(123));
		assertEquals(random1.nextInt(12, 19), RandomUtils.nextInt(12, 19, random2));
		assertEquals(random1.nextInt(120, 129), RandomUtils.nextInt(120, 129, random2));
		assertEquals(120, RandomUtils.nextInt(120, 120, random2));
		assertEquals(0, RandomUtils.nextInt(0, 0, random2));
	}
}