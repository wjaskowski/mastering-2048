package put.ci.cevo.experiments.othello;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.board.BoardGame;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Describes interaction of an Othello player against a player and an initial game state
 */
public class OthelloPlayerStateOpponentInteractionDomain implements InteractionDomain<OthelloPlayer, OthelloStateOpponent> {
	private final BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> othello;

	@AccessedViaReflection
	public OthelloPlayerStateOpponentInteractionDomain(BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> othello) {
		this.othello = othello;
	}

	@Override
	public InteractionResult interact(OthelloPlayer player, OthelloStateOpponent stateOpponent,
			RandomDataGenerator random) {
		GameOutcome outcome = othello.play(player, stateOpponent.opponent(), stateOpponent.state(), random);
		return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
