package put.ci.cevo.experiments.rl;

import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.util.annotations.AccessedViaReflection;

//TODO: Should be somehow integrated with GameResultEvaluator
public class OthelloInteractionEvaluator implements MDPInteractionEvaluator<OthelloState> {

	private final double pointsForWin;
	private final double pointsForLose;
	private final double pointsForDraw;

	@AccessedViaReflection
	public OthelloInteractionEvaluator() {
		this(1.0, 0.0, 0.5);
	}

	@AccessedViaReflection
	public OthelloInteractionEvaluator(double pointsForWin, double pointsForLose, double pointsForDraw) {
		this.pointsForWin = pointsForWin;
		this.pointsForLose = pointsForLose;
		this.pointsForDraw = pointsForDraw;
	}

	@Override
	public InteractionResult getInteractionResult(double totalAgentReward, int numSteps,
			OthelloState finalEnvironmentState, double agentPerformance) {
		if (agentPerformance == 0) {
			return new InteractionResult(pointsForDraw, pointsForDraw, numSteps);
		} else if (agentPerformance > 0) {
			return new InteractionResult(pointsForWin, pointsForLose, numSteps);
		} else {
			return new InteractionResult(pointsForLose, pointsForWin, numSteps);
		}
	}

}
