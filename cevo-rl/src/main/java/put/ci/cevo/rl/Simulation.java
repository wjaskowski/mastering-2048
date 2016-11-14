package put.ci.cevo.rl;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;

public class Simulation<S extends State, A extends Action> {

	private final Agent<S, A> agent;
	private final Environment<S, A> env;

	private int numSteps;
	private double totalReward;

	public Simulation(Agent<S, A> agent, Environment<S, A> env) {
		this.agent = agent;
		this.env = env;
		reset();
	}

	public int getNumSteps() {
		return numSteps;
	}

	public double getTotalReward() {
		return totalReward;
	}

	public void reset() {
		numSteps = 0;
		totalReward = 0;
	}

	public S run(RandomDataGenerator random) {
		return run(Integer.MAX_VALUE, random);
	}

	public S run(int maxNumSteps, RandomDataGenerator random) {
		S currentState = env.sampleInitialStateDistribution(random);

		while (!env.isTerminal(currentState) && (maxNumSteps == Integer.MAX_VALUE || numSteps < maxNumSteps)) {
			List<A> possibleActions = env.getPossibleActions(currentState);
			A chosenAction = possibleActions.size() == 0 ? null : agent.chooseAction(currentState, possibleActions,
					random).getAction();
			AgentTransition<S, A> agentTransition = env.getAgentTransition(currentState, chosenAction);

			//agent.observeTransition(transition);
			totalReward += agentTransition.getReward();
			currentState = env.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
			numSteps++;
		}

		return currentState;
	}
}
