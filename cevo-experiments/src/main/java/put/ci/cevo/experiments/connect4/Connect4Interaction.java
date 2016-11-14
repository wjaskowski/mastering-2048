package put.ci.cevo.experiments.connect4;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.connect4.Connect4;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.thill.c4.ConnectFour;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Uses {@link Connect4} game implementation. Lightweight and typically much faster than Thill's {@link ConnectFour}.
 */
public class Connect4Interaction implements InteractionDomain<Connect4Player, Connect4Player> {

	private final boolean playBoth;

	@AccessedViaReflection
	public Connect4Interaction() {
		this(true);
	}

	@AccessedViaReflection
	public Connect4Interaction(boolean playBoth) {
		this.playBoth = playBoth;
	}

	@Override
	public InteractionResult interact(Connect4Player candidate, Connect4Player test, RandomDataGenerator random) {
		return playBoth ? playDoubleGame(candidate, test, random) : play(candidate, test, random);
	}

	private InteractionResult playDoubleGame(Connect4Player player, Connect4Player opponent, RandomDataGenerator random) {
		InteractionResult firstResult = play(player, opponent, random);
		InteractionResult secondResult = play(opponent, player, random);
		return InteractionResult.aggregate(firstResult, secondResult.inverted());
	}

	private InteractionResult play(Connect4Player blackPlayer, Connect4Player whitePlayer, RandomDataGenerator random) {
		Game<Connect4Player, Connect4Player> game = new Connect4();
		GameOutcome outcome = game.play(blackPlayer, whitePlayer, random);

		return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("playBoth", playBoth).toString();
	}

}
