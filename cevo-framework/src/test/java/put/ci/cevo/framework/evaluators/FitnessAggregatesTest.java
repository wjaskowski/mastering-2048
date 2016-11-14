package put.ci.cevo.framework.evaluators;

import org.junit.BeforeClass;
import org.junit.Test;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.framework.fitness.*;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FitnessAggregatesTest {

	private static PayoffTable<String, String> payoffTable;

	@BeforeClass
	public static void setUp() {
		PayoffTableBuilder<String, String> payoffs = PayoffTable.create(4, 4);
		payoffs.put("A", "X", 1.0);
		payoffs.put("A", "Y", 0.5);
		payoffs.put("A", "Z", 1.0);
		payoffs.put("A", "V", 0.5);
		payoffs.put("B", "X", 0.0);
		payoffs.put("B", "Y", 1.0);
		payoffs.put("B", "Z", 0.5);
		payoffs.put("B", "V", 1.0);
		payoffs.put("C", "X", 1.0);
		payoffs.put("C", "Y", 1.0);
		payoffs.put("C", "Z", 1.0);
		payoffs.put("C", "V", 1.0);
		payoffs.put("D", "X", 0.0);
		payoffs.put("D", "Y", 0.0);
		payoffs.put("D", "Z", 0.5);
		payoffs.put("D", "V", 0.5);
		payoffTable = payoffs.build();
	}

	@Test
	public void testDistinctions() {
		FitnessAggregate dfs = new Distinctions();
		Map<String, Fitness> fitness = dfs.aggregateFitness(payoffTable, null);

		double numDistinctionsToMake = 6;
		assertEquals(4 / numDistinctionsToMake, fitness.get("A").fitness(), 0.0001);
		assertEquals(5 / numDistinctionsToMake, fitness.get("B").fitness(), 0.0001);
		assertEquals(0 / numDistinctionsToMake, fitness.get("C").fitness(), 0.0001);
		assertEquals(4 / numDistinctionsToMake, fitness.get("D").fitness(), 0.0001);

	}

	@Test
	public void testDistinctionsFitnessSharing() {
		FitnessAggregate dfs = new DistinctionsFitnessSharing();
		Map<String, Fitness> fitness = dfs.aggregateFitness(payoffTable, null);

		assertEquals(1.6667, fitness.get("A").fitness(), 0.0001);
		assertEquals(2.1667, fitness.get("B").fitness(), 0.0001);
		assertEquals(0, fitness.get("C").fitness(), 0.0001);
		assertEquals(2.1667, fitness.get("D").fitness(), 0.0001);

	}

	@Test
	public void testCompetitiveFitnessSharing() {
		FitnessAggregate fs = new CompetitiveFitnessSharing();
		Map<String, Fitness> fitness = fs.aggregateFitness(payoffTable, null);

		assertEquals(1.2, fitness.get("A").fitness(), 0.0001);
		assertEquals(0.9, fitness.get("B").fitness(), 0.0001);
		assertEquals(1.5667, fitness.get("C").fitness(), 0.0001);
		assertEquals(0.3333, fitness.get("D").fitness(), 0.0001);
	}

	@Test
	public void testSimpleSumFitness() {
		FitnessAggregate sumFitness = new SimpleSumFitness();
		Map<String, Fitness> fitness = sumFitness.aggregateFitness(payoffTable, null);

		assertEquals(3, fitness.get("A").fitness(), 0.0001);
		assertEquals(2.5, fitness.get("B").fitness(), 0.0001);
		assertEquals(4, fitness.get("C").fitness(), 0.0001);
		assertEquals(1, fitness.get("D").fitness(), 0.0001);
	}

	@Test
	public void testNegativeFitness() {
		FitnessAggregate sumFitness = new NegativeFitnessAggregate(new SimpleSumFitness());

		Map<String, Fitness> fitness = sumFitness.aggregateFitness(payoffTable, null);

		assertEquals(-3, fitness.get("A").fitness(), 0.0001);
		assertEquals(-2.5, fitness.get("B").fitness(), 0.0001);
		assertEquals(-4, fitness.get("C").fitness(), 0.0001);
		assertEquals(-1, fitness.get("D").fitness(), 0.0001);
	}
}
