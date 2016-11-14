package put.ci.cevo.games.othello.players.published;

import java.io.IOException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.io.IOUtils;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 *  Jaśkowski W. & Szubert M. "Coevolutionary CMA-ES for Knowledge-Free Learning of Game Position Evaluation",
 *  IEEE Transactions on Computational Intelligence and AI in Games, 2015
 */
public class JaskowskiSzubert2015CoCMAES4Player implements Factory<OthelloPlayer> {
	@Override
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.BOARD_INVERSION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(JaskowskiSzubert2015CoCMAES4Player.class.getResourceAsStream("JaskowskiSzubert2015CoCMAES-4.player"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
