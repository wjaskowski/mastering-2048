package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class OthelloStateInteraction<T> implements InteractionDomain<T, OthelloState> {

	private final OthelloPlayer opponent;
	private final GameResultEvaluator boardEvaluator;

	private final GenotypePhenotypeMapper<T, OthelloPlayer> playerMapper;

	@AccessedViaReflection
	public OthelloStateInteraction(GenotypePhenotypeMapper<T, OthelloPlayer> playerMapper,
			GameResultEvaluator boardEvaluator, OthelloPlayer opponent) {
		this.playerMapper = playerMapper;
		this.boardEvaluator = boardEvaluator;
		this.opponent = opponent;
	}

	@AccessedViaReflection
	public OthelloStateInteraction(GenotypePhenotypeMapper<T, OthelloPlayer> playerMapper) {
		this(playerMapper, new MorePointsGameResultEvaluator(1, 0, 0.5),
				new OthelloStandardWPCHeuristicPlayer().create());
	}

	@Override
	public InteractionResult interact(T genotype, OthelloState state, RandomDataGenerator random) {
		OthelloPlayer player = playerMapper.getPhenotype(genotype, random);
		Othello othelloGame = new Othello(boardEvaluator);
		if (state.getPlayerToMove() == OthelloBoard.BLACK) {
			GameOutcome outcome = othelloGame.play(player, opponent, state, random);
			return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
		} else {
			GameOutcome outcome = othelloGame.play(opponent, player, state, random);
			return new InteractionResult(outcome.opponentPoints(), outcome.playerPoints(), 1);
		}
	}
}