package put.ci.cevo.games.othello.players.published;

import java.io.IOException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.io.IOUtils;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * Manning, E.: Coevolution in a large search space using resource-limited nash memory, GECCO 2010
 * Notice: it uses board inversion. This is the most recent Manning's player
 */
public class Manning2010GECCOPlayer implements Factory<OthelloPlayer> {

	@Override
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.BOARD_INVERSION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(Manning2010GECCOPlayer.class.getResourceAsStream("Manning2010GECCO.player"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
