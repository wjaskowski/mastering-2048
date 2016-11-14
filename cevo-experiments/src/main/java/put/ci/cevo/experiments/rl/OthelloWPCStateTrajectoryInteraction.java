package put.ci.cevo.experiments.rl;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.rl.environment.StateTrajectory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class OthelloWPCStateTrajectoryInteraction implements InteractionDomain<WPC, StateTrajectory<OthelloState>> {

	private static final int MAX_OTHELLO_MOVES = 60;

	private final GameResultEvaluator boardEvaluator;
	private final boolean playBoth;

	private final PopulationFactory<WPC> opponentFactory;
	private final int numOpponents;

	/**
	 * @param opponentFactory
	 *            how to generate opponent players
	 * @param playBoth
	 *            if true, play two games with changed colors and return the average score
	 * @param boardEvaluator
	 *            how to count points when the game is finished
	 * @param numGames
	 *            how many games are to be played with generated opponents
	 */
	@AccessedViaReflection
	public OthelloWPCStateTrajectoryInteraction(PopulationFactory<WPC> opponentFactory,
			GameResultEvaluator boardEvaluator, int numGames, boolean playBoth) {
		this.opponentFactory = opponentFactory;
		this.numOpponents = numGames;
		this.playBoth = playBoth;
		this.boardEvaluator = boardEvaluator;
	}

	@AccessedViaReflection
	public OthelloWPCStateTrajectoryInteraction(PopulationFactory<WPC> opponentFactory, int numGames, boolean playBoth) {
		this(opponentFactory, new MorePointsGameResultEvaluator(1, 0, 0.5), numGames, playBoth);
	}

	@Override
	public InteractionResult interact(WPC candidate, StateTrajectory<OthelloState> test, RandomDataGenerator random) {
		OthelloWPCPlayer player = new OthelloWPCPlayer(candidate);
		OthelloState state = test.getLastState();
		List<WPC> opponents = opponentFactory.createPopulation(numOpponents, random);

		int totalEffort = 0;
		double firstResult = 0;
		double secondResult = 0;
		for (WPC opponent : opponents) {
			OthelloWPCPlayer opponentPlayer = new OthelloWPCPlayer(opponent);
			InteractionResult result = playBoth ? playDoubleGame(player, opponentPlayer, state, random) : play(player,
				opponentPlayer, state, random);

			totalEffort += result.getEffort();
			firstResult += result.firstResult();
			secondResult += result.secondResult();
		}

		totalEffort *= (MAX_OTHELLO_MOVES - test.getDepth());
		return new InteractionResult(firstResult, secondResult, totalEffort);
	}

	private InteractionResult playDoubleGame(OthelloPlayer player, OthelloPlayer opponent, OthelloState state,
			RandomDataGenerator random) {
		InteractionResult firstResult = play(player, opponent, state, random);
		InteractionResult secondResult = play(opponent, player, state, random);
		return InteractionResult.aggregate(firstResult, secondResult.inverted());
	}

	private InteractionResult play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, OthelloState state,
			RandomDataGenerator random) {
		Othello othelloGame = new Othello(boardEvaluator);
		GameOutcome outcome = othelloGame.play(blackPlayer, whitePlayer, state, random);

		return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
	}
}