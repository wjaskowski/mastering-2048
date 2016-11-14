package put.ci.cevo.framework.evaluators.sampling;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Better individuals are evaluated more precisely (using single-elimination tournament).
 * See Jaskowski & Kotlowski, "On selecting the best individual in noisy environments", 2008
 */
public final class TournamentRandomSamplingEvaluator<S, T> implements PopulationEvaluator<S> {

	private final PopulationFactory<T> randomSampleFactory;
	private final InteractionDomain<S, T> domain;
	private final int evaluationsBudget;

	public TournamentRandomSamplingEvaluator(InteractionDomain<S, T> domain, PopulationFactory<T> randomSampleFactory,
			int evaluationsBudget) {
		Preconditions.checkArgument(evaluationsBudget > 0);
		this.evaluationsBudget = evaluationsBudget;
		this.domain = domain;
		this.randomSampleFactory = randomSampleFactory;
	}

	private class Measured<X> {
		public final X individual;
		private double sum = 0.0;
		private int n = 0;

		public Measured(X solution) {
			this.individual = solution;
		}

		public double mean() {
			return ((n > 0) ? (sum / n) : 0);
		}

		public void update(StatisticalSummary stat) {
			sum += stat.getSum();
			n += stat.getN();
		}

		@Override
		public String toString() {
			return String.format("%.2f", mean());
		}
	}

	@Override
	public EvaluatedPopulation<S> evaluate(List<S> population, int generation, ThreadedContext context) {

		final int HARDCODED_EFFORT = 2; // FIXME, TODO: HACK. This is for DoubleOthello only

		int n = population.size();
		int t = evaluationsBudget / n;

		List<Measured<S>> measuredPopulation = new ArrayList<>();
		for (S solution : population) {
			measuredPopulation.add(new Measured<>(solution));
		}

		List<Measured<S>> A = new ArrayList<>(measuredPopulation);
		while (n > 10 /* FIXME: HACKED for mu=10 */&& A.get(0).n < 1500 /* FIXME: HACKED for NUM_EVAL=1500 */) {
			ExpectedUtility<S, T> measure = new ExpectedUtility<>(
				domain, randomSampleFactory, t / 2, context);

			for (Measured<S> measured : A) {
				measured.update(measure.measure(measured.individual, context).stats());
			}
			Collections.sort(A, new Comparator<Measured<S>>() {
				@Override
				public int compare(Measured<S> o1, Measured<S> o2) {
					if (o1.mean() > o2.mean()) {
						return -1;
					} else if (o1.mean() < o2.mean()) {
						return +1;
					}
					return 0;
				}
			});

			A = A.subList(0, n / 2);
			n = n / 2;
		}

		List<EvaluatedIndividual<S>> evaluated = new ArrayList<>(population.size());
		for (Measured<S> measured : measuredPopulation) {
			evaluated.add(new EvaluatedIndividual<>(measured.individual, measured.mean(), generation, HARDCODED_EFFORT
				* measured.n));
		}
		return new EvaluatedPopulation<>(evaluated);
	}
}
