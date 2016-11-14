package put.ci.cevo.games.game2048;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryUtils;
import put.ci.cevo.util.RandomUtils;

public class Tiling2048 implements Serializable {
	private final int numTileValues;
	private final int numSegments;
	private final int maxSegment;

	private final float[] LUT;
	private final int[] locations;
	private final int[] straightLocations;

	public int getMaxSegment() {
		return maxSegment;
	}

	public int[] getLocations() {
		return locations;
	}

	public float[] getLUT() {
		return LUT;
	}

	public Tiling2048(int[] locations, float[] weights, int numTileValues, int numSegments, int maxSegment) {
		Preconditions.checkArgument(1 <= locations.length);
		Preconditions.checkArgument(1 <= numTileValues);
		Preconditions.checkArgument(0 <= numSegments);
		Preconditions.checkArgument(weights.length == computeNumWeights(numTileValues, locations.length, numSegments));

		// I do not copy this arrays, because some of them may be shared among NTuples (symmetry expander)!
		this.locations = locations;
		this.straightLocations = computeStraightLocations(locations);
		this.LUT = weights;
		this.numTileValues = numTileValues;
		this.numSegments = numSegments;
		this.maxSegment = maxSegment;
	}

	public static Tiling2048 createWithSharedWeights(Tiling2048 template, int[] locations) {
		return new Tiling2048(locations, template.LUT, template.numTileValues, template.numSegments, template.maxSegment);
	}

	private int[] computeStraightLocations(int[] locations) {
		return IntStream.of(locations).map(x -> BoardUtils.marginPosToPos(x, State2048.SIZE)).toArray();
	}

	public int stageBegins(int stage) {
		int address = stage;
		for (int i = 0; i < straightLocations.length; ++i) {
			address *= numTileValues;
		}
		return address;
	}

	public int address(State2048 state) {
		int[] values = state.getValues();
		int address = 0;
		for (int i = 0; i < numSegments; ++i) {
			address *= 2;
			address += state.hasValue(values, maxSegment - i) ? 1 : 0;
		}
		for (int i = 0; i < straightLocations.length; ++i) {
			address *= numTileValues;
			address += state.getValue(straightLocations[i]);
		}
		return address;
	}

	public static int computeNumWeights(int numTileValues, int numLocations, int numSegments) {
		return (1 << numSegments) * (int) (Math.pow(numTileValues, numLocations) + 0.5);
	}

	public double getValue(State2048 state) {
		return LUT[address(state)];
	}

	//HACK
	public int addressSpecial(State2048 state) {
		int[] values = state.getValues();
		int address = 0;
		for (int i = 0; i < numSegments; ++i) {
			address *= 2;
			address += state.hasValue(values, maxSegment - i) ? 1 : 0;
		}
		if (address > 0)
			address -= 1;
		for (int i = 0; i < straightLocations.length; ++i) {
			address *= numTileValues;
			address += state.getValue(straightLocations[i]);
		}
		return address;
	}

	//HACK
	public double getValueSpecial(State2048 state) {
		int address = address(state);
		float value = LUT[address];
		if (value != 0.0)
			return value;

		int special = addressSpecial(state);
		if (address == special)
			return 0.0f;

		float newValue = LUT[special];
		//HACK
		if (newValue != 0.0) {
			LUT[address] = newValue;
		}
		return newValue;
	}

	//HACK
	public double getValueSuperSpecial(State2048 state) {
		int address = address(state);
		float value = LUT[address];
		if (value != 0.0)
			return value;

		int addressSpecial = addressSpecial(state);
		if (address != addressSpecial) {
			float valueSpecial = LUT[addressSpecial];
			if (valueSpecial != 0.0) {
				LUT[address] = valueSpecial;
				return valueSpecial;
			} else {
				int addressSuperSpecial = addressSuperSpecial(state);
				float valueSuperSpecial = LUT[addressSuperSpecial];
				LUT[address] = valueSuperSpecial;
				return valueSuperSpecial;
			}
		} else {
			int addressSuperSpecial = addressSuperSpecial(state);
			float valueSuperSpecial = LUT[addressSuperSpecial];
			LUT[address] = valueSuperSpecial;
			return valueSuperSpecial;
		}
	}

	private int addressSuperSpecial(State2048 state) {
		int[] values = state.getValues();
		int address = 0;
		for (int i = 0; i < numSegments; ++i) {
			address *= 2;
			address += state.hasValue(values, maxSegment - i) ? 1 : 0;
		}
		if (address > 0)
			address -= 1;
		for (int i = 0; i < straightLocations.length; ++i) {
			address *= numTileValues;
			address += Math.max(0, state.getValue(straightLocations[i]) - 1);
		}
		return address;
	}

	public void setValue(State2048 state, float value) {
		LUT[address(state)] = value;
	}

	public void increaseValue(State2048 state, float delta) {
		if (delta == 0)
			return;
		LUT[address(state)] += delta;
	}

	public int getNumWeights() {
		return LUT.length;
	}

	public int getNumSegments() {
		return numSegments;
	}

	public int getNumTileValues() {
		return numTileValues;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Tiling2048 that = (Tiling2048) o;
		return new EqualsBuilder().append(numTileValues, that.numTileValues).append(numSegments, that.numSegments)
								  .append(maxSegment, that.maxSegment).append(locations, that.locations).append(LUT,
						that.LUT).isEquals();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(numTileValues, locations, LUT, numSegments);
	}

	@Override
	public String toString() {
		return "loc=[" + Joiner.on(",").join(ArrayUtils.toObject(locations)) + "]";
	}

	public static List<Tiling2048> createSymmetric(Tiling2048 template, SymmetryExpander expander) {
		List<int[]> symmetric = SymmetryUtils.createSymmetric(template.locations, expander);

		List<Tiling2048> tilings = new ArrayList<>(symmetric.size());
		for (int[] locations : symmetric) {
			// Watch out: weights are shared among symmetric tilings
			tilings.add(Tiling2048.createWithSharedWeights(template, locations));
		}
		assert tilings.get(0).equals(template);
		return tilings;
	}

	public static Tiling2048 createWithRandomWeights(int numSegments, int maxSegment, int[] locations, int numValues,
			double minWeight, double maxWeight, RandomDataGenerator random) {
		Preconditions.checkArgument(locations.length > 0);
		int len = Tiling2048.computeNumWeights(numValues, locations.length, numSegments);

		float[] weights = RandomUtils.nextFloatVector(len, (float) minWeight, (float) maxWeight, random);

		return new Tiling2048(locations, weights, numValues, numSegments, maxSegment);
	}

	public int getNumLocations() {
		return locations.length;
	}
}
