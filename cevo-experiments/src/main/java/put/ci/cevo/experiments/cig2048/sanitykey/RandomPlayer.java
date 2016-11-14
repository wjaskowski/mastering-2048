package put.ci.cevo.experiments.cig2048.sanitykey;

import java.util.Random;

final class RandomPlayer extends AbstractPlayer {
	public RandomPlayer(final Random random) {
		super(random);
	}

	@Override
	public final int getAction() {
		return Game.ACTIONS[random.nextInt(Game.ACTIONS.length)];
	}
}