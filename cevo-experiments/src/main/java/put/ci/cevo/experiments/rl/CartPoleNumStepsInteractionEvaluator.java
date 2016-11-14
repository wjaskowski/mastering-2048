package put.ci.cevo.experiments.rl;

import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.rl.environment.cartpole.CartPoleState;

public class CartPoleNumStepsInteractionEvaluator implements MDPInteractionEvaluator<CartPoleState> {

	@Override
	public InteractionResult getInteractionResult(double totalAgentReward, int numSteps,
			CartPoleState finalEnvironmentState, double agentPerformance) {
		return new InteractionResult(numSteps, -numSteps, numSteps);
	}
}
