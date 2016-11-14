package put.ci.cevo.rl.learn;

import static java.lang.Math.abs;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.*;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.util.RandomUtils;

/**
 * Temporal difference learning algorithm. Learns an afterstate value function
 */
public class TDAfterstateValueLearningTCL<S extends State, A extends Action>
		implements VFunctionLearningAlgorithm<S, A> {

	private double explorationRate;
	private double metaLearningRate;
	private final LearnableStateValueFunction<S> errors;
	private final LearnableStateValueFunction<S> absErrors;

	public TDAfterstateValueLearningTCL(double metaLearningRate, double explorationRate,
			LearnableStateValueFunction<S> errors, LearnableStateValueFunction<S> absErrors) {
		this.explorationRate = explorationRate;
		this.metaLearningRate = metaLearningRate;
		this.errors = errors;
		this.absErrors = absErrors;
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
		double metaLearningRatePerWeight = metaLearningRate / n;

		double[] absErrors = new double[n];
		double[] normErrors = new double[n];
		double[] w = new double[n];

		for (int i = 0; i < n; ++i) {
			absErrors[i] = this.absErrors.getActiveWeight(state, i);
			normErrors[i] = errors.getActiveWeight(state, i);
			w[i] = vFunction.getActiveWeight(state, i);
		}

		double currentValue = 0.0;
		for (int i = 0; i < n; ++i) {
			currentValue += w[i];
		}
		double error = targetValue - currentValue;

		for (int i = 0; i < n; ++i) {
			double alpha = absErrors[i] == 0.0 ? 1 : abs(normErrors[i]) / absErrors[i];

			w[i] += metaLearningRatePerWeight * alpha * error;

			normErrors[i] += error;
			absErrors[i] += abs(error);
		}

		for (int i = 0; i < n; ++i) {
			this.absErrors.setActiveWeight(state, i, absErrors[i]);
			this.errors.setActiveWeight(state, i, normErrors[i]);
			vFunction.setActiveWeight(state, i, w[i]);
		}
	}
}
