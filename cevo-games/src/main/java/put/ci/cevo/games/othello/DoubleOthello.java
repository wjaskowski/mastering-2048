package put.ci.cevo.games.othello;

import static put.ci.cevo.games.othello.Othello.*;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.board.BoardGame;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;

/**
 * Othello that internally plays two games between two players: First and Second. In the first game, First is black, and
 * Second is white; in the second game, vice versa. The game outcome is an average of the two games.
 */
public final class DoubleOthello implements BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> {

	private final Othello othello1;
	private final Othello othello2;

	public DoubleOthello() {
		this(DEFAULT_GAME_RESULT_EVALUATOR);
	}

	public DoubleOthello(GameResultEvaluator boardEvaluator) {
		this(boardEvaluator, DEFAULT_RANDOM_MOVE_PROBABILITY, DEFAULT_RANDOM_MOVE_PROBABILITY,
				DEFAULT_RANDOM_MOVE_MAX_TURNS);
	}

	/**
	 * @param forceRandomMoveProbability random move can happen to both players
	 */
	public DoubleOthello(double forceRandomMoveProbability) {
		this(DEFAULT_GAME_RESULT_EVALUATOR, forceRandomMoveProbability, forceRandomMoveProbability,
				DEFAULT_RANDOM_MOVE_MAX_TURNS);
	}

	public DoubleOthello(double forceRandomMoveProbability, int randomMoveMaxTurns) {
		this(DEFAULT_GAME_RESULT_EVALUATOR, forceRandomMoveProbability, forceRandomMoveProbability, randomMoveMaxTurns);
	}

	public DoubleOthello(double randomMoveProbabilityForFirstPlayer, double randomMoveProbabilityForSecondPlayer,
			int randomMoveMaxTurns) {
		this(DEFAULT_GAME_RESULT_EVALUATOR, randomMoveProbabilityForFirstPlayer, randomMoveProbabilityForSecondPlayer,
				randomMoveMaxTurns);
	}

	public DoubleOthello(GameResultEvaluator boardEvaluator, double firstPlayerRandomMoveProbability,
			double secondPlayerRandomMoveProbability, int randomMoveMaxTurns) {
		Preconditions.checkArgument(0 <= firstPlayerRandomMoveProbability && firstPlayerRandomMoveProbability <= 1);
		Preconditions.checkArgument(0 <= secondPlayerRandomMoveProbability && secondPlayerRandomMoveProbability <= 1);
		othello1 = new Othello(boardEvaluator, firstPlayerRandomMoveProbability, secondPlayerRandomMoveProbability,
				randomMoveMaxTurns);
		othello2 = new Othello(boardEvaluator, secondPlayerRandomMoveProbability, firstPlayerRandomMoveProbability,
				randomMoveMaxTurns);
	}

	@Override
	public GameOutcome play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		return play(blackPlayer, whitePlayer, new OthelloState(), random);
	}

	/**
	 * Play a game from initialState
	 */
	@Override
	public GameOutcome play(OthelloPlayer firstPlayer, OthelloPlayer secondPlayer, OthelloState initialState,
			RandomDataGenerator random) {
		GameOutcome firstIsBlackOutcome = othello1.play(firstPlayer, secondPlayer, initialState, random);
		GameOutcome firstIsWhiteOutcome = othello2.play(secondPlayer, firstPlayer, initialState, random);
		double blackPlayerPoints = (firstIsBlackOutcome.playerPoints() + firstIsWhiteOutcome.opponentPoints()) / 2;
		double whitePlayerPoints = (firstIsBlackOutcome.opponentPoints() + firstIsWhiteOutcome.playerPoints()) / 2;
		return new GameOutcome(blackPlayerPoints, whitePlayerPoints);
	}
}
