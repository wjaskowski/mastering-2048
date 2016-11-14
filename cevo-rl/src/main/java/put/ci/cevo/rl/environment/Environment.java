package put.ci.cevo.rl.environment;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;

public interface Environment<S extends State, A extends Action> {

	AgentTransition<S, A> getAgentTransition(S state, A action);

	EnvTransition<S> getEnvironmentTransition(S afterState, RandomDataGenerator random);

	List<A> getPossibleActions(S state);

	S sampleInitialStateDistribution(RandomDataGenerator random);

	boolean isTerminal(S state);

	@Deprecated
	default double getAgentPerformance(double totalReward, int numSteps, S finalState) {
		throw new NotImplementedException("Deprecated");
	}


	/**
	 * Gets full transition (agent + environment)
	 */
	default Transition<S, A> getTransition(S state, A action, RandomDataGenerator random) {
		AgentTransition<S, A> agentTransition = getAgentTransition(state, action);
		EnvTransition<S> envTransition = getEnvironmentTransition(agentTransition.getAfterState(), random);

		return new Transition<>(agentTransition, envTransition);
	}

	/**
	 * Executes the episode till the terminalState
	 * @param listener executed after every transition
	 * @return total reward
	 */
	default double runEpisode(Agent<S, A> agent, RandomDataGenerator random, Consumer<Transition<S, A>> listener) {
		double totalReward = 0;
		S currentState = sampleInitialStateDistribution(random);

		while (!isTerminal(currentState)) {
			A action = agent.chooseAction(currentState, getPossibleActions(currentState), random).getAction();
			Transition<S, A> transition = getTransition(currentState, action, random);
			currentState = transition.getNextState();
			totalReward += transition.getReward();
			listener.accept(transition);
		}

		return totalReward;
	}

	default double runEpisode(Agent<S, A> agent, RandomDataGenerator random) {
		return runEpisode(agent, random, x -> {});
	}
}
