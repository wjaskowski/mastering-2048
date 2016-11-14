package put.ci.cevo.experiments.runs.profiles.ipd;

import static com.google.common.base.Joiner.on;

import java.io.File;

import org.joda.time.Duration;
import put.ci.cevo.experiments.ipd.*;
import put.ci.cevo.experiments.profiles.generators.RandomStrategyGenerator;
import put.ci.cevo.experiments.runs.profiles.generic.PerfProfileDatabaseGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.games.ipd.LinearPayoffInterpolator;
import put.ci.cevo.newexperiments.Experiment;

public class IPDPerfProfileDatabaseRandomGenerator implements Experiment {

	private static final int DEFAULT_MAX_BUCKET_SIZE = 1000;
	private static final int DEFAULT_NUM_OPPONENTS = 1000;
	private static final int DEFAULT_CHOICES = 9;

	private final ThreadedContext random = new ThreadedContext();

	private final InteractionDomain<IPDVector, IPDVector> ipd;
	private final PerformanceMeasure<IPDVector> expectedUtility;

	private final PerfProfileDatabaseGenerator<IPDVector> perfProfileDatabaseGenerator;

	public IPDPerfProfileDatabaseRandomGenerator() {
		this(DEFAULT_CHOICES, DEFAULT_NUM_OPPONENTS, DEFAULT_MAX_BUCKET_SIZE);
	}

	public IPDPerfProfileDatabaseRandomGenerator(int choices, int numOpponents, int maxBucketSize) {
		this.ipd = new IPDInteraction(new LinearPayoffInterpolator(choices), new IPDLookupTableMapper(), 150);
		this.expectedUtility = new ExpectedUtility<>(
			ipd, new IPDPopulationFactory(choices), numOpponents, new ThreadedContext(123));

		final Duration backupInterval = Duration.standardHours(2);
		final Duration checkpointInterval = Duration.standardSeconds(60);
		final int numBuckets = 100;

		File output = new File("ipd-choices-db-" + on("-").join(choices, numBuckets, maxBucketSize).toString()
			+ ".dump");
		perfProfileDatabaseGenerator = new PerfProfileDatabaseGenerator<>(
			expectedUtility, new RandomStrategyGenerator<>(new IPDVectorIndividualFactory(choices)), numBuckets,
			maxBucketSize, output, backupInterval, checkpointInterval, random);
	}

	@Override
	public void run(String[] args) {
		perfProfileDatabaseGenerator.run();
	}

	public static void main(String[] args) {
		IPDPerfProfileDatabaseRandomGenerator exp = new IPDPerfProfileDatabaseRandomGenerator();
		exp.run(null);
	}
}
