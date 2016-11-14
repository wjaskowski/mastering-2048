package put.ci.cevo.experiments.othello;

import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * Implements the performance measure consistent with OthelloLeague. Both players make random moves with prob 0.1
 */
public class OthelloLeaguePerformanceMeasure implements PerformanceMeasure<OthelloPlayer> {

	public static final double RANDOM_MOVE_PROBABILITY = 0.1;

	public final AgainstTeamPerformanceMeasure<OthelloPlayer, OthelloPlayer> measure;

	public OthelloLeaguePerformanceMeasure(int sampleSize) {
		measure = new AgainstTeamPerformanceMeasure<>(
				new OthelloInteractionDomain(new DoubleOthello(RANDOM_MOVE_PROBABILITY)),
				new OthelloStandardWPCHeuristicPlayer().create(),
				sampleSize);
	}

	@Override
	public Measurement measure(RandomFactory<OthelloPlayer> subjectFactory, ThreadedContext context) {
		return measure.measure(subjectFactory, context);
	}
}
