package put.ci.cevo.games.connect4.players;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.player.BoardGamePlayer;

/**
 * @deprecated User {@link put.ci.cevo.rl.agent.Agent < put.ci.cevo.games.connect4.Connect4State ,
 * put.ci.cevo.games.connect4.Connect4Action >} instead
 */
@Deprecated
public interface Connect4Player extends BoardGamePlayer<Connect4Board> {

	/** Valid moves are represented as indexes of columns */
	@Override
	public int getMove(Connect4Board board, int player, int[] validMoves, RandomDataGenerator random);
}
