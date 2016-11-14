package put.ci.cevo.experiments.runs.profiles.generic;

import org.apache.commons.math3.distribution.NormalDistribution;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class PerfProfileNormalDistributionDBFactory<T> extends PerfProfileRealDistributionDBFactory<T> {

	@AccessedViaReflection
	public PerfProfileNormalDistributionDBFactory(String dbFile, double mean, double sd, int poolSize) {
		super(new NormalDistribution(mean, sd), poolSize, dbFile);
	}
}
