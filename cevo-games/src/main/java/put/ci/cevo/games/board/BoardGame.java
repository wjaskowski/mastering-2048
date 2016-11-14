package put.ci.cevo.games.board;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.rl.environment.State;

public interface BoardGame<S, T, U extends State> extends Game<S, T> {

	/** Plays the game. starting from <code>initialState</code> */
	public GameOutcome play(S blackPlayer, T whitePlayer, U initialState, RandomDataGenerator random);
}
