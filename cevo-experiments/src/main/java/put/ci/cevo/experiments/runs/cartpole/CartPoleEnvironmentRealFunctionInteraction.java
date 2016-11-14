package put.ci.cevo.experiments.runs.cartpole;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.experiments.rl.CartPoleNumStepsInteractionEvaluator;
import put.ci.cevo.experiments.rl.MDPEpisodeInteraction;
import put.ci.cevo.experiments.rl.MDPGenotypeMappingInteraction;
import put.ci.cevo.experiments.rl.RealFunctionActionValueAgentMapping;
import put.ci.cevo.experiments.rl.RealFunctionContinuousActionAgentMapping;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.environment.ContinuousAction;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.cartpole.CartPoleEnvironment;
import put.ci.cevo.rl.environment.cartpole.CartPoleState;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class CartPoleEnvironmentRealFunctionInteraction implements InteractionDomain<CartPoleEnvironment, RealFunction> {

	private final int numMaxSteps;
	private final boolean continuousAction;
	private boolean binary;

	@AccessedViaReflection
	public CartPoleEnvironmentRealFunctionInteraction(int numMaxSteps, boolean continuousAction, boolean binary) {
		this.numMaxSteps = numMaxSteps;
		this.continuousAction = continuousAction;
		this.binary = binary;
	}

	@Override
	public InteractionResult interact(CartPoleEnvironment environment, RealFunction function, RandomDataGenerator random) {
		GenotypePhenotypeMapper<RealFunction, Agent<CartPoleState, ContinuousAction>> agentMapping;
		if (continuousAction) {
			agentMapping = new RealFunctionContinuousActionAgentMapping<>(environment);
		} else {
			agentMapping = new RealFunctionActionValueAgentMapping<>(environment);
		}

		MDPEpisodeInteraction<CartPoleState, ContinuousAction> mdpInteraction = new MDPEpisodeInteraction<>(
				new CartPoleNumStepsInteractionEvaluator(), numMaxSteps);
		InteractionDomain<RealFunction, Environment<CartPoleState, ContinuousAction>> interaction = new MDPGenotypeMappingInteraction<>(
				agentMapping, mdpInteraction);

		InteractionResult result = interaction.interact(function, environment, random);
		
		if (!binary) {
			double envDifficulty = (double) (numMaxSteps - result.firstResult()) / (double) numMaxSteps;
			return new InteractionResult(envDifficulty, 1 - envDifficulty, result.getEffort());
		} else if (result.firstResult() >= numMaxSteps) {
			return new InteractionResult(0.0, 1.0, result.getEffort());
		} else {
			return new InteractionResult(1.0, 0.0, result.getEffort());
		}
	}
}
