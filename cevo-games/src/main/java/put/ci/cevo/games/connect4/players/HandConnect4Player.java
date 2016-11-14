package put.ci.cevo.games.connect4.players;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.Connect4;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.util.RandomUtils;

/**
 * Makes random moves just like {@link RandomConnect4Player} but if a winning move is available, it will be chosen.
 */
public class HandConnect4Player implements Connect4Player {

	@Override
	public int getMove(Connect4Board board, int player, int[] validMoves, RandomDataGenerator random) {
		for (int move : validMoves) {
			if (Connect4.isWinningMove(move, player, board)) {
				return move;
			}
		}
		return RandomUtils.pickRandom(validMoves, random);
	}
}
