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
 * Submitted to the othello league under the name wj-1-2-3-tuples
 */
public class Jaskowski2014All2Player implements Factory<OthelloPlayer> {
	@Override
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.OUTPUT_NEGATION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(Jaskowski2014All2Player.class.getResourceAsStream("Jaskowski2014All123.player"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

