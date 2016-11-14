package put.ci.cevo.experiments.clustering.aggregates;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.ml.clustering.algorithms.KMedoidsClusterer;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Map;

import static put.ci.cevo.ml.clustering.algorithms.KMedoidsClusterer.SeedingStrategy.PARKJUN;

public class KMedoidsFitnessAggregate implements FitnessAggregate {

	private static final int DEFAULT_MAX_ITERATIONS = 1000;

	private final ClusteringFitnessAggregate aggregate;

	public KMedoidsFitnessAggregate(int k) {
		this.aggregate = new ClusteringFitnessAggregate(new KMedoidsClusterer<>(k, DEFAULT_MAX_ITERATIONS,
				new EuclideanDistance(), PARKJUN));
	}

//	public KMedoidsFitnessAggregate(int k, boolean simpleMeans) {
//		this.aggregate = new ClusteringFitnessAggregate(new BinaryKMedoidsClusterer<>(k, simpleMeans));
//	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		return aggregate.aggregateFitness(payoff, context);
	}

}
