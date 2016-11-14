package put.ci.cevo.games.ipd;

public class IPDPlayer {

	private final int[] strategy;
	private final int choices;

	public IPDPlayer(int[] strategy) {
		this(strategy, (int) Math.sqrt(strategy.length - 1));
	}

	public IPDPlayer(int[] strategy, int choices) {
		this.strategy = strategy;
		this.choices = choices;
	}

	public int firstMove() {
		return strategy[0];
	}

	public int nextMove(int lastMove, int opponentLastMove) {
		return strategy[1 + lastMove * choices + opponentLastMove];
	}

}
