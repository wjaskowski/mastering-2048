package put.ci.cevo.rl.learn;

import static java.lang.Math.*;

import java.util.ArrayList;

import com.google.common.base.Preconditions;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.FastMath;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

/**
 * Temporal difference learning (lambda) algorithm. Learns an afterstate value function
 */
public class ClassicalTDLambdaAfterstateValueLearningTCL<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private final double lambda;
	private final int historySize;
	private final double metaLearningRate;

	private LearnableStateValueFunction<S> absErrors;
	private LearnableStateValueFunction<S> errors;

	public ClassicalTDLambdaAfterstateValueLearningTCL(double metaLearningRate, double explorationRate, double lambda,
			double lambdaLimitProbability, LearnableStateValueFunction<S> errors,
			LearnableStateValueFunction<S> absErrors) {
		Preconditions.checkArgument(0 <= metaLearningRate);
		Preconditions.checkArgument(0 <= explorationRate && explorationRate <= 1);
		Preconditions.checkArgument(0 <= lambda && lambda < 1);
		Preconditions.checkArgument(0 <= lambdaLimitProbability && lambdaLimitProbability < 1);

		this.metaLearningRate = metaLearningRate;
		this.lambda = lambda;
		this.historySize = (int) max(1, ceil(FastMath.log(lambda, lambdaLimitProbability)));

		this.absErrors = absErrors;
		this.errors = errors;
	}

	@Override
	public long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> afterstateVFunction,
			Agent<S, A> agent, RandomDataGenerator random) {

		EvictingQueue<S> historyStates = EvictingQueue.create(historySize);

		long totalPerformedActions = 0;
		S prevAfterstate = null;
		S state = model.sampleInitialStateDistribution(random);

		// We assume, the game ends always in state (afterstates are never terminal)
		while (!model.isTerminal(state)) {
			A action = agent.chooseAction(state, model.getPossibleActions(state), random).getAction();
			AgentTransition<S, A> agentTransition = model.getAgentTransition(state, action);
			S afterstate = agentTransition.getAfterState();
			totalPerformedActions += 1;

			if (prevAfterstate != null) {
				double afterstateValue = afterstateVFunction.getValue(afterstate);
				double targetValue = afterstateValue + agentTransition.getReward();
				deltaUpdate(afterstateVFunction, prevAfterstate, targetValue, historyStates);
			}

			prevAfterstate = afterstate;
			state = model.getEnvironmentTransition(afterstate, random).getNextState();
		}

		if (prevAfterstate != null) {
			// Important to update the last afterstate before the interaction terminates
			deltaUpdate(afterstateVFunction, prevAfterstate, 0, historyStates);
		}

		return totalPerformedActions;
	}

	private double deltaUpdate(LearnableStateValueFunction<S> afterstateVFunction, S afterstate, double targetValue,
			EvictingQueue<S> historyStates) {

		historyStates.add(afterstate);

		double error = targetValue - afterstateVFunction.getValue(afterstate);

		double metaLearningRatePerWeight = metaLearningRate / afterstateVFunction.getActiveFeaturesCount();

		double thisLambda = 1.0;
		for (S state : Lists.reverse(new ArrayList<>(historyStates))) {
			double delta = thisLambda * error * metaLearningRatePerWeight;
			updateStateValue(afterstateVFunction, state, delta);
			thisLambda *= lambda;
		}

		return error;
	}

	private void updateStateValue(LearnableStateValueFunction<S> vFunction, S state, double error) {

		int n = vFunction.getActiveFeaturesCount();

		double[] absErrors = new double[n];
		double[] normErrors = new double[n];

		for (int i = 0; i < n; ++i) {
			absErrors[i] = this.absErrors.getActiveWeight(state, i);
			normErrors[i] = errors.getActiveWeight(state, i);
		}

		for (int i = 0; i < n; ++i) {
			double alpha = absErrors[i] == 0.0 ? 1 : abs(normErrors[i]) / absErrors[i];

			vFunction.increaseActiveWeight(state, i, alpha * error);

			normErrors[i] += error;
			absErrors[i] += abs(error);
		}

		for (int i = 0; i < n; ++i) {
			this.absErrors.setActiveWeight(state, i, absErrors[i]);
			this.errors.setActiveWeight(state, i, normErrors[i]);
		}
	}
}
