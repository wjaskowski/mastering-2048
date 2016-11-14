package put.ci.cevo.games.othello.players.published;

import org.apache.commons.collections15.Factory;

import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.ArrayUtils;

/**
 * The best WCP strategy from
 * 
 * Coevolutionary Temporal Difference Learning for Othello (Marcin Szubert, Wojciech Ja’skowski, Krzysztof Krawiec), In
 * IEEE Symposium on Computational Intelligence and Games, 2009, pp. 104-111
 * 
 *
 * @deprecated user SzubertJaskowskiKrawiec2009Player instead
 */
@Deprecated
public class SzubertJaskowskiKrawiec2009 implements Factory<WPC> {
	// @formatter:off
	static private double standard[][] = new double[][] {
    	{ 1.02, -0.27, 0.55, -0.10,  0.08, 0.47, -0.38, 1.00},
    	{-0.13, -0.52,-0.18, -0.07, -0.18,-0.29, -0.68,-0.44},
    	{ 0.55, -0.24, 0.02, -0.01, -0.01, 0.10, -0.13, 0.77},
    	{-0.10, -0.10, 0.01, -0.01,  0.00,-0.01, -0.09,-0.05},
    	{ 0.05, -0.17, 0.02, -0.04, -0.03, 0.03, -0.09,-0.05},
    	{ 0.56, -0.25, 0.05,  0.02, -0.02, 0.17, -0.35, 0.42},
    	{-0.25, -0.71,-0.24, -0.23, -0.08,-0.29, -0.63,-0.24},
    	{ 0.93, -0.44, 0.55,  0.22, -0.15, 0.74, -0.57, 0.97},
	};
	// @formatter:on

	@Override
	public WPC create() {
		return new WPC(ArrayUtils.flatten(standard));
	}
}
