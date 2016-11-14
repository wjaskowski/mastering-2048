package put.ci.cevo.games.player.minmax;

import put.ci.cevo.games.GameState;

public interface GameStateEvaluator {

	public double evaluate(GameState state);

}