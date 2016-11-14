package put.ci.cevo.experiments.othello;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.board.BoardGame;
import put.ci.cevo.games.GameState;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Interaction on all given initial states. The interaction result is an average on all initial states.
 */
public class AllStatesInteractionDomain<X, U extends GameState> implements InteractionDomain<X, X> {
	private final BoardGame<X, X, U> game;
	private final List<U> initialStates;

	/**
	 * @param initialStates states from which the initial state will be drawn from
	 */
	@AccessedViaReflection
	public AllStatesInteractionDomain(BoardGame<X, X, U> game, List<U> initialStates) {
		this.game = game;
		this.initialStates = initialStates;
	}

	@Override
	public InteractionResult interact(X player, X opponent, RandomDataGenerator random) {
		InteractionResult result = new InteractionResult();
		for (U state : initialStates) {
			GameOutcome outcome = game.play(player, opponent, state, random);
			result = result.add(new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1));
		}
		return result.divide(initialStates.size());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
