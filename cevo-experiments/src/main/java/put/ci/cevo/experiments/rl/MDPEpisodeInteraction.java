package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.rl.Simulation;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public final class MDPEpisodeInteraction<S extends State, A extends Action> implements
		InteractionDomain<Agent<S, A>, Environment<S, A>> {

	private MDPInteractionEvaluator<S> interactionEvaluator;
	private int maxNumSteps;

	@AccessedViaReflection
	public MDPEpisodeInteraction(MDPInteractionEvaluator<S> interactionEvaluator) {
		this(interactionEvaluator, Integer.MAX_VALUE);
	}

	@AccessedViaReflection
	public MDPEpisodeInteraction(MDPInteractionEvaluator<S> interactionEvaluator, int maxNumSteps) {
		this.interactionEvaluator = interactionEvaluator;
		this.maxNumSteps = maxNumSteps;
	}

	@Override
	public InteractionResult interact(Agent<S, A> agent, Environment<S, A> env, RandomDataGenerator random) {
		Simulation<S, A> sim = new Simulation<>(agent, env);
		S finalState = sim.run(maxNumSteps, random);

		double agentPerformance = env.getAgentPerformance(sim.getTotalReward(), sim.getNumSteps(), finalState);
		return interactionEvaluator.getInteractionResult(sim.getTotalReward(), sim.getNumSteps(), finalState,
			agentPerformance);
	}
}
