package put.ci.cevo.rl.environment;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomizedEnvironment<S extends State, A extends Action> extends EnvironmentDecorator<S, A> {

	private RandomDataGenerator random;
	private double epsilon;

	public RandomizedEnvironment(Environment<S, A> env, double epsilon, RandomDataGenerator random) {
		this.env = env;
		this.epsilon = epsilon;
		this.random = random;
	}

	@Override
	public AgentTransition<S, A> getAgentTransition(S state, A action) {
		if (this.random.nextUniform(0.0, 1.0) < epsilon) {
			List<A> actions = env.getPossibleActions(state);
			if (actions.size() < 2) {
				return env.getAgentTransition(state, action);
			} else {
				A randomAction = actions.get(this.random.nextInt(0, actions.size() - 1));
				return env.getAgentTransition(state, randomAction);
			}
		} else {
			return env.getAgentTransition(state, action);
		}
	}
}
