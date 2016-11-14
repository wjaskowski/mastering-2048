package put.ci.cevo.util.sequence;

import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static put.ci.cevo.util.sequence.Sequences.range;

import org.junit.Test;

import put.ci.cevo.util.Pair;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;

public class SequenceTest {

	@Test
	public void reduceTest() {
		assertEquals(45, (int) range(10).reduce(new Transform<Pair<Integer, Integer>, Integer>() {
			@Override
			public Integer transform(Pair<Integer, Integer> pair) {
				return pair.first() + pair.second();
			}
		}));

		assertEquals(9, (int) range(10).reduce(new Transform<Pair<Integer, Integer>, Integer>() {
			@Override
			public Integer transform(Pair<Integer, Integer> pair) {
				return max(pair.first(), pair.second());
			}
		}));

		assertEquals(9, (int) range(10).reduce(Transforms.<Integer> max()));
		assertEquals(0, (int) range(10).reduce(Transforms.<Integer> min()));
	}

	@Test
	public void enumerateTest() {
		assertEquals(asList(0, 1, 2), range(3).enumerate().map(Transforms.<Integer> getFirst()).toList());
	}
}
