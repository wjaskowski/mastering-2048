package put.ci.cevo.games.game2048;

import org.apache.commons.lang.NotImplementedException;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public class HashTilingsSet2048VFunction implements LearnableStateValueFunction<State2048> {

	public HashTilingsSet2048 getTiles() {
		return tiles;
	}

	private final HashTilingsSet2048 tiles;
	private final boolean special;

	public HashTilingsSet2048VFunction(HashTilingsSet2048 tiles, boolean special) {
		this.tiles = tiles;
		this.special = special;
	}

	@Override
	public double getValue(State2048 state) {
		double value = 0;
		if (special) {
			for (int i = 0; i < getActiveFeaturesCount(); ++i) {
				value += tiles.getTiling(i).getValueSpecial(state);
			}
		} else {
			for (int i = 0; i < getActiveFeaturesCount(); ++i) {
				value += tiles.getTiling(i).getValue(state);
			}
		}
		return value;
	}

	@Override
	public void increase(State2048 state, double delta) {
		throw new NotImplementedException();
	}

	@Override
	public int getActiveFeaturesCount() {
		return tiles.getNumAllTilings();
	}

	@Override
	public double getActiveWeight(State2048 state, int idx) {
		return tiles.getTiling(idx).getValue(state);
	}

	@Override
	public void setActiveWeight(State2048 state, int idx, double value) {
		tiles.getTiling(idx).setValue(state, (float) value);
	}

	@Override
	public void increaseActiveWeight(State2048 state, int idx, double delta) {
		tiles.getTiling(idx).increaseValue(state, (float) delta);
	}
}
