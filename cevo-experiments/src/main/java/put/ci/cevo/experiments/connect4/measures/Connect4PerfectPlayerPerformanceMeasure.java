package put.ci.cevo.experiments.connect4.measures;

import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.connect4.players.Connect4PerfectPlayer;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;

import static put.ci.cevo.games.connect4.players.Connect4PerfectPlayer.Randomization.RANDOMIZE_LOSSES_AND_MOVES;

public class Connect4PerfectPlayerPerformanceMeasure implements PerformanceMeasure<Connect4Player> {

	private final AgainstTeamPerformanceMeasure<Connect4Player, Connect4Player> measure;

	public Connect4PerfectPlayerPerformanceMeasure(InteractionDomain<Connect4Player, Connect4Player> c4, int numRepeats) {
		this.measure = new AgainstTeamPerformanceMeasure<>(c4,
				new Connect4PerfectPlayer(RANDOMIZE_LOSSES_AND_MOVES), numRepeats);
	}

	/**
	 * @param numRepeats    number of interactions to make against the random opponent
	 * @param randomization controls degree of randomization in perfect player's behavior
	 */
	public Connect4PerfectPlayerPerformanceMeasure(InteractionDomain<Connect4Player, Connect4Player> c4,
			Connect4PerfectPlayer.Randomization randomization, int numRepeats) {
		this.measure = new AgainstTeamPerformanceMeasure<>(c4, new Connect4PerfectPlayer(randomization), numRepeats);
	}

	@Override
	public Measurement measure(RandomFactory<Connect4Player> subjectFactory, ThreadedContext context) {
		return measure.measure(subjectFactory, context);
	}
}
