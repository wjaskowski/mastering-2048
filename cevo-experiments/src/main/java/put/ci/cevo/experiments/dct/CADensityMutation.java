package put.ci.cevo.experiments.dct;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.games.dct.CADensity;

public class CADensityMutation implements MutationOperator<CADensity> {

	private final double mu;
	private final double sigma;

	public CADensityMutation(double sigma) {
		this(0, sigma);
	}

	public CADensityMutation(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
	}

	@Override
	public CADensity produce(CADensity individual, RandomDataGenerator random) {
		double density = individual.getDensity() + random.nextGaussian(mu, sigma);
		if (density > 1) {
			density = 1;
		} else if (density < 0) {
			density = 0;
		}
		return new CADensity(density, individual.getTestLength());
	}
}
