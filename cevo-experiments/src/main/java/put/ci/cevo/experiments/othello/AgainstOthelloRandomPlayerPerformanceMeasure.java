package put.ci.cevo.experiments.othello;

import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloRandomPlayer;

/**
 * Measures performance of OthelloPlayer based on a number of games against the random move player.
 */
public class AgainstOthelloRandomPlayerPerformanceMeasure implements PerformanceMeasure<OthelloPlayer> {

	private final AgainstTeamPerformanceMeasure<OthelloPlayer, OthelloPlayer> measure;

	/**
	 * @param numRepeats
	 *            number of interactions to make against the random opponent
	 */
	public AgainstOthelloRandomPlayerPerformanceMeasure(InteractionDomain<OthelloPlayer, OthelloPlayer> othello,
			int numRepeats) {
		this.measure = new AgainstTeamPerformanceMeasure<>(othello, new OthelloRandomPlayer(), numRepeats);
	}

	public AgainstOthelloRandomPlayerPerformanceMeasure(int numRepeats) {
		this(new OthelloInteractionDomain(new DoubleOthello()), numRepeats);
	}

	@Override
	public Measurement measure(RandomFactory<OthelloPlayer> subjectFactory, ThreadedContext context) {
		return measure.measure(subjectFactory, context);
	}
}
