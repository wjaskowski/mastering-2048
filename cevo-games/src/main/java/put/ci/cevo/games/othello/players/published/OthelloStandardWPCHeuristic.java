package put.ci.cevo.games.othello.players.published;

import org.apache.commons.collections15.Factory;

import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.ArrayUtils;

/**
 * Standard heuristic WPC player for Othello
 * 
 *
 * @deprecated user OthelloStandardWPCHeuristicPlayer instead
 */
@Deprecated
public class OthelloStandardWPCHeuristic implements Factory<WPC> {
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
	public WPC create() {
		return new WPC(ArrayUtils.flatten(standard));
	}
}
