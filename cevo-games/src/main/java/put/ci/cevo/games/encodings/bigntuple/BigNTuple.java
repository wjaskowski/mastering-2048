package put.ci.cevo.games.encodings.bigntuple;

import java.io.Serializable;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.FloatBigArrays;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.util.RandomUtils;

public class BigNTuple implements Serializable {

	private final int numValues;
	private final int[] locations;
	private final float[][] LUT;

	/**
	 * Watch out: Locations are margin-based (not 0-based)
	 */
	public BigNTuple(int numValues, int[] locations, float[][] weights) {
		Preconditions.checkArgument(locations.length > 0);
		Preconditions.checkArgument(FloatBigArrays.length(weights) == computeNumWeights(numValues, locations.length));
		// I do not copy this arrays, because some of them may be shared among NTuples (symmetry expander)!
		this.numValues = numValues;
		this.locations = locations;
		this.LUT = weights;
	}

	public static BigNTuple create(int numValues, int[] locations) {
		Preconditions.checkArgument(locations.length > 0);
		long len = BigNTuple.computeNumWeights(numValues, locations.length);

		return new BigNTuple(numValues, locations, FloatBigArrays.newBigArray(len));
	}

	public static BigNTuple createWithRandomWeights(int numValues, int[] locations, double minWeight,
			double maxWeight, RandomDataGenerator random) {
		long len = BigNTuple.computeNumWeights(numValues, locations.length);
		BigNTuple bigNTuple = new BigNTuple(numValues, locations, FloatBigArrays.newBigArray(len));

		for (int s = bigNTuple.LUT.length; s-- != 0; ) {
			final float[] t = bigNTuple.LUT[s];
			for (int d = t.length; d-- != 0; )
				t[d] = (float) RandomUtils.nextUniform(minWeight, maxWeight, random);
		}

		return bigNTuple;
	}

	public float valueFor(Board board) {
		return FloatBigArrays.get(LUT, address(board));
	}

	public float get(long address) {
		return FloatBigArrays.get(LUT, address);
	}

	public void add(long address, float value) {
		FloatBigArrays.add(LUT, address, value);
	}

	public long address(Board board) {
		long address = 0;
		for (int location : locations) {
			address *= numValues;
			// Here we assume that board values are in [0, numValues) range
			address += board.getValue(location);
		}
		return address;
	}

	/**
	 * @return actual values (e.g. [0, 1, 2]) for a given address (e.g. 5). Could be static in principle (TODO)
	 */
	public static int[] valuesFromAddress(long address, int tupleLength, int numValues) {
		int[] val = new int[tupleLength];
		for (int i = tupleLength - 1; i >= 0; --i) {
			val[i] = (int) (address % numValues);
			address = address / numValues;
		}
		return val;
	}

	public long getNumWeights() {
		return FloatBigArrays.length(LUT);
	}

	public int[] getLocations() {
		return locations.clone();
	}

	public float[][] getWeights() {
		return LUT;
	}

	public int getSize() {
		return locations.length;
	}

	public int getNumValues() {
		return numValues;
	}

	public static long computeNumWeights(int numValues, int numFields) {
		return (long) (Math.pow(numValues, numFields) + 0.5);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(locations).append(LUT).append(numValues).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BigNTuple other = (BigNTuple) obj;
		return new EqualsBuilder().append(locations, other.locations).append(LUT, other.LUT)
								  .append(numValues, other.numValues).isEquals();
	}

	@Override
	public String toString() {
		return "loc=[" + Joiner.on(",").join(ArrayUtils.toObject(locations)) + "]";
	}

	public String toStringDetailed() {
		String s = "loc=[" + Joiner.on(",").join(ArrayUtils.toObject(locations)) + "]\n";
		for (long i = 0; i < getNumWeights(); ++i) {
			int[] values = valuesFromAddress(i, getSize(), getNumValues());
			s += " " + Joiner.on(",").join(ArrayUtils.toObject(values)) + ":\t" + String.format("%5.1f",
					FloatBigArrays.get(LUT, i)) + "\n";
		}
		return s;
	}

}
