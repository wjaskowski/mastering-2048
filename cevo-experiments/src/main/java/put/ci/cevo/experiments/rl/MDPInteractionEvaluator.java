package put.ci.cevo.experiments.rl;

import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.rl.environment.State;

public interface MDPInteractionEvaluator<S extends State> {

	InteractionResult getInteractionResult(double totalAgentReward, int numSteps, S finalEnvironmentState,
			double agentPerformance);

}
