package put.ci.cevo.games.connect4;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.EnvTransition;

/**
 * This is pseudo MDP Environment
 */
public class Connect4SelfPlayEnvironment implements Environment<Connect4State, Connect4Action> {

	/**
	 * Reward from the perspective of the first (BLACK) player
	 */
	private double getReward(Connect4State state) {
		if (!state.isTerminal())
			return 0;

		Connect4Board board = state.getBoard();
		if (board.getWinner() != -1) {
			if (board.getWinner() == Board.BLACK)
				//TODO: Consider 0, 0.5, 1
				return 1;
			else
				return -1;
		} else {
			// a draw
			return 0;
		}
	}

	@Override
	public AgentTransition<Connect4State, Connect4Action> getAgentTransition(Connect4State state,
			Connect4Action action) {
		Connect4State afterState = state.makeMove(action.getCol());
		return new AgentTransition<>(state, action, getReward(afterState), afterState);
	}

	@Override
	public EnvTransition<Connect4State> getEnvironmentTransition(Connect4State afterState,
			RandomDataGenerator random) {
		return new EnvTransition<>(afterState, 0, afterState);
	}

	@Override
	public List<Connect4Action> getPossibleActions(Connect4State state) {
		return stream(state.getBoard().getValidMoves().toArray()).mapToObj(Connect4Action::new).collect(toList());
	}

	@Override
	public Connect4State sampleInitialStateDistribution(RandomDataGenerator random) {
		return new Connect4State(new Connect4Board(), Board.BLACK);
	}

	@Override
	public boolean isTerminal(Connect4State state) {
		return state.isTerminal();
	}

	@Override
	public double getAgentPerformance(double totalReward, int numSteps, Connect4State finalState) {
		throw new NotImplementedException();
	}
}
