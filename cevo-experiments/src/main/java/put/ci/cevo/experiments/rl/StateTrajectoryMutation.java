package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.environment.StateTrajectory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class StateTrajectoryMutation<S extends State> implements MutationOperator<StateTrajectory<S>> {

	private final Environment<S, ?> env;

	@AccessedViaReflection
	public StateTrajectoryMutation(Environment<S, ?> env) {
		this.env = env;
	}

	@Override
	public StateTrajectory<S> produce(StateTrajectory<S> individual, RandomDataGenerator random) {
		if (random.getRandomGenerator().nextBoolean() && individual.getDepth() > 1) {
			return individual.shorten(1);
		} else {
			return individual.lengthen(1, env, random);
		}
	}
}
