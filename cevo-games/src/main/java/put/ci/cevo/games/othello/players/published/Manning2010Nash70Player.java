package put.ci.cevo.games.othello.players.published;

import java.io.IOException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.io.IOUtils;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * A player submitted by E. Manning to Othello League (not sure exactly how prepared). Maybe:
 *
 * Manning, E.: Using Resource-Limited Nash Memory to Improve an Othello Evaluation Function. Computational Intelligence
 * and AI in Games, IEEE Transactions on 2(1) (2010) 40-53
 */
public class Manning2010Nash70Player implements Factory<OthelloPlayer> {
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.OUTPUT_NEGATION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(Manning2010Nash70Player.class.getResourceAsStream("Manning2010Nash70.player"));
		} catch (IOException e) {
			new RuntimeException(e);
		}
	}
}
