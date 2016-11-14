package put.ci.cevo.games.game2048;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Game2048Outcome {
	private final int score;
	private final State2048 lastState;

	public Game2048Outcome(int score, State2048 lastState) {
		this.score = score;
		this.lastState = lastState;
	}

	public int score() {
		return score;
	}

	public State2048 getLastState() {
		return lastState;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
