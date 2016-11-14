package put.ci.cevo.experiments.connect4;

import static com.google.common.base.Objects.toStringHelper;
import static put.ci.cevo.games.connect4.players.Connect4PerfectPlayer.Randomization.RANDOMIZE_EQUAL_MOVES;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.connect4.thill.c4.Agent;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
import put.ci.cevo.games.connect4.thill.c4.ConnectFour;
import put.ci.cevo.games.connect4.thill.c4.competition.Competition;
import put.ci.cevo.games.connect4.thill.c4.competition.ResultCompSingle;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Uses Thill's {@link ConnectFour} game implementation to perform interactions between agents.
 */
public class Connect4AgentInteraction implements InteractionDomain<Agent, Agent> {

	private static final AlphaBetaAgent ab = AlphaBetaAgent.createAgent(RANDOMIZE_EQUAL_MOVES);

	private final boolean playBoth;

	@AccessedViaReflection
	public Connect4AgentInteraction() {
		this(true);
	}

	@AccessedViaReflection
	public Connect4AgentInteraction(boolean playBoth) {
		this.playBoth = playBoth;
	}

	@Override
	public InteractionResult interact(Agent candidate, Agent test, RandomDataGenerator random) {
		return playBoth ? playDoubleGame(candidate, test, random) : play(candidate, test, random);
	}

	private InteractionResult playDoubleGame(Agent player, Agent opponent, RandomDataGenerator random) {
		InteractionResult firstResult = play(player, opponent, random);
		InteractionResult secondResult = play(opponent, player, random);
		return InteractionResult.aggregate(firstResult, secondResult.inverted());
	}

	private InteractionResult play(Agent blackPlayer, Agent whitePlayer, RandomDataGenerator random) {
		Competition competition = new Competition(blackPlayer, whitePlayer, ab);
		ResultCompSingle scr = competition.compete(42, false, false, random);
		return getInteractionResult(scr);
	}

	private InteractionResult getInteractionResult(ResultCompSingle scr) {
		switch (scr.winner) {
		case -1:
			return new InteractionResult(0.5, 0.5, 1);
		case 0:
			return new InteractionResult(1, 0, 1);
		case 1:
			return new InteractionResult(0, 1, 1);
		default:
			throw new RuntimeException("Invalid game outcome: " + scr);
		}
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("playBoth", playBoth).toString();
	}

}
