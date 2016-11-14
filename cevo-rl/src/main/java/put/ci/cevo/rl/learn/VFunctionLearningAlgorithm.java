package put.ci.cevo.rl.learn;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.policies.GreedyAfterstatePolicy;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public interface VFunctionLearningAlgorithm<S extends State, A extends Action> {

	/**
	 * @param model     in which we interact
	 * @param vFunction which we learn
	 * @param agent     which makes actions during interaction
	 * @return Number of actions performed by the agent (computational effort)
	 */
	long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> vFunction,
			Agent<S, A> agent, RandomDataGenerator random);

	/**
	 * A shorter version where the afterstateVFunction is used directly for making actions. It may be slower if there
	 * are more effective agent implementations
	 */
	default long learnFromEpisode(Environment<S, A> model, LearnableStateValueFunction<S> afterstateVFunction,
			RandomDataGenerator random) {
		return learnFromEpisode(model, afterstateVFunction, new AfterstateFunctionAgent<>(afterstateVFunction,
				new GreedyAfterstatePolicy<>(model)), random);
	}
}
