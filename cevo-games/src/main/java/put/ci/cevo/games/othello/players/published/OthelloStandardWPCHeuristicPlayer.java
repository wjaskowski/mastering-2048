package put.ci.cevo.games.othello.players.published;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.ArrayUtils;

/**
 * Standard heuristic WPC player for Othello
 */
public class OthelloStandardWPCHeuristicPlayer implements Factory<OthelloPlayer> {
	// @formatter:off
	static private double standard[][] = new double[][] {
		{ 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00},
		{-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
		{ 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
		{ 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
		{ 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
		{ 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
		{-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
		{ 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00},
	};
	// @formatter:on

	@Override
	public OthelloWPCPlayer create() {
		return new OthelloWPCPlayer(new WPC(ArrayUtils.flatten(standard)), BoardEvaluationType.OUTPUT_NEGATION);
	}
}
