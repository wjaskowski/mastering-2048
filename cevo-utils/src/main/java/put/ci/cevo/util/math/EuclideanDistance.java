package put.ci.cevo.util.math;

import org.apache.commons.math3.util.MathArrays;

public class EuclideanDistance implements DistanceMetric {

	@Override
	public double distance(double[] a, double[] b) {
		return MathArrays.distance(a, b);
	}

}
