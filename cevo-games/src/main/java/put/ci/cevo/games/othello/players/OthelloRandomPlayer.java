package put.ci.cevo.games.othello.players;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.util.RandomUtils;

/**
 * Selects a random valid move.
 */
public class OthelloRandomPlayer implements OthelloPlayer {

	public OthelloRandomPlayer() {
	}

	@Override
	public int getMove(OthelloBoard board, int player, int[] validMoves, RandomDataGenerator random) {
		return RandomUtils.pickRandom(validMoves, random);
	}
}
