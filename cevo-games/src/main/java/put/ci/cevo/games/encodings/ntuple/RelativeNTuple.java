package put.ci.cevo.games.encodings.ntuple;

import java.io.Serializable;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.util.RandomUtils;

public class RelativeNTuple implements Serializable {

	private static final long serialVersionUID = -7031227015558262219L;

	private final int numValues;
	private final int[] locations;
	private final float[] LUT;

	/**
	 * Watch out: Locations are margin-based (not 0-based)
	 */
	public RelativeNTuple(int numValues, int[] locations, float[] weights) {
		Preconditions.checkArgument(locations.length > 0);
		Preconditions.checkArgument(weights.length == computeNumWeights(numValues, locations.length));
		// I do not copy this arrays, because some of them may be shared among NTuples (symmetry expander)!
		this.numValues = numValues;
		this.locations = locations;
		this.LUT = weights;
	}

	public static RelativeNTuple newWithSharedWeights(RelativeNTuple template, int[] locations) {
		return new RelativeNTuple(template.getNumValues(), locations, template.getWeights());
	}

	public static RelativeNTuple newWithRandomWeights(int numValues, int[] locations, double minWeight, double maxWeight,
			RandomDataGenerator random) {
		Preconditions.checkArgument(locations.length > 0);
		int len = RelativeNTuple.computeNumWeights(numValues, locations.length);

		float[] weights = RandomUtils.nextFloatVector(len, (float)minWeight, (float)maxWeight, random);

		return new RelativeNTuple(numValues, locations, weights);
	}

	/**
	 * Copy constructor
	 */
	public RelativeNTuple(RelativeNTuple template) {
		this(template.numValues, template.locations.clone(), template.LUT.clone());
	}

	public double getValue(Board board) {
		return LUT[address(board)];
	}

	public int address(Board board) {
		int address = 0;
		int lastvalue = -1;
		for (int location : locations) {
			address *= numValues;
			// Here we assume that board values are in [0, numValues) range
			int value = board.getValue(location);
			if (lastvalue == -1) lastvalue = value;
			//TODO: A bit of hackery
			int addition = numValues/2 + value - lastvalue;
			if (addition >= numValues)
				addition = numValues - 1;
			else if (addition < 0)
				addition = 0;
			address += addition;
			lastvalue = value;
		}
		return address;
	}

	/**
	 * @return actual values (e.g. [0, 1, 2]) for a given address (e.g. 5). Could be static in principle (TODO)
	 */
	public int[] valuesFromAddress(int address) {
		int[] val = new int[getSize()];
		for (int i = getSize() - 1; i >= 0; --i) {
			val[i] = address % numValues;
			address = address / numValues;
		}
		return val;
	}

	/**
	 * weights are not cloned, because I use this method for sharing weights among ntuples. Right, this could be done
	 * better (TODO)
	 */
	public float[] getWeights() {
		return LUT;
	}

	public int getNumWeights() {
		return LUT.length;
	}

	public int[] getLocations() {
		return locations.clone();
	}

	public int getSize() {
		return locations.length;
	}

	public int getNumValues() {
		return numValues;
	}

	public static int computeNumWeights(int numValues, int numFields) {
		return (int) (Math.pow(numValues, numFields) + 0.5);
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
		RelativeNTuple other = (RelativeNTuple) obj;
		return new EqualsBuilder().append(locations, other.locations).append(LUT, other.LUT)
			.append(numValues, other.numValues).isEquals();
	}

	@Override
	public String toString() {
		return "loc=[" + Joiner.on(",").join(ArrayUtils.toObject(locations)) + "]";
	}

	public String toStringDetailed() {
		String s = "loc=[" + Joiner.on(",").join(ArrayUtils.toObject(locations)) + "]\n";
		for (int i = 0; i < getNumWeights(); ++i) {
			int[] values = valuesFromAddress(i);
			s += " " + Joiner.on(",").join(ArrayUtils.toObject(values)) + ":\t" + String.format("%5.1f", LUT[i]) + "\n";
		}
		return s;
	}

	public void increaseValue(Board board, float delta) {
		LUT[address(board)] += delta;
	}

	public void setValue(Board board, float value) {
		LUT[address(board)] = value;
	}
}
