package put.ci.cevo.games.number;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;

public class CompareOnOne implements Game<NumbersGamePlayer, NumbersGamePlayer> {

	@Override
	public GameOutcome play(NumbersGamePlayer solution, NumbersGamePlayer test, RandomDataGenerator random) {
		int maxDim = 0;
		double maxVal = test.get(maxDim);
		for (int i = 1; i < solution.getStrategyLength(); ++i) {
			double v = test.get(i);
			if (maxVal < v) {
				maxVal = v;
				maxDim = i;
			}
		}
		return solution.get(maxDim) >= maxVal ? new GameOutcome(1, 0) : new GameOutcome(0, 1);
	}

}