package put.ci.cevo.framework.selection;

import static java.util.Collections.frequency;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

public class SigmaScalingTest {

	@Test
	public void testSelection() {
		SelectionStrategy<String, String> strategy = new SigmaScaling<>(4);
		List<EvaluatedIndividual<String>> population = new ArrayList<EvaluatedIndividual<String>>(4);

		EvaluatedIndividual<String> a = new EvaluatedIndividual<String>("a", 10.0);
		EvaluatedIndividual<String> b = new EvaluatedIndividual<String>("b", 4.5);
		EvaluatedIndividual<String> c = new EvaluatedIndividual<String>("c", 1.0);
		EvaluatedIndividual<String> d = new EvaluatedIndividual<String>("d", 0.5);

		population.add(a);
		population.add(b);
		population.add(c);
		population.add(d);

		List<String> selection = strategy.select(population, new ThreadedContext(123).getRandomForThread());

		assertEquals(4, selection.size());

		int aCount = frequency(selection, a.getIndividual());
		assertTrue("Individual selected wrong number of times (should be 1 or 2, was " + aCount + ")", aCount >= 1
			&& aCount <= 2);

		int bCount = frequency(selection, b.getIndividual());
		assertTrue("Individual selected wrong number of times (should be 1 or 2, was " + bCount + ")", bCount >= 1
			&& bCount <= 2);

		int cCount = frequency(selection, c.getIndividual());
		assertTrue("Individual selected wrong number of times (should be 0 or 1, was " + cCount + ")", cCount <= 1);

		int dCount = frequency(selection, d.getIndividual());
		assertTrue("Individual selected wrong number of times (should be 0 or 1, was " + dCount + ")", dCount <= 1);
	}

	@Test
	public void testNoVariance() {
		SelectionStrategy<String, String> strategy = new SigmaScaling<>(4);
		List<EvaluatedIndividual<String>> population = new ArrayList<EvaluatedIndividual<String>>(4);

		EvaluatedIndividual<String> a = new EvaluatedIndividual<String>("a", 10.0);
		EvaluatedIndividual<String> b = new EvaluatedIndividual<String>("b", 10.0);
		EvaluatedIndividual<String> c = new EvaluatedIndividual<String>("c", 10.0);
		EvaluatedIndividual<String> d = new EvaluatedIndividual<String>("d", 10.0);

		population.add(a);
		population.add(b);
		population.add(c);
		population.add(d);

		List<String> selection = strategy.select(population, new ThreadedContext(123).getRandomForThread());

		assertEquals(4, selection.size());
		assertEquals(1, frequency(selection, a.getIndividual()));
		assertEquals(1, frequency(selection, b.getIndividual()));
		assertEquals(1, frequency(selection, c.getIndividual()));
		assertEquals(1, frequency(selection, d.getIndividual()));
	}
}
