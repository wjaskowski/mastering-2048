package put.ci.cevo.experiments.runs.profiles.othello;

import org.joda.time.Duration;
import put.ci.cevo.experiments.profiles.generators.RandomStrategyGenerator;
import put.ci.cevo.experiments.profiles.generators.StrategyGenerator;
import put.ci.cevo.experiments.rl.OthelloStateWPCInteraction;
import put.ci.cevo.experiments.rl.StateIndividualFactory;
import put.ci.cevo.experiments.runs.profiles.generic.PerfProfileDatabaseGenerator;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.OthelloSelfPlayEnvironment;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

import java.io.File;

public class OthelloStatePerfProfilesDatabaseRandomGenerator implements Experiment {

	private final ThreadedContext context = new ThreadedContext();

	private final InteractionDomain<OthelloState, WPC> interaction;
	private final PerformanceMeasure<OthelloState> performanceMeasure;

	private final StrategyGenerator<OthelloState> strategyGenerator;

	private final PerfProfileDatabaseGenerator<OthelloState> perfProfileDatabaseGenerator;
	private final WPCIndividualFactory wpcFactory;

	private final StateIndividualFactory<OthelloState> stateFactory;

	public OthelloStatePerfProfilesDatabaseRandomGenerator() {

		wpcFactory = new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0);
		stateFactory = new StateIndividualFactory<>(new OthelloSelfPlayEnvironment(), 5, 30);

		interaction = new OthelloStateWPCInteraction();

		Integer numOpponents = 1000;
		performanceMeasure = new ExpectedUtility<OthelloState, WPC>(interaction, 
			new UniformRandomPopulationFactory<WPC>(wpcFactory), numOpponents, context);

		strategyGenerator = new RandomStrategyGenerator<>(stateFactory);

		final Duration backupInterval = Duration.standardHours(2);
		final Duration checkpointInterval = Duration.standardSeconds(60);

		// TODO: I wonder whether it would be wise in general to revert the
		// object creation order starting with
		// this one...
		final int maxBucketSize = 100000;
		final int numBuckets = 100;
		final int totalNumStrategies = 100000;
		perfProfileDatabaseGenerator = new PerfProfileDatabaseGenerator<OthelloState>(
			performanceMeasure, strategyGenerator, numBuckets, maxBucketSize, totalNumStrategies, new File(
				"othello-states-30-total-100000.dump"), backupInterval, checkpointInterval, context);
		// @formatter:on
	}

	@Override
	public void run(String[] args) {
		perfProfileDatabaseGenerator.run();
	}
}
