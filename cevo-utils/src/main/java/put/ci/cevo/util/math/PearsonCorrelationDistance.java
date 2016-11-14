package put.ci.cevo.util.math;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class PearsonCorrelationDistance implements DistanceMetric {

	private static final PearsonsCorrelation pc = new PearsonsCorrelation();

	@Override
	public double distance(double[] a, double[] b) {
		return 1 - pc.correlation(a, b);
	}

}