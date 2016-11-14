package put.ci.cevo.framework.algorithms;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;

//TODO: Improve performance by:
// 1. using combineToSelf (and other methods which reduce the need for creating new vectors
// 2. some vectors should not be created dynamically

/**
 * An implementation of VD-CMA-ES translated to Java from Shark library (http://image.diku.dk/shark/).
 * (original code from r3370 2014-10-18)
 *
 * The order of some operations has been changed so I do not guarantee the same numerical accuracy
 * as the original code.
 *
 * For more reference, see the paper
 * Akimoto, Y., A. Auger, and N. Hansen (2014). Comparison-Based Natural Gradient Optimization in High Dimension.
 * Genetic and Evolutionary Computation Conference (GECCO 2014), Proceedings, ACM
 */
class SharkVDCMAESInternal {
	final private int m_numberOfVariables; // Stores the dimensionality of the search space.
	final private int m_mu;                // The size of the parent population.
	final private int m_lambda;            // The size of the offspring population, needs to be larger than mu.

	final private double m_cC;
	final private double m_c1;
	final private double m_cMu;
	final private double m_cSigma;
	final private double m_dSigma;
	final private double m_muEff;

	private double m_sigma;

	private RealVector m_mean;
	private RealVector m_weights;

	private RealVector m_best_point;

	private RealVector m_evolutionPathC;
	private RealVector m_evolutionPathSigma;

	// normalised vector v
	private RealVector m_vn;
	// norm of the vector v, therefore  v=m_vn*m_normv
	private double m_normv;

	private RealVector m_D;

	private int m_counter; // counter for generations
	private Logger logger = Logger.getLogger(SharkVDCMAESInternal.class);

	static class Individual {
		private RealVector m_chromosome;
		private RealVector m_searchPoint;
		private double m_fitness;

		//TODO: chromosome is actually not chromosome
		public Individual(RealVector searchPoint, RealVector chromosome) {
			this.m_searchPoint = searchPoint;
			this.m_chromosome = chromosome;
		}

		public RealVector chromosome() {
			return m_chromosome;
		}

		public RealVector searchPoint() {
			return m_searchPoint;
		}

		public void setFitness(double fitness) {
			m_fitness = fitness;
		}

		public double fitness() {
			return m_fitness;
		}
	}

	static private double chi(int n) {
		return (sqrt(n) * (1. - 1. / (4. * n) + 1. / (21. * n * n)));
	}

	// Calculates lambda for the supplied dimensionality n.
	static int suggestLambda(int dimension) {
		return 4 + (int) floor(3 * log(dimension)); // eq. (44)
	}

	// Calculates mu for the supplied lambda and the recombination strategy.
	static int suggestMu(int lambda) {
		return lambda / 2; // eq. (44)
	}

	static double suggestInitialSigma(int dim) {
		return 1.0 / sqrt(dim);
	}

	/**
	 * Uses suggested values for all parameters
	 */
	public SharkVDCMAESInternal(double[] initialSearchPoint, RandomDataGenerator random) {
		this(
				initialSearchPoint,
				suggestLambda(initialSearchPoint.length),
				suggestMu(
						suggestLambda(initialSearchPoint.length)),
				suggestInitialSigma(initialSearchPoint.length), random);
	}

	// Initializes the algorithm
	public SharkVDCMAESInternal(double[] initialSearchPoint, int lambda, int mu, double initialSigma,
			RandomDataGenerator random) {

		m_numberOfVariables = initialSearchPoint.length;
		m_lambda = lambda;
		m_mu = mu;
		m_sigma = initialSigma;

		m_mean = new ArrayRealVector(m_numberOfVariables, 0.0);
		m_vn = new ArrayRealVector(m_numberOfVariables);
		for (int i = 0; i < m_numberOfVariables; ++i){
			m_vn.setEntry(i, random.nextUniform(0, 1.0 / m_numberOfVariables, true));
		}
		m_normv = m_vn.getNorm();
		m_vn.mapDivideToSelf(m_normv); // m_vn /= m_normv;

		m_D = new ArrayRealVector(m_numberOfVariables, 1.0);
		m_evolutionPathC = new ArrayRealVector(m_numberOfVariables, 0.0);
		m_evolutionPathSigma = new ArrayRealVector(m_numberOfVariables, 0.0);

		//set initial point
		m_mean = new ArrayRealVector(initialSearchPoint);
		m_best_point = new ArrayRealVector(initialSearchPoint);

		m_counter = 0;//first iteration

		//weighting of the mu-best individuals
		m_weights = new ArrayRealVector(m_mu);
		for (int i = 0; i < m_mu; i++){
			m_weights.setEntry(i, log(mu + 0.5) - log(1. + i));
		}

		m_weights.mapDivideToSelf(sum(m_weights)); // m_weights /= sum(m_weights);

		// constants based on (4) and Step 3 in the algorithm
		m_muEff = 1. / sum(sqr(m_weights));
		m_cSigma = 0.5 / (1 + sqrt(m_numberOfVariables / m_muEff));
		m_dSigma = 1. + 2. * max(0., sqrt((m_muEff - 1.) / (m_numberOfVariables + 1)) - 1.) + m_cSigma;

		m_cC = (4. + m_muEff / m_numberOfVariables) / (m_numberOfVariables + 4. + 2 * m_muEff / m_numberOfVariables);
		double correction = (m_numberOfVariables - 5.0) / 6.0;
		m_c1 = correction * 2 / (sqr(m_numberOfVariables + 1.3) + m_muEff);
		m_cMu = min(1. - m_c1, correction * 2 * (m_muEff - 2. + 1. / m_muEff) / (sqr(m_numberOfVariables + 2)
				+ m_muEff));
	}

	// Executes one iteration of the algorithm.

	/**
	 * @param populationEvaluator the lower fitness the better individual
	 */
	public void step(Function<List<double[]>, List<Double>> populationEvaluator, RandomDataGenerator random) {
		List<Individual> offspring = createSamples(random, m_lambda);

		// For saving space consider using ArrayRealVector.toDataRef()
		List<double[]> raw = offspring.stream()
									  .map(ind -> ind.searchPoint().toArray())
									  .collect(Collectors.toList());
		List<Double> fitness = populationEvaluator.apply(raw);

		nextGeneration(offspring, fitness);
	}

	public List<Individual> createSamples(RandomDataGenerator random, int count) {
		List<Individual> offspring = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			offspring.add(createSample(random));
		}
		return offspring;
	}

	public void nextGeneration(List<Individual> offspring, List<Double> fitness) {
		for (int i = 0; i < m_lambda; ++i)
			offspring.get(i).setFitness(fitness.get(i));

		// "Lower" fitness is better
		offspring.sort((x, y) -> Double.compare(x.fitness(), y.fitness()));

		// Selection
		List<Individual> parents = offspring.subList(0, m_mu);

		// Strategy parameter update
		m_counter++; // increase generation counter
		updateStrategyParameters(parents);

		m_best_point = parents.get(0).searchPoint();
	}

	public double[] currentBest() {
		return m_best_point.toArray();
	}

	/**
	 * Accesses the current step size.
	 */
	public double getSigma() {
		return m_sigma;
	}

	/**
	 * Accesses the current population mean.
	 */
	public RealVector mean() {
		return m_mean;
	}

	/**
	 * Accesses the current weighting vector.
	 */
	public RealVector weights() {
		return m_weights;
	}

	/**
	 * Accesses the evolution path for the covariance matrix update.
	 */
	public RealVector evolutionPath() {
		return m_evolutionPathC;
	}

	/**
	 * Accesses the evolution path for the step size update.
	 */
	public RealVector evolutionPathSigma() {
		return m_evolutionPathSigma;
	}

	/** Returns the size of the parent population \f$\mu\f$. */
	public int mu() {
		return m_mu;
	}

	/** Returns the size of the offspring population \f$\mu\f$. */
	public int lambda() {
		return m_lambda;
	}

	/** Updates the strategy parameters based on the supplied offspring population.
	 *  The chromosome stores the y-vector that is the step from the mean in D=1, sigma=1 space.
	 */
	private void updateStrategyParameters(List<Individual> offspring) {
		RealVector m = new ArrayRealVector(m_numberOfVariables, 0.0);
		RealVector z = new ArrayRealVector(m_numberOfVariables, 0.0);

		for (int j = 0; j<offspring.size(); j++) {
			addInPlace(m, offspring.get(j).searchPoint().mapMultiply(m_weights.getEntry(j)));
			addInPlace(z, offspring.get(j).chromosome().mapMultiply(m_weights.getEntry(j)));
		}
		// compute z from y = (1+(sqrt(1+||v||^2)-1)v_n v_n^T)z
		// therefore z = (1+(1/sqrt(1+||v||^2)-1)v_n v_n^T)y
		double b = (1 / sqrt(1 + sqr(m_normv)) - 1);
		z = z.add(m_vn.mapMultiply(b * z.dotProduct(m_vn)));

		// update paths (
		//   m_evolutionPathSigma = (1. - m_cSigma)*m_evolutionPathSigma +
		//                          sqrt( m_cSigma * (2. - m_cSigma) * m_muEff ) * z );

		m_evolutionPathSigma = m_evolutionPathSigma.mapMultiply(1 - m_cSigma).add(
				z.mapMultiply(sqrt(m_cSigma * (2 - m_cSigma) * m_muEff)));

		// compute h_sigma
		double hSigLHS = (m_evolutionPathSigma.getNorm()) / sqrt(1. - pow((1 - m_cSigma), 2. * (m_counter + 1)));
		double hSigRHS = (1.4 + 2 / (m_numberOfVariables + 1.)) * chi(m_numberOfVariables);
		double hSig = 0;
		if (hSigLHS < hSigRHS)
			hSig = 1.;
		// m_evolutionPathC = m_evolutionPathC * (1. - m_cC )
		//                  + (m - m_mean) / m_sigma * hSig * sqrt( m_cC * (2. - m_cC) * m_muEff );
		m_evolutionPathC = m_evolutionPathC.mapMultiply(1. - m_cC)
										   .add(m.subtract(m_mean).mapDivide(m_sigma).mapMultiply(hSig * sqrt(
												   m_cC * (2. - m_cC) * m_muEff)));

		// we split the computation of s and t in the paper in two parts
		// we compute the first two steps and then compute the weighted mean over samples and
		// evolution path. afterwards we compute the rest using the mean result
		// the paper describes this as first computing S and T for all samples and compute the weighted
		// mean of that, but the reference implementation does it the other way to prevent numerical instabilities
		RealVector meanS = new ArrayRealVector(m_numberOfVariables, 0.0);
		RealVector meanT = new ArrayRealVector(m_numberOfVariables, 0.0);
		for (int j = 0; j < mu(); ++j) {
			computeSAndTFirst(offspring.get(j).chromosome(), meanS, meanT, m_cMu * m_weights.getEntry(j));
		}
		computeSAndTFirst(m_evolutionPathC.ebeDivide(m_D), meanS, meanT, hSig * m_c1);

		// compute the remaining mean S and T steps
		computeSAndTSecond(meanS, meanT);

		// compute update to v and d
		addInPlace(m_D, m_D.ebeMultiply(meanS));
		m_vn = m_vn.mapMultiply(m_normv).add(meanT.mapDivide(m_normv)); //result is v and not vn
		// store the new v separately as vn and its norm
		m_normv = m_vn.getNorm();
		m_vn.mapDivideToSelf(m_normv);

		//update step length
		m_sigma *= exp((m_cSigma / m_dSigma) * (m_evolutionPathSigma.getNorm() / chi(m_numberOfVariables) - 1.)); // eq. (39)

		//update mean
		m_mean = m;

		StringBuilder str = new StringBuilder()
				.append("hSig = " + hSig + "; ")
				.append("m_sigma = " + m_sigma + "; ")
				.append("||m_evolutionPathC|| = " + m_evolutionPathC.getNorm() + "; ")
				.append("||m_evolutionPathSigma|| = " + m_evolutionPathSigma.getNorm() + "; ")
				.append("||m_mean|| = " + m_mean.getNorm() + "; ");
		logger.info(str.toString());
	}

	// samples a point and stores additionally y=(x-m_mean)/(sigma*D)
	// as this is required for calculation later
	private Individual createSample(RandomDataGenerator random) {
		RealVector y = new ArrayRealVector(m_numberOfVariables);
		for (int i = 0; i < m_numberOfVariables; ++i) {
			y.setEntry(i, random.nextGaussian(0, 1));
		}
		double a = sqrt(1 + sqr(m_normv)) - 1;
		a *= y.dotProduct(m_vn);
		addInPlace(y, m_vn.mapMultiply(a));
		RealVector x = m_mean.add(m_D.ebeMultiply(y).mapMultiply(m_sigma));

		return new Individual(x, y);
	}

	/** computes the sample wise first two steps of S and T of theorem 3.6 in the paper
	 *  S and T arguments accordingly
	 */
	private void computeSAndTFirst(RealVector y, RealVector s, RealVector t, double weight) {
		if (weight == 0)
			return; //nothing to do
		double yvn = y.dotProduct(m_vn);
		double normv2 = sqr(m_normv);
		double gammav = 1 + normv2;

		//step 1 ( s += (sqr(y) - (normv2/gammav*yvn)*(y*m_vn)-1) * weight )
		RealVector ONE = new ArrayRealVector(m_numberOfVariables, 1.0);
		RealVector middleTerm = y.ebeMultiply(m_vn).mapMultiply(normv2 / gammav * yvn);
		addInPlace(s, sqr(y).subtract(middleTerm).subtract(ONE).mapMultiply(weight));

		//step 2 ( t += (yvn*y - m_vn*0.5*(sqr(yvn)+gammav)) * weight);
		addInPlace(t, y.mapMultiply(yvn).subtract(m_vn.mapMultiply(0.5).mapMultiply(sqr(yvn) + gammav)).mapMultiply(
				weight));
	}

	/** computes the last three steps of S and T of theorem 3.6 in the paper */
	private void computeSAndTSecond(RealVector s, RealVector t) {
		RealVector vn2 = m_vn.ebeMultiply(m_vn);
		double normv2 = sqr(m_normv);
		double gammav = 1 + normv2;
		// alpha of 3.5
		double alpha = sqr(normv2) + (2 * gammav - sqrt(gammav)) / vn2.getMaxValue();
		alpha = sqrt(alpha);
		alpha /= 2 + normv2;
		alpha = min(alpha, 1.0);
		// constants (b,A) of 3.4
		double b = -(1 - sqr(alpha)) * sqr(normv2) / gammav + 2 * sqr(alpha);
		RealVector TWO = new ArrayRealVector(m_numberOfVariables, 2.0);
		RealVector A = TWO.subtract(vn2.mapMultiply(b + 2 * sqr(alpha)));
		RealVector invAvn2 = vn2.ebeDivide(A);

		// step 3 ( alpha/gammav * ((2+normv2)*(m_vn*t) - vn2*normv2*inner_prod(m_vn,t));
		subtractInPlace(s,
				(m_vn.ebeMultiply(t).mapMultiply(2 + normv2).subtract(vn2.mapMultiply(m_vn.dotProduct(t) * normv2)))
						.mapMultiply(alpha / gammav));

		// step 4 ( s/A - invAvn2 * b * inner_prod(s,invAvn2) / (1+b*inner_prod(vn2, invAvn2)) );

		assignVector(s, s.ebeDivide(A).add(invAvn2.mapMultiply(-b * s.dotProduct(invAvn2) /
				(1 + b * vn2.dotProduct(invAvn2)))));

		// step 5 ( ((m_vn * s) * (2 + normv2) - m_vn * inner_prod(s, vn2)) * alpha );
		subtractInPlace(t,
				m_vn.ebeMultiply(s).mapMultiply(2 + normv2).subtract(m_vn.mapMultiply(s.dotProduct(vn2)))
					.mapMultiply(alpha));
	}

	static private double sum(RealVector v) {
		double s = 0.0;
		for (int i = 0; i < v.getDimension(); ++i)
			s += v.getEntry(i);
		return s;
	}

	static private RealVector sqr(RealVector v) {
		return v.map(x -> x * x);
	}

	static private double sqr(double x) {
		return x * x;
	}

	/**
	 * v += a (modifies vector in place)
	 *
	 * @return v
	 */
	static private RealVector addInPlace(RealVector v, RealVector a) {
		return assignVector(v, v.add(a));
	}

	/**
	 * v -= s (modifies vector in place)
	 *
	 * @return v
	 */
	static private RealVector subtractInPlace(RealVector v, RealVector s) {
		return assignVector(v, v.subtract(s));
	}

	/**
	 * v = x (In place)
	 *
	 * @return v
	 */
	static private RealVector assignVector(RealVector v, RealVector x) {
		for (int i = 0; i < v.getDimension(); ++i) {
			v.setEntry(i, x.getEntry(i));
		}
		return v;
	}
}

