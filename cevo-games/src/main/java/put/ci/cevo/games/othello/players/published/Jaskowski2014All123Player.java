package put.ci.cevo.games.othello.players.published;

import java.io.IOException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.io.IOUtils;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * Jaśkowski, W. (2014). Systematic N-tuple Networks for Position Evaluation: Exceeding 90% in the Othello League. arXiv
 * preprint arXiv:1406.1509.
 */
public class Jaskowski2014All123Player implements Factory<OthelloPlayer> {
	@Override
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.BOARD_INVERSION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(Jaskowski2014All123Player.class.getResourceAsStream("Jaskowski2014All2.player"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

