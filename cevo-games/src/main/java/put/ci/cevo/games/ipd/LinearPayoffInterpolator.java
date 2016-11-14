package put.ci.cevo.games.ipd;

import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * The n-choice IPD game can be formulated using payoffs obtained through this linear interpolation. However, the payoff
 * matrix for the game must satisfy the following conditions: 1) defection always pays more; 2) mutual cooperation has a
 * higher payoff than mutual defection; and 3) alternating between cooperation and defection pays less in comparison to
 * just playing cooperation. To this end, we normalize cooperation level to the [-1, 1] interval.
 */
public class LinearPayoffInterpolator implements IPDPayoffProvider {

	private final int choices;

	@AccessedViaReflection
	public LinearPayoffInterpolator(int choices) {
		this.choices = choices;
	}

	@Override
	public double getPayoff(double blackLevel, double whiteLevel) {
		final double diff = 2.0 / (choices - 1);
		return 2.5 - 0.5 * (1 - diff * (choices - blackLevel - 1)) + 2 * (1 - diff * (choices - whiteLevel - 1));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
