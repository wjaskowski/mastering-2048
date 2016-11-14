package put.ci.cevo.games.tetris;

import com.carrotsearch.hppc.DoubleArrayList;
import put.ci.cevo.rl.environment.FeaturesExtractor;

/**
 * A standard set of 21 Tetris features (Bertsekas & Ioffe, 1996)
 */
public class BertsekasIoffeTetrisFeaturesExtractor implements FeaturesExtractor<TetrisState> {

	public static final int FEATURES_COUNT = 22;

	@Override
	public double[] getFeatures(TetrisState state) {
		DoubleArrayList features = new DoubleArrayList(FEATURES_COUNT);

		TetrisBoard board = state.getBoard();

		// Column heights
		for (int c = 0; c < board.getWidth(); ++c) {
			features.add(board.getSkyline()[c]);
		}

		// Column height differences (may be negative)
		for (int c = 1; c < board.getWidth(); ++c) {
			features.add(Math.abs(board.getSkyline()[c] - board.getSkyline()[c - 1]));
		}

		features.add(board.getHeight() - board.getMaxColumnHeight());

		features.add(board.getHolesCount());

		features.add(1); // Bias (useful for TDL)

		assert features.size() == featuresCount();

		return features.toArray();
	}

	@Override
	public int featuresCount() {
		return FEATURES_COUNT;
	}
}
