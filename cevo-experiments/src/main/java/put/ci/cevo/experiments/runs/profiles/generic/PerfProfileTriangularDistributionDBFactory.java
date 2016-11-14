package put.ci.cevo.experiments.runs.profiles.generic;

import org.apache.commons.math3.distribution.TriangularDistribution;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class PerfProfileTriangularDistributionDBFactory<S> extends PerfProfileRealDistributionDBFactory<S> {

	@AccessedViaReflection
	public PerfProfileTriangularDistributionDBFactory(double a, double b, double c, int poolSize, String dbFile) {
		super(new TriangularDistribution(a, c, b), poolSize, dbFile);
	}

}
