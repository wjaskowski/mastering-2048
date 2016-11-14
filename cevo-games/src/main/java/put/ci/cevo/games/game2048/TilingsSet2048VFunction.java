package put.ci.cevo.games.game2048;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public class TilingsSet2048VFunction implements LearnableStateValueFunction<State2048> {

	public TilingsSet2048 getTiles() {
		return tiles;
	}

	private final TilingsSet2048 tiles;
	private final boolean special;

	public TilingsSet2048VFunction(TilingsSet2048 tiles, boolean special) {
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
