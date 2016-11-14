package put.ci.cevo.rl.learn;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.policies.GreedyAfterstatePolicy;
import put.ci.cevo.rl.environment.*;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Temporal difference learning algorithm. Learns an afterstate value function
 */
public class TDAfterstateValueLearning<S extends State, A extends Action> {

	private final LearnableStateValueFunction<S> afterstateValueFunction;
	private final Environment<S, A> environment;
	private final Agent<S, A> agent;

	/**
	 * @param afterstateValueFunction function to learn
	 * @param agent                   responsible for making decisions (might decide basing on afterstateValueFunction)
	 */
	@AccessedViaReflection
	public TDAfterstateValueLearning(Environment<S, A> environment, LearnableStateValueFunction<S> afterstateValueFunction,
			Agent<S, A> agent) {
		this.environment = environment;
		this.afterstateValueFunction = afterstateValueFunction;
		this.agent = agent;
	}

	/**
	 * A shorter version where the afterstateValueFunction is used directly for making actions. It may be slower.
	 */
	@AccessedViaReflection
	public TDAfterstateValueLearning(Environment<S, A> model, LearnableStateValueFunction<S> afterstateValueFunction) {
		this(model, afterstateValueFunction, new AfterstateFunctionAgent<>(afterstateValueFunction,
				new GreedyAfterstatePolicy<>(model)));
	}

	public void fastLearningEpisode(double explorationRate, double learningRate, RandomDataGenerator random) {
		S state = environment.sampleInitialStateDistribution(random);
		AgentTransition<S, A> agentTransition;       	    // state -> afterstate
		AgentTransition<S, A> nextAgentTransition = null;   // nextState -> nextAfterstate

		while (!environment.isTerminal(state)) {
			// To explore or not to explore?
			if (random.nextUniform(0, 1) < explorationRate) {
				A randomAction = RandomUtils.pickRandom(environment.getPossibleActions(state), random);
				agentTransition = environment.getAgentTransition(state, randomAction);
			} else {
				// If not to explore then we already computed the transition
				// (our transition was nextTransition in the previous iteration if there was a previous iteration)
				if (nextAgentTransition == null)
					nextAgentTransition = environment.getAgentTransition(state, agent.chooseAction(state,
							environment.getPossibleActions(state), random).getAction());
				agentTransition = nextAgentTransition;
			}

			EnvTransition<S> envTransition = environment.getEnvironmentTransition(
					agentTransition.getAfterState(), random);
			S nextState = envTransition.getNextState();

			double correctAfterStateValue = envTransition.getReward();
			// If the next state is a terminal state then the only needed reward is provided by environmentTransition
			if (!environment.isTerminal(nextState)) {
				A nextAction = agent.chooseAction(nextState, environment.getPossibleActions(nextState), random).getAction();
				nextAgentTransition = environment.getAgentTransition(nextState, nextAction);
				correctAfterStateValue += nextAgentTransition.getReward() + afterstateValueFunction.getValue(
						nextAgentTransition.getAfterState());
			}

			double error = correctAfterStateValue - afterstateValueFunction.getValue(agentTransition.getAfterState());
			afterstateValueFunction.increase(agentTransition.getAfterState(), error * learningRate);
			state = nextState;
		}
	}
}
