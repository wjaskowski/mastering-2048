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
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Describes interaction for the game of Othello. Players have to be OthelloPlayers
 * //TODO: Could be generalized as AllStatesInteractionDomain
 */
public class OthelloInteractionDomain implements InteractionDomain<OthelloPlayer, OthelloPlayer> {

	private final BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> othello;
	private final List<OthelloState> initialStates;

	@AccessedViaReflection
	public OthelloInteractionDomain(BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> othello) {
		this(othello, singletonList(new OthelloState()));
	}

	/**
	 * @param initialStates states from which the initial state will be drawn from
	 */
	@AccessedViaReflection
	public OthelloInteractionDomain(BoardGame<OthelloPlayer, OthelloPlayer, OthelloState> othello,
			List<OthelloState> initialStates) {
		this.othello = othello;
		this.initialStates = initialStates;
	}

	@Override
	public InteractionResult interact(OthelloPlayer player, OthelloPlayer opponent, RandomDataGenerator random) {
		OthelloState initialState = RandomUtils.pickRandom(initialStates, random);
		GameOutcome outcome = othello.play(player, opponent, initialState, random);
		return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
