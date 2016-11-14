package put.ci.cevo.games;

import java.util.List;

import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;

/**
 * Interface describing game rules.
 * 
 * @author Marcin Szubert
 * 
 */
public interface GameRules<S extends State, A extends Action> {

	/**
	 * Verifies if game is in terminal state.
	 * 
	 * @return <code>true</code> if game is ended
	 */
	public boolean isTerminal(S state);

	public List<A> findMoves(S state);

	public S makeMove(S state, A action);

	public S createInitialState();

	public boolean isMaxPlayer(int currentPlayer);

	public int getOutcome(S state);

	public double getReward(S state);
}
