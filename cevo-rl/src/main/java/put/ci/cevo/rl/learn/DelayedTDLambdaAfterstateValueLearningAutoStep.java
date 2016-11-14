package put.ci.cevo.rl.learn;

import static java.lang.Math.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.EvictingQueue;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.FastMath;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

/**
 * Temporal difference learning (lambda) algorithm with TCL. Learns an afterstate value function
 */
public class DelayedTDLambdaAfterstateValueLearningAutoStep<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private final double lambda;
	private final int historySize;
	private final double metaLearningRate;

	private final LearnableStateValueFunction<S> hFunction;
	private final double tau;
	private final LearnableStateValueFunction<S> viFunction;
	private final LearnableStateValueFunction<S> alphaFunction;

	public DelayedTDLambdaAfterstateValueLearningAutoStep(double metaLearningRate, double tau, double explorationRate,
			double lambda, double lambdaLimitProbability, LearnableStateValueFunction<S> alphaFunction,
			LearnableStateValueFunction<S> hFunction, LearnableStateValueFunction<S> viFunction) {
		Preconditions.checkArgument(0 <= metaLearningRate);
		Preconditions.checkArgument(0 <= explorationRate && explorationRate <= 1);
		Preconditions.checkArgument(explorationRate == 0, "Not implemented");
		Preconditions.checkArgument(0 <= lambda && lambda < 1);
		Preconditions.checkArgument(0 <= lambdaLimitProbability && lambdaLimitProbability < 1);

		this.metaLearningRate = metaLearningRate;
		this.tau = tau;
		this.lambda = lambda;
		this.historySize = (int) max(1, ceil(FastMath.log(lambda, lambdaLimitProbability)));

		this.alphaFunction = alphaFunction;
		this.hFunction = hFunction;
		this.viFunction = viFunction;
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
			Decision<A> decision = agent.chooseAction(state, model.getPossibleActions(state), random);
			AgentTransition<S, A> agentTransition = model.getAgentTransition(state, decision.getAction());
			S afterstate = agentTransition.getAfterState();
			totalPerformedActions += 1;

			if (prevAfterstate != null) {
				double targetValue = decision.getValue();
				double error = delayedUpdate(afterstateVFunction, prevAfterstate, targetValue, historyStates,
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

	private double delayedUpdate(LearnableStateValueFunction<S> afterstateVFunction, S afterstate, double targetValue,
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

	private double getErrorSum(EvictingQueue<Double> historyErrors) {
		double errorSum = 0;
		double lam = 1;
		for (double err : historyErrors) {
			errorSum += err * lam;
			lam *= lambda;
		}
		return errorSum;
	}

	private void updateStateValue(LearnableStateValueFunction<S> vFunction, S state, double error) {
		int n = vFunction.getActiveFeaturesCount();

		double[] a = new double[vFunction.getActiveFeaturesCount()];
		double[] h = new double[vFunction.getActiveFeaturesCount()];
		double[] w = new double[vFunction.getActiveFeaturesCount()];
		for (int i = 0; i < n; ++i) {
			a[i] = alphaFunction.getActiveWeight(state, i);
			h[i] = hFunction.getActiveWeight(state, i);
			w[i] = vFunction.getActiveWeight(state, i);
		}

		double m = 0.0;
		for (int i = 0; i < n; ++i) {
			double vi = viFunction.getActiveWeight(state, i);
			double abserrhi = abs(error * h[i]);
			vi = max(abserrhi, vi + tau * a[i] * (abserrhi - vi));
			if (vi != 0.0) {
				a[i] *= exp(metaLearningRate * error * h[i] / vi);
			}
			m += a[i];
			viFunction.setActiveWeight(state, i, vi);
		}

		if (m > 1) {
			for (int i = 0; i < n; ++i) {
				a[i] /= m;
			}
		}

		for (int i = 0; i < n; ++i) {
			w[i] += a[i] * error;
			h[i] = h[i] * (1 - a[i]) + a[i] * error;
		}

		for (int i = 0; i < n; ++i) {
			alphaFunction.setActiveWeight(state, i, a[i]);
			hFunction.setActiveWeight(state, i, h[i]);
			vFunction.setActiveWeight(state, i, w[i]);
		}
	}
}
