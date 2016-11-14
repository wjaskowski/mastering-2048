package put.ci.cevo.rl.environment;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

public class EnvironmentDecorator<S extends State, A extends Action> implements Environment<S, A> {

	protected Environment<S, A> env;

	@Override
	public AgentTransition<S, A> getAgentTransition(S state, A action) {
		return env.getAgentTransition(state, action);
	}

	@Override
	public EnvTransition<S> getEnvironmentTransition(S afterState, RandomDataGenerator random) {
		return env.getEnvironmentTransition(afterState, random);
	}

	@Override
	public List<A> getPossibleActions(S state) {
		return env.getPossibleActions(state);
	}

	@Override
	public S sampleInitialStateDistribution(RandomDataGenerator random) {
		return env.sampleInitialStateDistribution(random);
	}

	@Override
	public boolean isTerminal(S state) {
		return env.isTerminal(state);
	}

	@Override
	public double getAgentPerformance(double totalReward, int numSteps, S finalState) {
		return env.getAgentPerformance(totalReward, numSteps, finalState);
	}
}
