package put.ci.cevo.experiments.clustering;

import org.junit.BeforeClass;
import org.junit.Test;
import put.ci.cevo.experiments.clustering.aggregates.KMeansFitnessAggregate;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedRandom;

import java.util.Map;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static put.ci.cevo.framework.algorithms.common.PayoffTable.create;
import static put.ci.cevo.util.stats.EventsLoggerFactory.createDefault;

public class KMeansFitnessAggregateTest {

	private static final ThreadedContext context = ThreadedContext.withEventsLogger(new ThreadedRandom(1), 8, createDefault());
	private static PayoffTable<String, String> payoffTable;

	@BeforeClass
	public static void setUp() {
		PayoffTableBuilder<String, String> payoffs = create(of("A", "B", "C", "D"), of("X", "Y", "Z", "V"));
		payoffs.put("A", "X", 1.0);
		payoffs.put("A", "Y", 1.0);
		payoffs.put("A", "Z", 0.0);
		payoffs.put("A", "V", 0.0);
		payoffs.put("B", "X", 1.0);
		payoffs.put("B", "Y", 1.0);
		payoffs.put("B", "Z", 0.0);
		payoffs.put("B", "V", 0.0);
		payoffs.put("C", "X", 1.0);
		payoffs.put("C", "Y", 1.0);
		payoffs.put("C", "Z", 0.5);
		payoffs.put("C", "V", 0.0);
		payoffs.put("D", "X", 1.0);
		payoffs.put("D", "Y", 1.0);
		payoffs.put("D", "Z", 0.0);
		payoffs.put("D", "V", 0.0);
		payoffTable = payoffs.build();
	}

	@Test
	public void testKMeansAggregate() {
		KMeansFitnessAggregate aggregate = new KMeansFitnessAggregate(2);
		Map<String, Fitness> aggregateFitness = aggregate.aggregateFitness(payoffTable, context);

		assertEquals(new MultiobjectiveFitness(new double[] { 0.0, 1.0 }), aggregateFitness.get("A"));
		assertEquals(new MultiobjectiveFitness(new double[] { 0.25, 1.0 }), aggregateFitness.get("C"));
	}

	@Test
	public void testDimensionsOrder() {
		KMeansFitnessAggregate aggregate = new KMeansFitnessAggregate(1);
		Map<String, Fitness> aggregateFitness = aggregate.aggregateFitness(payoffTable, context);

		assertEquals(new MultiobjectiveFitness(new double[] { 0.5 }), aggregateFitness.get("A"));
		assertEquals(new MultiobjectiveFitness(new double[] { 0.625 }), aggregateFitness.get("C"));
	}
}
