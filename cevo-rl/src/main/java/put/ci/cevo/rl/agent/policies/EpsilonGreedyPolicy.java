package put.ci.cevo.rl.agent.policies;

import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.StateValueFunction;
import put.ci.cevo.util.RandomUtils;

public class EpsilonGreedyPolicy<S extends State, A extends Action> implements VFunctionControlPolicy<S, A> {
	private final VFunctionControlPolicy<S, A> policy;
	private final double epsilon;

	public EpsilonGreedyPolicy(VFunctionControlPolicy<S, A> policy, double epsilon) {
		Preconditions.checkArgument(0 <= epsilon && epsilon <= 1, Double.toString(epsilon));
		this.policy = policy;
		this.epsilon = epsilon;
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> actions, StateValueFunction<S> vFunction,
			RandomDataGenerator random) {

		if (epsilon != 0 && random.nextUniform(0.0, 1.0) < epsilon) {
			return Decision.of(RandomUtils.pickRandom(actions, random));
		}

		return policy.chooseAction(state, actions, vFunction, random);
	}
}
