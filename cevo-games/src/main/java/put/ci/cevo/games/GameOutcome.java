package put.ci.cevo.games;

import java.util.Objects;

//TODO: Cosider merging it with InteractionResult. This way I will get effort here and could compute effort in
//games instead of interactions (like in case of double-game interaction). [Actually I am not sure whether it should
//or should not be like this]
public class GameOutcome {

	public double playerPoints() {
		return playerPoints;
	}

	public double opponentPoints() {
		return opponentPoints;
	}

	private final double playerPoints;
	private final double opponentPoints;

	public GameOutcome(double playerPoints, double opponentPoints) {
		this.playerPoints = playerPoints;
		this.opponentPoints = opponentPoints;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GameOutcome o = (GameOutcome) obj;
		return playerPoints == o.playerPoints && opponentPoints == o.opponentPoints;
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerPoints, opponentPoints);
	}

	@Override
	public String toString() {
		return "o(" + playerPoints + "," + opponentPoints + ")";
	}
}
