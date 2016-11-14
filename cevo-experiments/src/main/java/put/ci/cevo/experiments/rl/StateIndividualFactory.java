package put.ci.cevo.experiments.rl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.StateTrajectory;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class StateIndividualFactory<S extends State> implements IndividualFactory<S> {

	private final Environment<S, ?> environment;
	private int minNumMovesToEnd;
	private int maxNumMovesFromStart;

	@AccessedViaReflection
	public StateIndividualFactory(Environment<S, ?> environment, int minNumMovesToEnd) {
		this(environment, minNumMovesToEnd, Integer.MAX_VALUE);
	}

	@AccessedViaReflection
	public StateIndividualFactory(Environment<S, ?> environment, int minNumMovesToEnd, int maxNumMovesFromStart) {
		this.environment = environment;
		this.minNumMovesToEnd = minNumMovesToEnd;
		this.maxNumMovesFromStart = maxNumMovesFromStart;
	}

	@Override
	public S createRandomIndividual(RandomDataGenerator random) {
		List<S> states = generateRandomTrajectory(random);

		int lastIndex = Math.min(Math.min(states.size(), maxNumMovesFromStart),
			Math.max(1, states.size() - minNumMovesToEnd));
		return RandomUtils.pickRandom(states.subList(0, lastIndex), random);
	}

	public S createRandomIndividualAtDepth(int stateDepth, RandomDataGenerator random) {
		List<S> states = generateRandomTrajectory(random);
		return states.get(Math.min(stateDepth, Math.max(0, states.size() - minNumMovesToEnd)));
	}

	private List<S> generateRandomTrajectory(RandomDataGenerator random) {
		List<S> states = new ArrayList<>();
		S state = environment.sampleInitialStateDistribution(random);
		while (!environment.isTerminal(state)) {
			states.add(state);
			AgentTransition<S, ?> agentTransition = StateTrajectory.getRandomTransition(environment, state,
					random);
			state = agentTransition.getAfterState();
		}
		return states;
	}
}
