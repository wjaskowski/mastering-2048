package put.ci.cevo.games.board;

import put.ci.cevo.rl.environment.State;

/**
 * An environment state involving a board
 */
public interface BoardState extends State {
	Board getBoard();
}
