package put.ci.cevo.rl.learn;

import static java.lang.Math.*;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.EnvTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.util.RandomUtils;

/**
 * Temporal difference learning algorithm. Learns an afterstate value function
 */
public class TDAfterstateValueLearningAutoStep<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private final double tau;
	private final double explorationRate;
	private final double metaLearningRate;
	private final LearnableStateValueFunction<S> hFunction;
	private final LearnableStateValueFunction<S> viFunction;
	private final LearnableStateValueFunction<S> alphaFunction;

	public TDAfterstateValueLearningAutoStep(double metaLearningRate, double tau, double explorationRate,
			LearnableStateValueFunction<S> alphaFunction, LearnableStateValueFunction<S> hFunction,
			LearnableStateValueFunction<S> viFunction) {
		this.metaLearningRate = metaLearningRate;
		this.tau = tau;
		this.explorationRate = explorationRate;
		this.alphaFunction = alphaFunction;
		this.hFunction = hFunction;
		this.viFunction = viFunction;
	}

	@Override
	public long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> vFunction,
			Agent<S, A> agent, RandomDataGenerator random) {

		long performedActions = 0;

		S prevAfterstate = null;

		S state = model.sampleInitialStateDistribution(random);
		while (!model.isTerminal(state)) {
			Decision<A> decision;
			boolean learnFromTransition = (prevAfterstate != null); // I need two after states to reconcile them
			if (explorationRate > 0 && random.nextUniform(0, 1) < explorationRate) {
				decision = Decision.of(RandomUtils.pickRandom(model.getPossibleActions(state), random));
				learnFromTransition = false; // Do not learn on random transition
			} else {
				decision = agent.chooseAction(state, model.getPossibleActions(state), random);
				performedActions += 1;
			}
			S afterstate = model.getAgentTransition(state, decision.getAction()).getAfterState();

			if (learnFromTransition) {
				double targetPrevAfterStateValue = decision.getValue();
				update(vFunction, prevAfterstate, targetPrevAfterStateValue);
			}

			if (model.isTerminal(afterstate)) {
				break;
			}

			EnvTransition<S> envTransition = model.getEnvironmentTransition(afterstate, random);
			S nextState = envTransition.getNextState();

			if (model.isTerminal(nextState)) {
				update(vFunction, afterstate, envTransition.getReward());
			}

			prevAfterstate = afterstate;
			state = nextState;
		}

		return performedActions;
	}

	public void update(LearnableStateValueFunction<S> vFunction, S state, double targetValue) {
		int n = vFunction.getActiveFeaturesCount();

		double[] a = new double[vFunction.getActiveFeaturesCount()];
		double[] h = new double[vFunction.getActiveFeaturesCount()];
		double[] w = new double[vFunction.getActiveFeaturesCount()];
		for (int i = 0; i < n; ++i) {
			a[i] = alphaFunction.getActiveWeight(state, i);
			h[i] = hFunction.getActiveWeight(state, i);
			w[i] = vFunction.getActiveWeight(state, i);
		}

		double currentValue = 0.0;
		for (int i = 0; i < n; ++i) {
			currentValue += w[i];
		}
		double error = targetValue - currentValue;

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
			h[i] = h[i]*(1 - a[i]) + a[i]*error;
		}

		for (int i = 0; i < n; ++i) {
			alphaFunction.setActiveWeight(state, i, a[i]);
			hFunction.setActiveWeight(state, i, h[i]);
			vFunction.setActiveWeight(state, i, w[i]);
		}
	}
}
