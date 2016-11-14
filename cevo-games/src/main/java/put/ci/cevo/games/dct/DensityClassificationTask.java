package put.ci.cevo.games.dct;

import java.util.Arrays;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;

public class DensityClassificationTask implements Game<CARule, CAConfiguration> {

	private final int timeSteps;
	private final int radius;

	public DensityClassificationTask(int timeSteps, int radius) {
		this.timeSteps = timeSteps;
		this.radius = radius;
	}

	@Override
	public GameOutcome play(CARule rule, CAConfiguration test, RandomDataGenerator random) {
		int[] ca = test.toArray();
		int[] newca = new int[ca.length];

		final int target = test.getTarget();
		final int size = ca.length;

		// check for convergence
		if (target == 0 && rule.getFirst() != 0) {
			return new GameOutcome(0, 1);
		}
		if (target == 1 && rule.getLast() != 1) {
			return new GameOutcome(0, 1);
		}

		for (int time = 0; time < timeSteps; time++) {
			int cnt = 0;

			// Init moving window
			int num = 0;
			int exp = 2 * radius;
			for (int j = 0; j < 2 * radius + 1; ++j) {
				num += (1 << exp) * ca[j];
				exp -= 1;
			}

			newca[0] = rule.get(num);
			if (newca[0] == target) {
				cnt += 1;
			}

			// Move window
			int end = 2 * radius;
			for (int i = 1; i < size; ++i) {
				end += 1;
				if (end >= size) {
					end -= size;
				}
				num = ((num << 1) & ((1 << 2 * radius + 1) - 1)) ^ ca[end];

				newca[i] = rule.get(num);
				if (newca[i] == target) {
					cnt += 1;
				}
			}
			if (cnt == size) {
				return new GameOutcome(1, 0);
			}
			ca = Arrays.copyOf(newca, newca.length);
		}
		return new GameOutcome(0, 1);
	}

}
