package put.ci.cevo.games.othello.mdp;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.EnvTransition;

public class OthelloSelfPlayEnvironment implements Environment<OthelloState, OthelloMove> {

	private static final OthelloRules OTHELLO = new OthelloRules();

	@Override
	public AgentTransition<OthelloState, OthelloMove> getAgentTransition(OthelloState state, OthelloMove action) {
		OthelloState afterState = OTHELLO.makeMove(state, action);
		return new AgentTransition<>(state, action, 0, afterState);
	}

	@Override
	public EnvTransition<OthelloState> getEnvironmentTransition(OthelloState afterState,
			RandomDataGenerator random) {
		@SuppressWarnings("UnnecessaryLocalVariable") OthelloState nextState = afterState;
		double reward = isTerminal(nextState) ? OTHELLO.getReward(nextState) : 0;
		return new EnvTransition<>(afterState, reward, nextState);
	}

	@Override
	public List<OthelloMove> getPossibleActions(OthelloState state) {
		return OTHELLO.findMoves(state);
	}

	@Override
	public OthelloState sampleInitialStateDistribution(RandomDataGenerator random) {
		return OTHELLO.createInitialState();
	}

	@Override
	public boolean isTerminal(OthelloState state) {
		return OTHELLO.isTerminal(state);
	}

	public AgentTransition<OthelloState, OthelloMove> getEnvironmentChange(OthelloState state) {
		return new AgentTransition<>(state, null, 0.0, state);
	}

	@Override
	public double getAgentPerformance(double totalReward, int numSteps, OthelloState finalState) {
		return OTHELLO.getOutcome(finalState);
	}
}
