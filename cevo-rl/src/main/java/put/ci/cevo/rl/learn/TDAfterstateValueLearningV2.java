package put.ci.cevo.rl.learn;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.*;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.util.RandomUtils;

/**
 * Temporal difference learning algorithm. Learns an afterstate value function
 */
public class TDAfterstateValueLearningV2<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private double explorationRate;
	private double learningRate;

	public TDAfterstateValueLearningV2(double learningRate, double explorationRate) {
		this.explorationRate = explorationRate;
		this.learningRate = learningRate;
	}

	@Override
	public long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> afterstateValueFunction,
			Agent<S, A> agent, RandomDataGenerator random) {

		long performedActions = 0;

		AgentTransition<S, A> prevAgentTransition = null;   // prevState -> prevAfterstate
		EnvTransition<S> prevEnvTransition = null;  		// prevAfterState -> state
		AgentTransition<S, A> agentTransition;              // state -> afterState
		EnvTransition<S> envTransition;             		// afterState -> nextState

		S state = model.sampleInitialStateDistribution(random);
		while (!model.isTerminal(state)) {
			A action;
			boolean doReconcileAfterStates = (prevAgentTransition != null); // I need two after states to reconcile them
			if (explorationRate > 0 && random.nextUniform(0, 1) < explorationRate) {
				action = RandomUtils.pickRandom(model.getPossibleActions(state), random);
				doReconcileAfterStates = false; // Do not learn on random transition
			} else {
				action = agent.chooseAction(state, model.getPossibleActions(state), random).getAction();
				performedActions += 1;
			}
			agentTransition = model.getAgentTransition(state, action);

			S afterState = agentTransition.getAfterState();
			if (doReconcileAfterStates) {
				S prevAfterState = prevAgentTransition.getAfterState();
				double targetPrevAfterStateValue = prevEnvTransition.getReward() + agentTransition.getReward();
				if (!model.isTerminal(afterState)) {
					targetPrevAfterStateValue += afterstateValueFunction.getValue(afterState);
					// If afterState is terminal, we should strive to make V(prevAfterState) = reward
					// (compared to: V(prevAfterState) = reward + V(afterState) )
				}
				double error = targetPrevAfterStateValue - afterstateValueFunction.getValue(prevAfterState);
				updateStateValue(afterstateValueFunction, prevAfterState, error);
			}

			if (model.isTerminal(afterState)) {
				break;
			}

			envTransition = model.getEnvironmentTransition(agentTransition.getAfterState(), random);
			S nextState = envTransition.getNextState();

			if (model.isTerminal(nextState)) {
				double error = envTransition.getReward() - afterstateValueFunction.getValue(afterState);
				updateStateValue(afterstateValueFunction, afterState, error);
			}

			prevAgentTransition = agentTransition;
			prevEnvTransition = envTransition;
			state = nextState;
		}

		return performedActions;
	}

	private void updateStateValue(LearnableStateValueFunction<S> vFunction, S state, double error) {
		double learningRatePerWeight = learningRate / vFunction.getActiveFeaturesCount();
		vFunction.increase(state, error * learningRatePerWeight);
	}
}
