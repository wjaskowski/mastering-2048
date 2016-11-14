package put.ci.cevo.util.vectors;

import static put.ci.cevo.util.Pair.create;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.DoubleStream;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import org.apache.commons.math3.util.FastMath;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.math.DistanceMetric;

public final class DoubleVector {

	private final double[] vector;

	public DoubleVector(DoubleVector vector) {
		this(vector.vector);
	}

	public DoubleVector(double[] vector) {
		this.vector = vector;
	}

	/**
	 * Use {@link DoubleVector#zeros}
	 */
	private DoubleVector(int length) {
		this.vector = new double[length];
	}

	public static DoubleVector of(double... arr) {
		return new DoubleVector(arr);
	}

	public static DoubleVector of(Collection<Double> arr) {
		return DoubleVector.of(Doubles.toArray(arr));
	}

	public static DoubleVector mean(List<DoubleVector> points) {
		DoubleVector vector = points.get(0);
		for (int i = 1; i < points.size(); i++) {
			vector = vector.add(points.get(i));
		}
		return vector.divide(points.size());
	}

	public static DoubleVector concat(DoubleVector a, DoubleVector b) {
		return new DoubleVector(org.apache.commons.lang3.ArrayUtils.addAll(a.vector, b.vector));
	}

	public static DoubleVector zeros(int length) {
		return new DoubleVector(length);
	}

	public DoubleVector clone() {
		return new DoubleVector(this.vector.clone());
	}

	public double[] toArray() {
		return vector.clone();
	}

	public int size() {
		return vector.length;
	}

	public double get(int idx) {
		return vector[idx];
	}

	private void set(int idx, double value) {
		vector[idx] = value;
	}

	public final DoubleVector add(DoubleVector v) {
		DoubleVector result = new DoubleVector(v.size());
		for (int i = 0; i < v.size(); i++) {
			result.set(i, get(i) + v.get(i));
		}
		return result;
	}

	public final DoubleVector add(double scalar) {
		DoubleVector v = new DoubleVector(size());
		for (int i = 0; i < size(); i++) {
			v.set(i, get(i) + scalar);
		}
		return v;
	}

	public final DoubleVector subtract(DoubleVector v) {
		DoubleVector result = new DoubleVector(v.size());
		for (int i = 0; i < v.size(); i++) {
			result.set(i, get(i) - v.get(i));
		}
		return result;
	}

	public final DoubleVector subtract(double scalar) {
		DoubleVector v = new DoubleVector(vector.length);
		for (int i = 0; i < vector.length; i++) {
			v.set(i, vector[i] - scalar);
		}
		return v;
	}

	public DoubleVector multiply(DoubleVector v) {
		DoubleVector result = new DoubleVector(size());
		for (int i = 0; i < v.size(); i++) {
			result.set(i, get(i) * v.get(i));
		}
		return result;
	}

	public DoubleVector multiply(double scalar) {
		DoubleVector v = new DoubleVector(size());
		for (int i = 0; i < v.size(); i++) {
			v.set(i, get(i) * scalar);
		}
		return v;
	}

	public DoubleVector divide(DoubleVector v) {
		DoubleVector result = new DoubleVector(size());
		for (int i = 0; i < result.size(); i++) {
			if (v.get(i) != 0) {
				result.set(i, get(i) / v.get(i));
			} else {
				throw new java.lang.ArithmeticException("/ by zero");
			}
		}
		return result;
	}

	public DoubleVector divide(double scalar) {
		if (scalar == 0d) {
			throw new java.lang.ArithmeticException("/ by zero");
		}
		DoubleVector v = new DoubleVector(size());
		for (int i = 0; i < v.size(); i++) {
			v.set(i, get(i) / scalar);
		}
		return v;
	}

	/**
	 * Make the length of the vector 1.0. If norm is <10e-9, will return the original
	 */
	public DoubleVector normalize() {
		double length = getL2Norm();
		if (length < 10e-9)
			return this;
		return divide(length);
	}

	public double sum() {
		double sum = 0.0d;
		for (double elem : vector) {
			sum += elem;
		}
		return sum;
	}

	public DoubleVector abs() {
		DoubleVector v = new DoubleVector(size());
		for (int i = 0; i < v.size(); i++) {
			v.set(i, FastMath.abs(vector[i]));
		}
		return v;
	}

	public double dot(DoubleVector other) {
		double product = 0;
		for (int i = 0; i < size(); i++) {
			product += get(i) * other.get(i);
		}
		return product;
	}

	public Pair<Double, Integer> argmax() {
		double max = Double.NEGATIVE_INFINITY;
		int maxIndex = 0;
		for (int i = 0; i < size(); i++) {
			double d = vector[i];
			if (d > max) {
				max = d;
				maxIndex = i;
			}
		}
		return create(max, maxIndex);
	}

	public double max() {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < size(); i++) {
			double d = vector[i];
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	public Pair<Double, Integer> argmin() {
		double min = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < size(); i++) {
			double d = vector[i];
			if (d < min) {
				min = d;
				minIndex = i;
			}
		}
		return create(min, minIndex);
	}

	public double min() {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < size(); i++) {
			double d = vector[i];
			if (d < min) {
				min = d;
			}
		}
		return min;
	}

	public double getL1Norm() {
		double sum = 0;
		for (double elem : vector) {
			sum += FastMath.abs(elem);
		}
		return sum;
	}

	/** @return length of the vector */
	public double getL2Norm() {
		double sum = 0;
		for (double elem : vector) {
			sum += elem * elem;
		}
		return FastMath.sqrt(sum);
	}

	public double distanceTo(DoubleVector other, DistanceMetric metric) {
		return metric.distance(vector, other.vector);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(vector);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DoubleVector other = (DoubleVector) obj;
		return Arrays.equals(vector, other.vector);
	}

	@Override
	public String toString() {
		return Arrays.toString(vector);
	}

	public DoubleVector slice(int from, int toExclusive) {
		Preconditions.checkArgument(0 <= from && from <= toExclusive && toExclusive <= size());
		return new DoubleVector(Arrays.copyOfRange(vector, from, toExclusive));
	}

	public DoubleStream stream() {
		return DoubleStream.of(vector);
	}

	public DoubleVector sqrt() {
		DoubleVector copy = this.clone();
		for (int i = 0; i < size(); ++i) {
			copy.vector[i] = FastMath.sqrt(copy.vector[i]);
		}
		return copy;
	}
}
