package put.ci.cevo.experiments.connect4.measures;

import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.players.RandomConnect4Player;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;

public class Connect4RandomPlayerPerformanceMeasure implements PerformanceMeasure<Connect4Player> {

	private final AgainstTeamPerformanceMeasure<Connect4Player, Connect4Player> measure;

	/**
	 * @param numRepeats number of interactions to make against the random opponent
	 */
	public Connect4RandomPlayerPerformanceMeasure(InteractionDomain<Connect4Player, Connect4Player> c4,
			int numRepeats) {
		this.measure = new AgainstTeamPerformanceMeasure<>(c4, new RandomConnect4Player(), numRepeats);
	}

	@Override
	public Measurement measure(RandomFactory<Connect4Player> subjectFactory, ThreadedContext context) {
		return measure.measure(subjectFactory, context);
	}
}
