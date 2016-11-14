package put.ci.cevo.games.ipd;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;

public class IteratedPrisonersDilemma implements Game<IPDPlayer, IPDPlayer> {

	private final IPDPayoffProvider payoffProvider;
	private final GameResultEvaluator evaluator;

	private final int rounds;

	public IteratedPrisonersDilemma(IPDPayoffProvider provider, int rounds) {
		this(provider, rounds, new MorePointsGameResultEvaluator(1, 0, 0.5));
	}

	public IteratedPrisonersDilemma(IPDPayoffProvider payoffProvider, int rounds, GameResultEvaluator evaluator) {
		this.payoffProvider = payoffProvider;
		this.rounds = rounds;
		this.evaluator = evaluator;
	}

	@Override
	public GameOutcome play(IPDPlayer firstPlayer, IPDPlayer secondPlayer, RandomDataGenerator random) {
		double firstPlayerPayoff = 0;
		double secondPlayerPayoff = 0;

		int lastPlayerMove = firstPlayer.firstMove();
		int lastOpponentMove = secondPlayer.firstMove();

		firstPlayerPayoff += payoffProvider.getPayoff(lastPlayerMove, lastOpponentMove);
		secondPlayerPayoff += payoffProvider.getPayoff(lastOpponentMove, lastPlayerMove);

		for (int i = 0; i < rounds - 1; i++) {
			int playerMove = firstPlayer.nextMove(lastPlayerMove, lastOpponentMove);
			int opponentMove = secondPlayer.nextMove(lastOpponentMove, lastPlayerMove);

			firstPlayerPayoff += payoffProvider.getPayoff(playerMove, opponentMove);
			secondPlayerPayoff += payoffProvider.getPayoff(opponentMove, playerMove);

			lastPlayerMove = playerMove;
			lastOpponentMove = opponentMove;
		}

		return evaluator.evaluate(firstPlayerPayoff, secondPlayerPayoff);
	}
}
