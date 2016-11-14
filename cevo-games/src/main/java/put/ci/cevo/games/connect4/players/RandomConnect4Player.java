package put.ci.cevo.games.connect4.players;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.Connect4Action;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.connect4.Connect4State;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.util.RandomUtils;

/**
 * Makes random moves regardless of the board situation.
 */
public class RandomConnect4Player implements Connect4Player, Agent<Connect4State, Connect4Action> {

	@Override
	@Deprecated
	public int getMove(Connect4Board board, int player, int[] validMoves, RandomDataGenerator random) {
		return RandomUtils.pickRandom(validMoves, random);
	}

	@Override
	public Decision<Connect4Action> chooseAction(Connect4State state, List<Connect4Action> availableActions,
			RandomDataGenerator random) {
		return Decision.of(RandomUtils.pickRandom(availableActions, random));
	}
}
