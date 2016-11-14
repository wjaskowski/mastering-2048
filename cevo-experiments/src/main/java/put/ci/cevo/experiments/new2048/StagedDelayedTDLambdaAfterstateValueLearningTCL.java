package put.ci.cevo.experiments.new2048;

import static java.lang.Math.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.EvictingQueue;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.FastMath;
import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.rl.learn.VFunctionLearningAlgorithm;

/**
 * Temporal difference learning (lambda) algorithm with TCL. Learns an afterstate value function
 */
public class StagedDelayedTDLambdaAfterstateValueLearningTCL
		implements VFunctionLearningAlgorithm<State2048, Action2048> {

	private final double lambda;
	private final int historySize;
	private final double metaLearningRate;
	private final int maxSegment;
	private final int numSegments;

	private LearnableStateValueFunction<State2048> absErrors;
	private LearnableStateValueFunction<State2048> errors;

	public StagedDelayedTDLambdaAfterstateValueLearningTCL(double metaLearningRate, double explorationRate,
			double lambda, double lambdaLimitProbability, int maxSegment, int numSegments,
			LearnableStateValueFunction<State2048> errors, LearnableStateValueFunction<State2048> absErrors) {
		this.maxSegment = maxSegment;
		this.numSegments = numSegments;
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
	public long learnFromEpisode(Environment<State2048, Action2048> model, LearnableStateValueFunction<State2048> afterstateVFunction,
			Agent<State2048, Action2048> agent, RandomDataGenerator random) {
		return learnFromEpisode(model, afterstateVFunction, agent, random, model.sampleInitialStateDistribution(random), new State2048[32]);
	}

	public long learnFromEpisode(Environment<State2048, Action2048> model, LearnableStateValueFunction<State2048> afterstateVFunction,
			Agent<State2048, Action2048> agent, RandomDataGenerator random, State2048 initialState, State2048[] stages) {

		EvictingQueue<State2048> historyStates = EvictingQueue.create(historySize);
		EvictingQueue<Double> historyErrors = EvictingQueue.create(historySize);

		long totalPerformedActions = 0;
		State2048 prevAfterstate = null;
		State2048 state = initialState;

		// We assume, the game ends always in state (afterstates are never terminal)

		while (!model.isTerminal(state)) {
			Decision<Action2048> decision = agent.chooseAction(state, model.getPossibleActions(state), random);
			AgentTransition<State2048, Action2048> agentTransition = model.getAgentTransition(state, decision.getAction());
			State2048 afterstate = agentTransition.getAfterState();

			int stage = afterstate.getStage(numSegments, maxSegment);
			if (stages[stage] == null)
				stages[stage] = afterstate;

			totalPerformedActions += 1;

			if (prevAfterstate != null) {
				double targetValue = decision.getValue();
				delayedUpdate(afterstateVFunction, prevAfterstate, targetValue, historyStates,
						historyErrors);
			}

			prevAfterstate = afterstate;
			state = model.getEnvironmentTransition(afterstate, random).getNextState();
		}

		if (prevAfterstate != null) {
			// Important to update the last afterstate before the interaction terminates
			delayedUpdate(afterstateVFunction, prevAfterstate, 0, historyStates, historyErrors);
		}

		while (!historyStates.isEmpty()) {
			updateStateValue(afterstateVFunction, historyStates.poll(), getErrorSum(historyErrors));
			historyErrors.poll();
		}

		return totalPerformedActions;
	}

	private double delayedUpdate(LearnableStateValueFunction<State2048> afterstateVFunction, State2048 afterstate, double targetValue,
			EvictingQueue<State2048> historyStates, EvictingQueue<Double> historyErrors) {

		double error = targetValue - afterstateVFunction.getValue(afterstate);

		historyStates.add(afterstate);
		historyErrors.add(error);

		if (historyStates.size() == historySize) {
			updateStateValue(afterstateVFunction, historyStates.poll(), getErrorSum(historyErrors));
			historyErrors.poll();
		}
		return error;
	}

	private double getErrorSum(EvictingQueue<Double> historyErrors) {
		double errorSum = 0;
		double lam = 1;
		for (double err : historyErrors) {
			errorSum += err * lam;
			lam *= lambda;
		}
		return errorSum;
	}

	private void updateStateValue(LearnableStateValueFunction<State2048> vFunction, State2048 state, double error) {

		int n = vFunction.getActiveFeaturesCount();
		double metaLearningRatePerWeight = metaLearningRate / n;

		double[] absErrors = new double[n];
		double[] normErrors = new double[n];

		for (int i = 0; i < n; ++i) {
			absErrors[i] = this.absErrors.getActiveWeight(state, i);
			normErrors[i] = errors.getActiveWeight(state, i);
		}

		for (int i = 0; i < n; ++i) {
			double alpha = absErrors[i] == 0.0 ? 1 : abs(normErrors[i]) / absErrors[i];

			vFunction.increaseActiveWeight(state, i, metaLearningRatePerWeight * alpha * error);

			normErrors[i] += error;
			absErrors[i] += abs(error);
		}

		for (int i = 0; i < n; ++i) {
			this.absErrors.setActiveWeight(state, i, absErrors[i]);
			this.errors.setActiveWeight(state, i, normErrors[i]);
		}
	}
}
