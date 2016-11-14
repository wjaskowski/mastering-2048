package put.ci.cevo.games.connect4.thill.c4;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.random.RandomDataGenerator;

public abstract class NotImplementedFakeAgent implements Agent {

	@Override
	public int getBestMove(int player, int[] colHeight, double[] vTable, RandomDataGenerator random) {
		throw new NotImplementedException();
	}

	@Override
	public double getScore(int[][] table, boolean useSigmoid) {
		throw new NotImplementedException();
	}

	@Override
	public double[] getNextVTable(int[][] table, boolean useSigmoid) {
		throw new NotImplementedException();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void semOpDown() {
		throw new NotImplementedException();
	}

	@Override
	public void semOpUp() {
		throw new NotImplementedException();
	}
}
