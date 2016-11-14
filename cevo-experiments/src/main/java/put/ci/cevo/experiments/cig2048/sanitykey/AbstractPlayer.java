package put.ci.cevo.experiments.cig2048.sanitykey;

import java.util.Random;

abstract class AbstractPlayer implements Player {
	protected final Random random;

	public AbstractPlayer(final Random random) {
		this.random = random;
	}
}