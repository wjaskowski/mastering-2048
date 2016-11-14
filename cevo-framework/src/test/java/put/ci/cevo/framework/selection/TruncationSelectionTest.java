package put.ci.cevo.framework.selection;

import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;

public class TruncationSelectionTest {

	private static final ThreadedContext context = new ThreadedContext(123);

	private List<EvaluatedIndividual<String>> population;

	@Before
	public void setup() {
		population = new ArrayList<>();
		population.add(new EvaluatedIndividual<String>("a", 10.0));
		population.add(new EvaluatedIndividual<String>("b", 9.5));
		population.add(new EvaluatedIndividual<String>("c", 9.8));
		population.add(new EvaluatedIndividual<String>("d", 1.6));
		population.add(new EvaluatedIndividual<String>("e", 5.6));
		population.add(new EvaluatedIndividual<String>("f", 2.1));
	}

	@Test
	public void testSelection1() {
		SelectionStrategy<String, String> strategy = new TruncationSelection<>(1, 2);
		assertEquals(of("a", "b"), strategy.select(population, context.getRandomForThread()));
	}

	@Test
	public void testSelection2() {
		SelectionStrategy<String, String> strategy = new TruncationSelection<>(0.5, 5);
		assertEquals(of("a", "b", "c", "a", "b"), strategy.select(population, context.getRandomForThread()));
	}

	@Test
	public void testSelection3() {
		SelectionStrategy<String, String> strategy = new TruncationSelection<>(0.167, 3);
		assertEquals(of("a", "a", "a"), strategy.select(population, context.getRandomForThread()));
	}

	@Test
	public void testSelection4() {
		SelectionStrategy<String, String> strategy = new TruncationSelection<>(0.5);
		assertEquals(of("a", "b", "c", "a", "b", "c"), strategy.select(population, context.getRandomForThread()));
	}

	@Test
	public void testSelection5() {
		SelectionStrategy<String, String> strategy = new TruncationSelection<>(3);
		assertEquals(of("a", "b", "c"), strategy.select(population, context.getRandomForThread()));
	}

	@Test
	public void testSelection6() {
		SelectionStrategy<String, String> strategy = new TruncationSelection<>(10);
		assertEquals(of("a", "b", "c", "d", "e", "f", "a", "b", "c", "d"),
			strategy.select(population, context.getRandomForThread()));
	}

}
