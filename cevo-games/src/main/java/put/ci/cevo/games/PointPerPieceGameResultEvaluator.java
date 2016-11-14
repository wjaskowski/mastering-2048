package put.ci.cevo.games;

import static com.google.common.base.Objects.toStringHelper;

public class PointPerPieceGameResultEvaluator implements GameResultEvaluator {

	@Override
	public GameOutcome evaluate(double firstPlayerPoints, double secondPlayerPoints) {
		return new GameOutcome(firstPlayerPoints, secondPlayerPoints);
	}

	@Override
	public String toString() {
		return toStringHelper(this).toString();
	}
}
