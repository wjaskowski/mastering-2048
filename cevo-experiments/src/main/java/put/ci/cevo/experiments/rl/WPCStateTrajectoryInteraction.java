package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.environment.StateTrajectory;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCStateTrajectoryInteraction<S extends State, A extends Action> implements
		InteractionDomain<WPC, StateTrajectory<S>> {

	private final Environment<S, A> environment;
	private final GenotypePhenotypeMapper<WPC, Agent<S, A>> wpcAgentMapping;

	@AccessedViaReflection
	public WPCStateTrajectoryInteraction(Environment<S, A> environment,
			GenotypePhenotypeMapper<WPC, Agent<S, A>> wpcAgentMapping) {
		this.environment = environment;
		this.wpcAgentMapping = wpcAgentMapping;
	}

	@Override
	public InteractionResult interact(WPC candidate, StateTrajectory<S> test, RandomDataGenerator random) {
		Agent<S, A> agent = wpcAgentMapping.getPhenotype(candidate, random);
		S state = test.getLastState();
		state = environment.getEnvironmentTransition(state, random).getNextState();

		double totalReward = 0;
		int numSteps = 0;
		while (!environment.isTerminal(state)) {
			AgentTransition<S, A> agentTransition = StateTrajectory.getAgentTransition(environment, state, agent,
					random);
			totalReward += agentTransition.getReward();
			state = environment.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
			numSteps++;
		}
		// environment.getFinalReward(state);

		return new InteractionResult(totalReward, -totalReward, numSteps);
	}

}
