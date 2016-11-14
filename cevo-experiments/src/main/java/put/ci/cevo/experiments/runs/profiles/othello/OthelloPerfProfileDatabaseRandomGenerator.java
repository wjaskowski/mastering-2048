package put.ci.cevo.experiments.runs.profiles.othello;

import org.joda.time.Duration;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.profiles.generators.RandomStrategyGenerator;
import put.ci.cevo.experiments.profiles.generators.StrategyGenerator;
import put.ci.cevo.experiments.runs.profiles.generic.PerfProfileDatabaseGenerator;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

import java.io.File;

/**
 * "Generates Performance Profiles with RSEL...";
 * 
 */
public class OthelloPerfProfileDatabaseRandomGenerator implements Experiment {

	private final ThreadedContext context = new ThreadedContext();

	private final InteractionDomain<WPC, WPC> interaction;
	private final PerformanceMeasure<WPC> performanceMeasure;

	private final StrategyGenerator<WPC> strategyGenerator;

	private final PerfProfileDatabaseGenerator<WPC> perfProfileDatabaseGenerator;
	private final WPCIndividualFactory wpcFactory;

	public OthelloPerfProfileDatabaseRandomGenerator() {

		wpcFactory = new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0);

		interaction = new OthelloWPCInteraction(true);

		Integer numOpponents = 1000;
		performanceMeasure = new ExpectedUtility<WPC, WPC>(interaction, new UniformRandomPopulationFactory<WPC>(
			wpcFactory), numOpponents, context);

		strategyGenerator = new RandomStrategyGenerator<>(wpcFactory);

		final Duration backupInterval = Duration.standardHours(2);
		final Duration checkpointInterval = Duration.standardSeconds(60);

		// TODO: I wonder whether it would be wise in general to revert the object creation order starting with
		// this one...
		final int maxBucketSize = 1000;
		final int numBuckets = 100;
		perfProfileDatabaseGenerator = new PerfProfileDatabaseGenerator<WPC>(
			performanceMeasure, strategyGenerator, numBuckets, maxBucketSize, new File(
				"othello-pprofile-db-random-new.dump"), backupInterval, checkpointInterval, context);
		// @formatter:on
	}

	@Override
	public void run(String[] args) {
		perfProfileDatabaseGenerator.run();
	}
}
