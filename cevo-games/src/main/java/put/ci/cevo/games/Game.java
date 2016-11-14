package put.ci.cevo.games;

import org.apache.commons.math3.random.RandomDataGenerator;

public interface Game<S, T> {

	/** Plays the game */
	public GameOutcome play(S blackPlayer, T whitePlayer, RandomDataGenerator random);
}
