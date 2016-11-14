package put.ci.cevo.rl.environment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;

public class StateTrajectory<S extends State> {

	final List<S> states;

	public static <S extends State, A extends Action> AgentTransition<S, A> getAgentTransition(Environment<S, A> env,
			S state, Agent<S, A> agent, RandomDataGenerator random) {
		A action = agent.chooseAction(state, env.getPossibleActions(state), random).getAction();
		return env.getAgentTransition(state, action);
	}

	public static <S extends State, A extends Action> AgentTransition<S, A> getRandomTransition(Environment<S, A> env,
			S state, RandomDataGenerator random) {
		List<A> actions = env.getPossibleActions(state);
		A action = chooseRandomMove(actions, random);
		return env.getAgentTransition(state, action);
	}

	private static <A extends Action> A chooseRandomMove(List<A> possibleActions, RandomDataGenerator random) {
		if (possibleActions.isEmpty()) {
			return null;
		} else if (possibleActions.size() == 1) {
			return possibleActions.get(0);
		} else {
			return possibleActions.get(random.nextInt(0, possibleActions.size() - 1));
		}
	}

	public int getDepth() {
		return states.size();
	}

	public StateTrajectory(List<S> states) {
		this.states = new ArrayList<>(states);
	}

	public List<S> getStates() {
		return new ArrayList<>(states);
	}

	public S getLastState() {
		return states.get(states.size() - 1);
	}

	public StateTrajectory<S> shorten(int depth) {
		return new StateTrajectory<>(states.subList(0, states.size() - depth));
	}

	public <A extends Action> StateTrajectory<S> lengthen(int depth, Environment<S, A> env, RandomDataGenerator random) {
		S state = getLastState();
		List<S> newStates = new ArrayList<>(states);
		for (int d = 0; d < depth; d++) {
			AgentTransition<S, A> agentTransition = getRandomTransition(env, state, random);
			state = agentTransition.getAfterState();

			if (env.isTerminal(state)) {
				break;
			} else {
				newStates.add(state);
			}
		}

		return new StateTrajectory<>(newStates);
	}
}
