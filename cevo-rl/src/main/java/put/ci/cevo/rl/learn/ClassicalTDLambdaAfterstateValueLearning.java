package put.ci.cevo.rl.learn;

import java.util.ArrayList;

import com.google.common.base.Preconditions;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
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
public class ClassicalTDLambdaAfterstateValueLearning<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private final double lambda;
	private final int historySize;
	private final double learningRate;

	private SummaryStatistics errorStats = new SynchronizedSummaryStatistics();

	public StatisticalSummary getErrorStats() {
		return errorStats;
	}

	public ClassicalTDLambdaAfterstateValueLearning(double learningRate, double explorationRate, double lambda,
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
				double error = deltaUpdate(afterstateVFunction, prevAfterstate, targetValue, historyStates);

				errorStats.addValue(error);
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

		double learningRatePerWeight = learningRate / afterstateVFunction.getActiveFeaturesCount();

		double thisLambda = 1.0;
		for (S state : Lists.reverse(new ArrayList<>(historyStates))) {
			double delta = thisLambda * error * learningRatePerWeight;
			afterstateVFunction.increase(state, delta);
			thisLambda *= lambda;
		}

		return error;
	}

	public void clearErrorStats() {
		errorStats.clear();
	}
}
