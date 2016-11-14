package put.ci.cevo.rl.agent;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.policies.GreedyAfterstatePolicy;
import put.ci.cevo.rl.agent.policies.VFunctionControlPolicy;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.StateValueFunction;

/**
 * Uses a policy (1-ply greedy by default) to choose an action based on the after state function
 */
public class AfterstateFunctionAgent<S extends State, A extends Action> implements Agent<S, A> {

	private final VFunctionControlPolicy<S, A> policy;
	private final StateValueFunction<S> afterStateFunction;

	public AfterstateFunctionAgent(StateValueFunction<S> afterStateFunction, Environment<S, A> model) {
		this(afterStateFunction, new GreedyAfterstatePolicy<>(model));
	}

	public AfterstateFunctionAgent(StateValueFunction<S> afterStateFunction, VFunctionControlPolicy<S, A> policy) {
		this.afterStateFunction = afterStateFunction;
		this.policy = policy;
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> availableActions, RandomDataGenerator random) {
		return policy.chooseAction(state, availableActions, afterStateFunction, random);
	}
}
