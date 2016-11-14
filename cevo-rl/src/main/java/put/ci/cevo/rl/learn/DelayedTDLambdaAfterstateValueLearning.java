package put.ci.cevo.rl.learn;

import com.google.common.base.Preconditions;
import com.google.common.collect.EvictingQueue;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
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
public class DelayedTDLambdaAfterstateValueLearning<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private final double lambda;
	private final int historySize;
	private final double learningRate;

	private SummaryStatistics errorStats = new SynchronizedSummaryStatistics();

	public StatisticalSummary getErrorStats() {
		return errorStats;
	}

	public DelayedTDLambdaAfterstateValueLearning(double learningRate, double explorationRate, double lambda,
			double lambdaLimitProbability) {
		Preconditions.checkArgument(0 <= learningRate && learningRate <= 1);
		Preconditions.checkArgument(0 <= explorationRate && explorationRate <= 1);
		Preconditions.checkArgument(explorationRate == 0, "Not implemented");
		Preconditions.checkArgument(0 <= lambda && lambda < 1);
		Preconditions.checkArgument(0 <= lambdaLimitProbability && lambdaLimitProbability < 1);

		this.learningRate = learningRate;
		this.lambda = lambda;
		this.historySize = (int) FastMath.ceil(FastMath.log(lambda, lambdaLimitProbability));
	}

	@Override
	public long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> afterstateVFunction,
			Agent<S, A> agent, RandomDataGenerator random) {

		EvictingQueue<S> historyStates = EvictingQueue.create(historySize);
		EvictingQueue<Double> historyErrors = EvictingQueue.create(historySize);

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
				double error = deltaUpdate(afterstateVFunction, prevAfterstate, targetValue, historyStates, historyErrors);

				errorStats.addValue(error);
			}

			prevAfterstate = afterstate;
			state = model.getEnvironmentTransition(afterstate, random).getNextState();
		}

		if (prevAfterstate != null) {
			// Important to update the last afterstate before the interaction terminates
			deltaUpdate(afterstateVFunction, prevAfterstate, 0, historyStates, historyErrors);
		}

		while (!historyStates.isEmpty()) {
			updateStateValue(afterstateVFunction, historyStates.poll(), getErrorSum(historyErrors));
			historyErrors.poll();
		}

		return totalPerformedActions;
	}

	private double deltaUpdate(LearnableStateValueFunction<S> afterstateVFunction, S afterstate, double targetValue,
			EvictingQueue<S> historyStates, EvictingQueue<Double> historyErrors) {

		double error = targetValue - afterstateVFunction.getValue(afterstate);

		historyStates.add(afterstate);
		historyErrors.add(error);

		if (historyStates.size() == historySize) {
			updateStateValue(afterstateVFunction, historyStates.poll(), getErrorSum(historyErrors));
			historyErrors.poll();
		}
		return error;
	}

	private void updateStateValue(LearnableStateValueFunction<S> vFunction, S state, double error) {
		double learningRatePerWeight = learningRate / vFunction.getActiveFeaturesCount();
		vFunction.increase(state, error * learningRatePerWeight);
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

	public void clearErrorStats() {
		errorStats.clear();
	}
}
