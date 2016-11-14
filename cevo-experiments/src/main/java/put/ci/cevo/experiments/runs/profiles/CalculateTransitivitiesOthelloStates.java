package put.ci.cevo.experiments.runs.profiles;

import put.ci.cevo.experiments.profiles.generators.RandomStrategyGenerator;
import put.ci.cevo.experiments.profiles.generators.StrategyGenerator;
import put.ci.cevo.experiments.rl.OthelloWPCStateInteraction;
import put.ci.cevo.experiments.runs.profiles.generic.PerfProfileTransitionsHazelcastCalculator;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class CalculateTransitivitiesOthelloStates implements Experiment {

	private final ThreadedContext random = new ThreadedContext();

	private final InteractionDomain<WPC, OthelloState> interaction;
	private final StrategyGenerator<WPC> strategyGenerator;

	private final PerfProfileTransitionsHazelcastCalculator<WPC, OthelloState> calculator;
	private final WPCIndividualFactory wpcFactory;

	public CalculateTransitivitiesOthelloStates() {

		interaction = new OthelloWPCStateInteraction();
		wpcFactory = new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0);
		strategyGenerator = new RandomStrategyGenerator<>(wpcFactory);

		final int numTestPairs = 10;
		final int numStrategiesPerPair = 100;

		calculator = new PerfProfileTransitionsHazelcastCalculator<WPC, OthelloState>(
			strategyGenerator, interaction, numTestPairs, numStrategiesPerPair,
			"/home/mszubert/res/othello-states-1000-per-bucket.dump", random);
	}

	@Override
	public void run(String[] args) {
		calculator.run();
	}
}
