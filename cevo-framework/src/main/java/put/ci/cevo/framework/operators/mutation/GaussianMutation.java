package put.ci.cevo.framework.operators.mutation;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

import static com.google.common.base.Objects.toStringHelper;

public class GaussianMutation implements MutationOperator<DoubleVector> {

	private static final double DEFAULT_MU = 0.0;
	private static final double DEFAULT_SIGMA = 1.0;
	private static final double DEFAULT_PROBABILITY = 1.0;

	private final double probability;
	private final double mu;
	private final double sigma;

	@AccessedViaReflection
	public GaussianMutation() {
		this(DEFAULT_SIGMA);
	}

	@AccessedViaReflection
	public GaussianMutation(double sigma) {
		this(DEFAULT_PROBABILITY, sigma);
	}

	@AccessedViaReflection
	public GaussianMutation(double probability, double sigma) {
		this(probability, sigma, DEFAULT_MU);
	}

	@AccessedViaReflection
	public GaussianMutation(double probability, double sigma, double mu) {
		Preconditions.checkArgument(0 <= probability && probability <= 1, "Mutation probability must be between [0,1]");
		Preconditions.checkArgument(0 <= sigma);
		this.probability = probability;
		this.mu = mu;
		this.sigma = sigma;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, RandomDataGenerator random) {
		double[] child = individual.toArray();
		for (int i = 0; i < individual.size(); i++) {
			if (random.nextUniform(0, 1) < probability) {
				double noise = random.nextGaussian(mu, sigma);
				child[i] = individual.get(i) + noise;
			}
		}
		return new DoubleVector(child);
	}

	public double getProbability() {
		return probability;
	}

	public double getMu() {
		return mu;
	}

	public double getSigma() {
		return sigma;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("μ", mu).add("σ", sigma).add("p", probability).toString();
	}
}
