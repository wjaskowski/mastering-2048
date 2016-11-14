package put.ci.cevo.games.othello.players.published;

import org.apache.commons.collections15.Factory;

import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.ArrayUtils;

/**
 * The best WCP strategy from
 * 
 * Learning Board Evaluation Function for Othello by Hybridizing Coevolution with Temporal Difference Learning (Marcin
 * Szubert, Wojciech Ja\’skowski, Krzysztof Krawiec), In Control and Cybernetics, volume 40, 2011, pp. 805-831
 * 
 *
 * @deprecated user SzubertJaskowskiKrawiec2011Player instead
 */

@Deprecated
public class SzubertJaskowskiKrawiec2011 implements Factory<WPC> {
	// @formatter:off
	static private double standard[][] = new double[][] {
		{ 1.01,-0.43, 0.38, 0.07, 0.00, 0.42, -0.20, 1.02},
		{-0.27,-0.74,-0.16,-0.14,-0.13,-0.25, -0.65,-0.39},
		{ 0.56,-0.30, 0.12, 0.05,-0.04, 0.07, -0.15, 0.48},
		{ 0.01,-0.08, 0.01,-0.01,-0.04,-0.02, -0.12, 0.03},
		{-0.10,-0.08, 0.01,-0.01,-0.03, 0.02, -0.04,-0.20},
		{ 0.59,-0.23, 0.06, 0.01, 0.04, 0.06, -0.19, 0.35},
		{-0.06,-0.55,-0.18,-0.08,-0.15,-0.31, -0.82,-0.58},
		{ 0.96,-0.42, 0.67,-0.02,-0.03, 0.81, -0.51, 1.01},
	};
	// @formatter:on

	@Override
	public WPC create() {
		return new WPC(ArrayUtils.flatten(standard));
	}
}
