package put.ci.cevo.rl.learn;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.FastMath;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.*;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.util.RandomUtils;

/**
 * Temporal difference learning algorithm. Learns an afterstate value function
 */
public class TDAfterstateValueLearningIDBD<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private double explorationRate;
	private double metaLearningRate;
	private final LearnableStateValueFunction<S> betaFunction;
	private final LearnableStateValueFunction<S> hFunction;

	public TDAfterstateValueLearningIDBD(double metaLearningRate, double explorationRate,
			LearnableStateValueFunction<S> betaFunction, LearnableStateValueFunction<S> hFunction) {
		this.explorationRate = explorationRate;
		this.metaLearningRate = metaLearningRate;
		this.betaFunction = betaFunction;
		this.hFunction = hFunction;
	}

	@Override
	public long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> vFunction,
			Agent<S, A> agent, RandomDataGenerator random) {

		long performedActions = 0;

		AgentTransition<S, A> prevAgentTransition = null;   // prevState -> prevAfterstate
		EnvTransition<S> prevEnvTransition = null;        // prevAfterState -> state
		AgentTransition<S, A> agentTransition;              // state -> afterState
		EnvTransition<S> envTransition;                    // afterState -> nextState

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
					targetPrevAfterStateValue += vFunction.getValue(afterState);
					// If afterState is terminal, we should strive to make V(prevAfterState) = reward
					// (compared to: V(prevAfterState) = reward + V(afterState) )
				}
				update(vFunction, prevAfterState, targetPrevAfterStateValue);
			}

			if (model.isTerminal(afterState)) {
				break;
			}

			envTransition = model.getEnvironmentTransition(agentTransition.getAfterState(), random);
			S nextState = envTransition.getNextState();

			if (model.isTerminal(nextState)) {
				update(vFunction, afterState, envTransition.getReward());
			}

			prevAgentTransition = agentTransition;
			prevEnvTransition = envTransition;
			state = nextState;
		}

		return performedActions;
	}

	public void update(LearnableStateValueFunction<S> vFunction, S state, double targetValue) {
		double error = targetValue - vFunction.getValue(state);


		double learningRatePerWeight = metaLearningRate / vFunction.getActiveFeaturesCount();

		for (int i = 0; i < vFunction.getActiveFeaturesCount(); ++i) {
			double oldH = hFunction.getActiveWeight(state, i);
			double oldBeta = betaFunction.getActiveWeight(state, i);
			double newBeta = oldBeta + learningRatePerWeight * error * oldH;
			betaFunction.setActiveWeight(state, i, newBeta);

			double alpha = FastMath.exp(newBeta);

			double oldWeight = vFunction.getActiveWeight(state, i);
			double newWeight = oldWeight + alpha * error;
			vFunction.setActiveWeight(state, i, newWeight);

			double newH = oldH * Math.max(0, 1 - alpha) + alpha * error;
			hFunction.setActiveWeight(state, i, newH);
		}
	}
}
