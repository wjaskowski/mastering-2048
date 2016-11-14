package put.ci.cevo.games.game2048;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import put.ci.cevo.games.board.BoardUtils;

public class HashTiling2048 implements Serializable {
	private final int numTileValues;
	private final int numSegments;
	private final int maxSegment;

	private final Map<Integer, Float> hashMap;
	//private final OptimisticHashMap hashMap;
	private final int[] locations;
	private final int[] straightLocations;

	public int getMaxSegment() {
		return maxSegment;
	}

	public int[] getLocations() {
		return locations;
	}

	public Map<Integer, Float> getHashMap() {
	//public OptimisticHashMap getHashMap() {
		return hashMap;
	}

	private HashTiling2048(int[] locations, Map<Integer, Float> hashMap, int numTileValues, int numSegments, int maxSegment) {
	//private HashTiling2048(int[] locations, OptimisticHashMap hashMap, int numTileValues, int numSegments, int maxSegment) {
		Preconditions.checkArgument(1 <= locations.length);
		Preconditions.checkArgument(1 <= numTileValues);
		Preconditions.checkArgument(0 <= numSegments);

		// I do not copy this arrays, because some of them may be shared among NTuples (symmetry expander)!
		this.locations = locations;
		this.straightLocations = computeStraightLocations(locations);
		this.hashMap = hashMap;
		this.numTileValues = numTileValues;
		this.numSegments = numSegments;
		this.maxSegment = maxSegment;
	}

	public static HashTiling2048 createWithSharedWeights(HashTiling2048 template, int[] locations) {
		return new HashTiling2048(locations, template.hashMap, template.numTileValues, template.numSegments, template.maxSegment);
	}

	private int[] computeStraightLocations(int[] locations) {
		return IntStream.of(locations).map(x -> BoardUtils.marginPosToPos(x, State2048.SIZE)).toArray();
	}

	public double getValue(State2048 state) {
		return hashMap.getOrDefault(address(state), 0.0f);
		//return hashMap.get(address(state));
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

	//HACKy
	private int addressSpecial(State2048 state) {
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

	//HACKy
	public double getValueSpecial(State2048 state) {
		int address = address(state);
		float value = hashMap.getOrDefault(address, 0.0f);
		//float value = hashMap.get(address);
		if (value != 0.0f)
			return value;

		int special = addressSpecial(state);
		if (address == special)
			return 0.0f;

		float newValue = hashMap.getOrDefault(special, 0.0f);
		//float newValue = hashMap.get(special);
		if (newValue != 0.0f) {
			hashMap.put(address, newValue);
		}
		return newValue;
	}

	public void setValue(State2048 state, float value) {
		int address = address(state);
		if (value == 0.0 && !hashMap.containsKey(address))
			return;
		hashMap.put(address, value);
	}

	public void increaseValue(State2048 state, float delta) {
		if (delta == 0.0)
			return;
		int address = address(state);
		hashMap.put(address, hashMap.getOrDefault(address, 0.0f) + delta);
		//hashMap.addTo(address, delta);
	}

	public int getNumWeights() {
		return hashMap.size();
		//return (int)hashMap.getCapacity();
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
		HashTiling2048 that = (HashTiling2048) o;
		return new EqualsBuilder().append(numTileValues, that.numTileValues).append(numSegments, that.numSegments)
								  .append(locations, that.locations).append(hashMap, that.hashMap).isEquals();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(numTileValues, locations, hashMap, numSegments);
	}

	@Override
	public String toString() {
		return "loc=[" + Joiner.on(",").join(ArrayUtils.toObject(locations)) + "]";
	}

	public static HashTiling2048 create(int numSegments, int maxSegment, int[] locations, int numValues,
			long totalCapacity) {
		Preconditions.checkArgument(locations.length > 0);

		Map<Integer, Float> hashMap = new ConcurrentHashMap<>((int)totalCapacity, 0.5f);
		//OptimisticHashMap hashMap = new OptimisticHashMap((int)totalCapacity);

		return new HashTiling2048(locations, hashMap, numValues, numSegments, maxSegment);
	}
}
