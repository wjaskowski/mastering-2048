package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class OthelloStateWPCInteraction implements InteractionDomain<OthelloState, WPC> {

	private final GameResultEvaluator boardEvaluator;
	private final OthelloPlayer player;

	@AccessedViaReflection
	public OthelloStateWPCInteraction() {
		this(new MorePointsGameResultEvaluator(1, 0, 0.5), new OthelloStandardWPCHeuristicPlayer().create());
	}

	@AccessedViaReflection
	public OthelloStateWPCInteraction(GameResultEvaluator boardEvaluator, OthelloPlayer opponent) {
		this.boardEvaluator = boardEvaluator;
		this.player = opponent;
	}

	@Override
	public InteractionResult interact(OthelloState state, WPC opponentWpc, RandomDataGenerator random) {
		Othello othelloGame = new Othello(boardEvaluator);
		OthelloWPCPlayer opponent = new OthelloWPCPlayer(opponentWpc);
		if (state.getPlayerToMove() == OthelloBoard.BLACK) {
			GameOutcome outcome = othelloGame.play(opponent, player, state, random);
			return new InteractionResult(outcome.opponentPoints(), outcome.playerPoints(), 1);
		} else {
			GameOutcome outcome = othelloGame.play(player, opponent, state, random);
			return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
		}
	}
}