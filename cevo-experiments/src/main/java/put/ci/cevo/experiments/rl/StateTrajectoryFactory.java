package put.ci.cevo.experiments.rl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.environment.StateTrajectory;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class StateTrajectoryFactory<S extends State> implements IndividualFactory<StateTrajectory<S>> {
	private final int minDepth;
	private final int maxDepth;

	private final Environment<S, ?> environment;

	@AccessedViaReflection
	public StateTrajectoryFactory(Environment<S, ?> environment) {
		this(environment, 1, Integer.MAX_VALUE);
	}

	@AccessedViaReflection
	public StateTrajectoryFactory(Environment<S, ?> environment, int minDepth, int maxDepth) {
		this.environment = environment;
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
	}

	@Override
	public StateTrajectory<S> createRandomIndividual(RandomDataGenerator random) {
		List<S> states = new ArrayList<>();
		S state = environment.sampleInitialStateDistribution(random);
		while (!environment.isTerminal(state)) {
			states.add(state);
			AgentTransition<S, ?> agentTransition = StateTrajectory.getRandomTransition(environment, state,
					random);
			state = agentTransition.getAfterState();
		}

		int toIndex = random.nextInt(Math.min(minDepth, states.size() - 2), Math.min(maxDepth, states.size() - 1));
		return new StateTrajectory<>(states.subList(0, toIndex));
	}
}
