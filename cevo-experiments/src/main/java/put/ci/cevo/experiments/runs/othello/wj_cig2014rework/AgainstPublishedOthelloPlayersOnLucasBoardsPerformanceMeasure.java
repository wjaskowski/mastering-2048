package put.ci.cevo.experiments.runs.othello.wj_cig2014rework;

import put.ci.cevo.experiments.othello.AgainstOthelloTeamPerformanceMeasure;
import put.ci.cevo.experiments.othello.OthelloPlayerStateOpponentInteractionDomain;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.LucasInitialOthelloStates;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.published.PublishedPlayers;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

/**
 * Measures OthelloPlayers against a bunch of known, published players
 */
public class AgainstPublishedOthelloPlayersOnLucasBoardsPerformanceMeasure
		implements PerformanceMeasure<OthelloPlayer> {

	private final List<OthelloPlayer> OPPONENTS = PublishedPlayers.eleven();

	private final PerformanceMeasure<OthelloPlayer> measure;

	public AgainstPublishedOthelloPlayersOnLucasBoardsPerformanceMeasure() {
		this(new DoubleOthello());
	}

	public AgainstPublishedOthelloPlayersOnLucasBoardsPerformanceMeasure(DoubleOthello othello) {
		measure = new AgainstOthelloTeamPerformanceMeasure(
				new OthelloPlayerStateOpponentInteractionDomain(othello),
				OPPONENTS,
				new LucasInitialOthelloStates(false).boards()
				// false despite some boards repeat, because I would like to use exactly the same measure as Runarsson & Lucas
		);
	}

	@Override
	public Measurement measure(RandomFactory<OthelloPlayer> subjectFactory, ThreadedContext context) {
		return measure.measure(subjectFactory, context);
	}
}
