package put.ci.cevo.experiments.othello;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.GameResultEvaluator;
import put.ci.cevo.games.MorePointsGameResultEvaluator;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Describes interaction for the game of Othello.
 * 
 * @deprecated Works on genotypes. Use {@link OthelloInteractionDomain} instead. Watchout: if case of playBoth=true,
 * you should use DoubleOthello instead of Othello.
 */
@Deprecated
public class OthelloInteraction<S, T> implements InteractionDomain<S, T> {

	private final GameResultEvaluator boardEvaluator;

	private final GenotypePhenotypeMapper<T, OthelloPlayer> opponentMapper;
	private final GenotypePhenotypeMapper<S, OthelloPlayer> playerMapper;

	private final boolean playBoth;
	private final double forceRandomMoveProbability;
	private final List<OthelloState> initialStates;

	/**
	 * @param playerMapper
	 *            responsible to producing (phenotype of) player from its genotype
	 * @param opponentMapper
	 *            responsible to producing (phenotype of) opponent from its genotype
	 * @param playBoth
	 *            if true, play two games with changed colors and return the average score
	 * @param boardEvaluator
	 *            how to count points when the game is finished
	 * @param initialStates
	 * 			  states among which I should start the game. I will select the one by random.
	 */
	@AccessedViaReflection
	public OthelloInteraction(GenotypePhenotypeMapper<S, OthelloPlayer> playerMapper,
			GenotypePhenotypeMapper<T, OthelloPlayer> opponentMapper, boolean playBoth,
			GameResultEvaluator boardEvaluator, double forceRandomMoveProbability, List<OthelloState> initialStates) {
		Preconditions.checkArgument(0.0 <= forceRandomMoveProbability && forceRandomMoveProbability <= 1.0);
		this.playerMapper = playerMapper;
		this.opponentMapper = opponentMapper;
		this.playBoth = playBoth;
		this.boardEvaluator = boardEvaluator;
		this.forceRandomMoveProbability = forceRandomMoveProbability;
		this.initialStates = initialStates;
	}

	@AccessedViaReflection
	public OthelloInteraction(GenotypePhenotypeMapper<S, OthelloPlayer> playerMapper,
			GenotypePhenotypeMapper<T, OthelloPlayer> opponentMapper, boolean playBoth,
			GameResultEvaluator boardEvaluator, double forceRandomMoveProbability) {
		this(playerMapper, opponentMapper, playBoth, boardEvaluator, forceRandomMoveProbability,
			Arrays.asList(new OthelloState()));
	}

	/**
	 * <code>forceRandomMovesProbability</code> defaults to 0.0.
	 *
	 */
	@AccessedViaReflection
	public OthelloInteraction(GenotypePhenotypeMapper<S, OthelloPlayer> playerMapper,
		GenotypePhenotypeMapper<T, OthelloPlayer> opponentMapper, boolean playBoth,
		GameResultEvaluator boardEvaluator) {
		this(playerMapper, opponentMapper, playBoth, boardEvaluator, 0.0);
	}

	/**
	 * <code>boardEvaluator</code> defaults to {@link MorePointsGameResultEvaluator()}.
	 * 
	 */
	@AccessedViaReflection
	public OthelloInteraction(GenotypePhenotypeMapper<S, OthelloPlayer> playerMapper,
			GenotypePhenotypeMapper<T, OthelloPlayer> opponentMapper, boolean playBoth) {
		this(playerMapper, opponentMapper, playBoth, new MorePointsGameResultEvaluator());
	}

	/**
	 * <code>boardEvaluator</code> defaults to {@link MorePointsGameResultEvaluator()}.
	 *
	 */
	@AccessedViaReflection
	public OthelloInteraction(GenotypePhenotypeMapper<S, OthelloPlayer> playerMapper,
		GenotypePhenotypeMapper<T, OthelloPlayer> opponentMapper, boolean playBoth, double forceRandomMoveProbability) {
		this(playerMapper, opponentMapper, playBoth, new MorePointsGameResultEvaluator(), forceRandomMoveProbability);
	}

	@Override
	public InteractionResult interact(S candidate, T test, RandomDataGenerator random) {
		OthelloPlayer player = playerMapper.getPhenotype(candidate, random);
		OthelloPlayer opponent = opponentMapper.getPhenotype(test, random);

		return interact(player, opponent, random);
	}

	// TODO: This is a bit hacky, because it does not use mappers. I need it for OthelloRandomPlayer
	public InteractionResult interact(OthelloPlayer player, OthelloPlayer opponent, RandomDataGenerator random) {
		return playBoth ? playDoubleGame(player, opponent, random) : play(player, opponent, random);
	}

	private InteractionResult playDoubleGame(OthelloPlayer player, OthelloPlayer opponent, RandomDataGenerator random) {
		InteractionResult firstResult = play(player, opponent, random);
		InteractionResult secondResult = play(opponent, player, random);
		return InteractionResult.aggregate(firstResult, secondResult.inverted());
	}

	private InteractionResult play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		Othello othelloGame = new Othello(boardEvaluator, forceRandomMoveProbability, forceRandomMoveProbability);
		OthelloState state = RandomUtils.pickRandom(initialStates, random);
		GameOutcome outcome = othelloGame.play(blackPlayer, whitePlayer, state, random);

		return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("playBoth", playBoth).add("boardEvaluator", boardEvaluator)
			.add("testMapper", opponentMapper).add("candidateMapper", playerMapper).toString();
	}
}
