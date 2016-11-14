package put.ci.cevo.games;

import java.io.Serializable;

import put.ci.cevo.rl.environment.State;

public interface GameState extends State, Serializable {
	int getPlayerToMove();
}
