package put.ci.cevo.games.number;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;

public class CompareOnAll implements Game<NumbersGamePlayer, NumbersGamePlayer> {

	@Override
	public GameOutcome play(NumbersGamePlayer solution, NumbersGamePlayer test, RandomDataGenerator random) {
		for (int i = 0; i < solution.getStrategyLength(); i++) {
			if (solution.get(i) >= test.get(i)) {
				continue;
			} else if (solution.get(i) < test.get(i)) {
				return new GameOutcome(0, 1);
			}
		}
		return new GameOutcome(1, 0);
	}

}
