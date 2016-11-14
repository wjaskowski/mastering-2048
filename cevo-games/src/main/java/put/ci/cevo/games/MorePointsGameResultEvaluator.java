package put.ci.cevo.games;

import static com.google.common.base.Objects.toStringHelper;

import com.google.common.base.Preconditions;

/**
 * Only the number of pieces at the end of the game counts.
 */
public class MorePointsGameResultEvaluator implements GameResultEvaluator {

	private final double pointsForWin;
	private final double pointsForLose;
	private final double pointsForDraw;

	public MorePointsGameResultEvaluator(double pointsForWin, double pointsForLose, double pointsForDraw) {
		Preconditions.checkArgument(pointsForWin >= pointsForDraw && pointsForDraw >= pointsForLose);
		Preconditions.checkArgument(pointsForWin > pointsForLose);
		this.pointsForWin = pointsForWin;
		this.pointsForLose = pointsForLose;
		this.pointsForDraw = pointsForDraw;
	}

	public MorePointsGameResultEvaluator() {
		this(1.0, 0.0, 0.5);
	}

	@Override
	public GameOutcome evaluate(double firstPlayerPoints, double secondPlayerPoints) {
		if (firstPlayerPoints > secondPlayerPoints) {
			return new GameOutcome(pointsForWin, pointsForLose);
		} else if (firstPlayerPoints < secondPlayerPoints) {
			return new GameOutcome(pointsForLose, pointsForWin);
		}
		return new GameOutcome(pointsForDraw, pointsForDraw);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("pointsForWin", pointsForWin).add("pointsForLose", pointsForLose)
			.add("pointsForDraw", pointsForDraw).toString();
	}
}
