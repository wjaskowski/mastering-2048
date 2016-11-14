package put.ci.cevo.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.meanValue;

import org.junit.Test;

import put.ci.cevo.util.sequence.Sequences;
import put.ci.cevo.util.sequence.transforms.Transform;

public class AggregatesTest {

	@Test
	public void testInjection() {
		assertEquals(4.5, Sequences.range(10).transform(new Transform<Integer, Double>() {
			@Override
			public Double transform(Integer object) {
				return object.doubleValue();
			}
		}).aggregate(meanValue()), 0);

		assertEquals(0.6113, Sequences.seq(asList(0.6520, 0.5920, 0.5900)).aggregate(meanValue()), 0.0001);
	}
}
