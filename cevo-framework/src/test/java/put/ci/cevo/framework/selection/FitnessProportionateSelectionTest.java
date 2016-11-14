package put.ci.cevo.framework.selection;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

public class FitnessProportionateSelectionTest {

	@Test
	public void testSelection() {
		SelectionStrategy<String, String> strategy = new FitnessProportionateSelection<>(4);
		List<EvaluatedIndividual<String>> population = new ArrayList<EvaluatedIndividual<String>>(4);

		population.add(new EvaluatedIndividual<>("a", 10.0));
		population.add(new EvaluatedIndividual<>("b", 9.5));
		population.add(new EvaluatedIndividual<>("c", 9.8));
		population.add(new EvaluatedIndividual<>("d", 1.6));
		population.add(new EvaluatedIndividual<>("e", 5.6));
		population.add(new EvaluatedIndividual<>("f", 2.1));

		ThreadedContext threadedRandom = new ThreadedContext(123);
		assertEquals(of("a", "b", "a", "f"), strategy.select(population, threadedRandom.getRandomForThread()));
	}
}
