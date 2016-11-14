package put.ci.cevo.games.othello.players.published;

import java.io.IOException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.io.IOUtils;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * Burrow, Peter: Hybridising Evolution and Temporal Difference Learning. Phd thesis, University of Essex, UK (2011)
 * Submitted to the Othello League under the name "prb_nt15_001"
 */
public class Burrow2011ETDLPlayer implements Factory<OthelloPlayer> {
	@Override
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.OUTPUT_NEGATION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(Burrow2011ETDLPlayer.class.getResourceAsStream("Burrow2011ETDL.player"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

