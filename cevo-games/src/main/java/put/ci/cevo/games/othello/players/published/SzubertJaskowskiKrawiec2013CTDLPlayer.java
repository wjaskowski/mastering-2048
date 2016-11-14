package put.ci.cevo.games.othello.players.published;

import java.io.IOException;

import org.apache.commons.collections15.Factory;
import org.apache.commons.io.IOUtils;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.OthelloLeague;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * On Scalability, Generalization, and Hybridization of Coevolutionary Learning: a Case Study for Othello (Marcin
 * Szubert, Wojciech Jaśkowski, Krzysztof Krawiec), In IEEE Transactions on Computational Intelligence and AI in Games,
 * volume 5, 2013.
 */
public class SzubertJaskowskiKrawiec2013CTDLPlayer implements Factory<OthelloPlayer> {
	@Override
	public OthelloNTuplesPlayer create() {
		return new OthelloNTuplesPlayer(OthelloLeague.fromOthelloLeagueFormat(PLAYER),
				BoardEvaluationType.OUTPUT_NEGATION);
	}

	static String PLAYER;

	static {
		try {
			PLAYER = IOUtils.toString(SzubertJaskowskiKrawiec2013CTDLPlayer.class.getResourceAsStream(
					"SzubertJaskowskiKrawiec2013CTDL.player"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
