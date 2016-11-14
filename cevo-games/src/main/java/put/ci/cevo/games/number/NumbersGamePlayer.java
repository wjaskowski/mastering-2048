package put.ci.cevo.games.number;

public class NumbersGamePlayer {

	private final double[] strategy;
	private final double discretization;

	public NumbersGamePlayer(double[] strategy, double discretization) {
		this.strategy = strategy;
		this.discretization = discretization;
	}

	public double get(int i) {
		if (discretization == 0.0) {
			return strategy[i];
		}
		return discretization * Math.floor(strategy[i] / discretization);
	}

	public int getStrategyLength() {
		return strategy.length;
	}
}
