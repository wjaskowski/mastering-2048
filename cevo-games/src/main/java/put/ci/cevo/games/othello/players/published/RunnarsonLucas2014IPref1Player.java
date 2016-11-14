package put.ci.cevo.games.othello.players.published;

import java.io.IOException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.io.IOUtils;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * iPref-1-Tuple player from Runarsson & Lucas: Preference Learning for Move Prediction and Evaluation Function
 * Approximation in Othello, 2014
 */
public class RunnarsonLucas2014IPref1Player implements Factory<OthelloPlayer> {
	@Override
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.BOARD_INVERSION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(RunnarsonLucas2014IPref1Player.class.getResourceAsStream(
					"RunnarsonLucas2014IPref1.player"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
