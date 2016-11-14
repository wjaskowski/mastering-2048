package put.ci.cevo.games.othello.players.published;

import org.apache.commons.collections15.Factory;

import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.ArrayUtils;

/**
 * The best WCP strategy from
 * 
 * Lucas, S.M.; Runarsson, T.P.,
 * "Temporal Difference Learning Versus Co-Evolution for Acquiring Othello Position Evaluation," Computational
 * Intelligence and Games, 2006 IEEE Symposium on , vol., no., pp.52,59, 22-24 May 2006 doi: 10.1109/CIG.2006.311681
 * 
 *
 * @deprecated Use LucasRunnarson2006Player
 */
@Deprecated
public class LucasRunnarson2006 implements Factory<WPC> {
	// @formatter:off
	static private double standard[][] = new double[][] {
		{ 4.622507,-1.477853, 1.409644,-0.066975,-0.305214, 1.633019, -1.050899, 4.365550},
		{-1.329145,-2.245663,-1.060633,-0.541089,-0.332716,-0.475830, -2.274535,-0.032595},
		{ 2.681550,-0.906628, 0.229372, 0.059260,-0.150415, 0.321982, -1.145060, 2.986767},
		{-0.746066,-0.317389, 0.140040,-0.045266, 0.236595, 0.158543, -0.720833,-0.131124},
		{-0.305566,-0.328398, 0.073872,-0.131472,-0.172101, 0.016603, -0.511448,-0.264125},
		{ 2.777411,-0.769551, 0.676483, 0.282190, 0.007184, 0.269876, -1.408169, 2.396238},
		{-1.566175,-3.049899,-0.637408,-0.077690,-0.648382,-0.911066, -3.329772,-0.870962},
		{ 5.046583,-1.468806, 1.545046,-0.031175, 0.263998, 2.063148, -0.148002, 5.781035},
	};
	// @formatter:on

	@Override
	public WPC create() {
		return new WPC(ArrayUtils.flatten(standard));
	}
}
