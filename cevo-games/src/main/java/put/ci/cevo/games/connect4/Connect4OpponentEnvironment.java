package put.ci.cevo.games.connect4;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.EnvTransition;

public class Connect4OpponentEnvironment implements Environment<Connect4State, Connect4Action> {

	private final Agent<Connect4State, Connect4Action> opponent;
	private final int opponentColor;
	private final boolean randomOpponentColor;

	/**
	 * The color of the opponent (and agent) pieces are selected at random upon {@link #sampleInitialStateDistribution}
	 */
	public Connect4OpponentEnvironment(Agent<Connect4State, Connect4Action> opponent) {
		this.opponent = opponent;
		this.randomOpponentColor = true;
		this.opponentColor = Integer.MIN_VALUE; // TODO: inelegant

	}

	public Connect4OpponentEnvironment(Agent<Connect4State, Connect4Action> opponent, int opponentColor) {
		Preconditions.checkArgument(opponentColor == Board.WHITE || opponentColor == Board.BLACK);
		this.opponent = opponent;
		this.randomOpponentColor = false;
		this.opponentColor = opponentColor;
	}

	private double getReward(Connect4State state) {
		if (!state.isTerminal())
			return 0;

		//TODO: Consider 0, 0.5, 1.0 (then watchout also for getEnvironmentTransition
		Connect4Board board = state.getBoard();
		if (board.getWinner() != -1) {
			// This is correct. Who made the winning move gets 1.0
			return 1;
		} else {
			// a draw
			return 0;
		}
	}

	@Override
	public AgentTransition<Connect4State, Connect4Action> getAgentTransition(Connect4State state,
			Connect4Action action) {
		Preconditions.checkArgument(randomOpponentColor || state.getPlayerToMove() != opponentColor,
				"Must be a state where its mine turn (not the opponents turn)");
		Preconditions.checkArgument(!state.isTerminal());

		Connect4State afterState = state.makeMove(action.getCol());
		return new AgentTransition<>(state, action, getReward(afterState), afterState);
		// If the afterstate is a terminal state (winning or end of the game), the agent will be immediately rewarded
	}

	@Override
	public EnvTransition<Connect4State> getEnvironmentTransition(Connect4State afterState,
			RandomDataGenerator random) {
		Preconditions.checkArgument(randomOpponentColor || afterState.getPlayerToMove() == opponentColor,
				"Must be a state where its opponents turn (opponent is a part of the environment)");

		// The last agent move finished the game, but by convention we have to handle the environment answer.
		// Thus we assume nextState = afterState and generate a reward.
		if (afterState.isTerminal()) {
			// Since the game is already finished, the reward has been already given to the player (thus no reward here)
			// Notice that this reward is correct V(afterState) = 0 + v(afterState)
			return new EnvTransition<>(afterState, 0, afterState);
		}

		Connect4Action action = opponent.chooseAction(afterState, getPossibleActions(afterState), random).getAction();
		Connect4State nextState = afterState.makeMove(action.getCol());
		// Here the game may finish by the opponent winning it, thus we check for a reward
		return new EnvTransition<>(afterState, -getReward(nextState), nextState);
	}

	@Override
	public List<Connect4Action> getPossibleActions(Connect4State state) {
		return stream(state.getBoard().getValidMoves().toArray()).mapToObj(Connect4Action::new).collect(toList());
	}

	@Override
	public Connect4State sampleInitialStateDistribution(RandomDataGenerator random) {
		Connect4State initialState = new Connect4State(new Connect4Board(), Board.BLACK);
		if (opponentColor == Board.BLACK || (randomOpponentColor && random.nextInt(0, 1) == 0)) {
			// If opponent is the first to move, then the game starts from the environment (opponent) transition
			initialState = getEnvironmentTransition(initialState, random).getNextState();
		}
		return initialState;
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
