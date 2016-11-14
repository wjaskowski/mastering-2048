package put.ci.cevo.rl.agent.policies;

import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.util.RandomUtils;

/**
 * Decorating QFunctionControlPolicy with epsilon-greedy policy
 */
public class EpsilonGreedyQFunctionPolicy<S extends State, A extends Action> implements QFunctionControlPolicy<S, A> {
	private final QFunctionControlPolicy<S, A> policy;
	private final double epsilon;

	public EpsilonGreedyQFunctionPolicy(QFunctionControlPolicy<S, A> policy, double epsilon) {
		Preconditions.checkArgument(0 <= epsilon && epsilon <= 1, Double.toString(epsilon));
		this.policy = policy;
		this.epsilon = epsilon;
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> actions, ActionValueFunction<S, A> qFunction,
			RandomDataGenerator random) {
		if (epsilon != 0 && random.nextUniform(0.0, 1.0) < epsilon) {
			return Decision.of(RandomUtils.pickRandom(actions, random));
		}

		return policy.chooseAction(state, actions, qFunction, random);
	}
}
