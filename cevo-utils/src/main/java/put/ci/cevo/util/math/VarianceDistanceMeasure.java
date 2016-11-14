package put.ci.cevo.util.math;

import org.apache.commons.math3.util.MathArrays;

import static java.lang.Math.sqrt;
import static org.apache.commons.math3.stat.StatUtils.variance;

public class VarianceDistanceMeasure implements DistanceMetric {

	@Override
	public double distance(double[] a, double[] b) {
		return MathArrays.distance(a, b) / (1 + sqrt(variance(b)));
	}

}