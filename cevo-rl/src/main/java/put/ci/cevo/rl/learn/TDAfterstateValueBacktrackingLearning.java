package put.ci.cevo.rl.learn;

import java.util.ArrayList;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.*;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.util.RandomUtils;

/**
 * Temporal difference learning algorithm. Learns an afterstate value function
 */
public class TDAfterstateValueBacktrackingLearning<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private final double backupPercent;
	private final double restartProbability;
	private final VFunctionUpdateAlgorithm<S> updateAlgorithm;

	private double explorationRate;

	public TDAfterstateValueBacktrackingLearning(VFunctionUpdateAlgorithm<S> updateAlgorithm, double explorationRate,
			double backupPercent,
			double restartProbability) {
		Preconditions.checkArgument(0 <= backupPercent && backupPercent <= 1);
		Preconditions.checkArgument(0 <= restartProbability && restartProbability <= 1);

		this.backupPercent = backupPercent;
		this.restartProbability = restartProbability;
		this.explorationRate = explorationRate;
		this.updateAlgorithm = updateAlgorithm;
	}

	public long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> vFunction, Agent<S, A> agent,
			RandomDataGenerator random) {

		ArrayList<S> backup = new ArrayList<>();
		S initialState = model.sampleInitialStateDistribution(random);
		int totalPerformedActions = 0;
		do {
			totalPerformedActions += learnFromEpisode(model, vFunction, agent, initialState, backup, random);

			// Backtrack to backupPercent of the backup
			backup.subList((int)(backup.size() * backupPercent), backup.size()).clear();
			initialState = backup.size() > 0 ? backup.get(backup.size() - 1) : null;
		} while (initialState != null && restartProbability < random.nextUniform(0, 1));
		return totalPerformedActions;
	}

	private long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> afterstateVFunction,
			Agent<S, A> agent, S initialState, ArrayList<S> backup, RandomDataGenerator random) {

		long totalPerformedActions = 0;

		AgentTransition<S, A> prevAgentTransition = null;   // prevState -> prevAfterstate
		EnvTransition<S> prevEnvTransition = null;  		// prevAfterState -> state
		AgentTransition<S, A> agentTransition;              // state -> afterState
		EnvTransition<S> envTransition;             		// afterState -> nextState

		S state = initialState;
		while (!model.isTerminal(state)) {
			A action;
			boolean doReconcileAfterStates = (prevAgentTransition != null); // I need two after states to reconcile them
			if (0 < explorationRate && random.nextUniform(0, 1) < explorationRate) {
				action = RandomUtils.pickRandom(model.getPossibleActions(state), random);
				doReconcileAfterStates = false; // Do not learn on random transition
			} else {
				action = agent.chooseAction(state, model.getPossibleActions(state), random).getAction();
				totalPerformedActions += 1;
			}
			agentTransition = model.getAgentTransition(state, action);

			S afterState = agentTransition.getAfterState();
			if (doReconcileAfterStates) {
				S prevAfterState = prevAgentTransition.getAfterState();
				double targetPrevAfterStateValue = prevEnvTransition.getReward() + agentTransition.getReward();
				if (!model.isTerminal(afterState)) {
					targetPrevAfterStateValue += afterstateVFunction.getValue(afterState);
					// If afterState is terminal, we should strive to make V(prevAfterState) = reward
					// (compared to: V(prevAfterState) = reward + V(afterState) )
				}
				updateAlgorithm.update(afterstateVFunction, prevAfterState, targetPrevAfterStateValue);
			}

			if (model.isTerminal(afterState)) {
				break;
			}

			envTransition = model.getEnvironmentTransition(agentTransition.getAfterState(), random);
			S nextState = envTransition.getNextState();

			if (model.isTerminal(nextState)) {
				updateAlgorithm.update(afterstateVFunction, afterState , envTransition.getReward());
			}

			prevAgentTransition = agentTransition;
			prevEnvTransition = envTransition;
			state = nextState;

			backup.add(state);
		}
		return totalPerformedActions;
	}
}
