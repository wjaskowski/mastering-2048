package put.ci.cevo.experiments.rl;

import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.OthelloState;

public class OthelloPointPerPieceInteractionEvaluator implements MDPInteractionEvaluator<OthelloState> {

	@Override
	public InteractionResult getInteractionResult(double totalAgentReward, int numSteps,
			OthelloState finalEnvironmentState, double agentPerformance) {

		OthelloBoard board = finalEnvironmentState.getBoard();

		int blackPoints = BoardUtils.countPieces(board, Board.BLACK);
		int whitePoints = BoardUtils.countPieces(board, Board.WHITE);

		if (agentPerformance > 0) {
			return new InteractionResult(
				Math.max(blackPoints, whitePoints), Math.min(blackPoints, whitePoints), numSteps);
		} else {
			return new InteractionResult(
				Math.min(blackPoints, whitePoints), Math.max(blackPoints, whitePoints), numSteps);
		}
	}
}
