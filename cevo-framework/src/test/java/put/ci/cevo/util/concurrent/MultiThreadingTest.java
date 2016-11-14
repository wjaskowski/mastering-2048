package put.ci.cevo.util.concurrent;

import org.junit.Test;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static put.ci.cevo.util.sequence.Sequences.range;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.intAdd;

public class MultiThreadingTest {

	@Test
	public void testMultiThreadedWork() {
		final List<Integer> seq = Collections.synchronizedList(new ArrayList<Integer>());
		new ThreadedContext().submit(new ThreadedContext.Worker<Integer, Void>() {
			@Override
			public Void process(Integer number, ThreadedContext context) throws Exception {
				seq.add(range(number).aggregate(intAdd()));
				return null;
			}
		}, range(1, 10).info("test"));

		Collections.sort(seq);
		assertEquals(0, seq.get(0).intValue());
		assertEquals(15, seq.get(5).intValue());
		assertEquals(36, seq.get(8).intValue());
	}

	@Test
	public void testProcessingThreadedWork() {
		ThreadedContext random = new ThreadedContext(123);
		List<Integer> result = random.invoke(new ThreadedContext.Worker<Integer, Integer>() {
			@Override
			public Integer process(Integer piece, ThreadedContext random) {
				return piece + 1;
			}
		}, range(1, 10)).toList();

		assertEquals(2, result.get(0).intValue());
		assertEquals(10, result.get(8).intValue());
	}
}
