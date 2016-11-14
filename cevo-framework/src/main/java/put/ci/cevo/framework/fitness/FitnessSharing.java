package put.ci.cevo.framework.fitness;

import java.util.List;
import java.util.Map;

import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;

public class FitnessSharing implements FitnessAggregate {

	private FitnessAggregate originalAggregate;
	private DistanceFunction distanceFunction;
	private double threshold;
	private double alpha;

	public FitnessSharing(FitnessAggregate originalAggregate, DistanceFunction distanceFunction, double threshold, double alpha) {
		this.originalAggregate = originalAggregate;
		this.distanceFunction = distanceFunction;
		this.threshold = threshold;
		this.alpha = alpha;
	}

	public FitnessSharing(FitnessAggregate originalAggregate, DistanceFunction distanceFunction, double threshold) {
		this(originalAggregate, distanceFunction, threshold, 1.0);
	}

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(PayoffTable<S, T> payoff, ThreadedContext context) {
		Map<S, Fitness> fitnessMap = originalAggregate.aggregateFitness(payoff, context);
		fitnessMap.replaceAll((s, f) -> new ScalarFitness(f.fitness() / calculateNicheCount(s, payoff)));
		return fitnessMap;
	}

	private <S, T> double calculateNicheCount(S individual, PayoffTable<S, T> payoff) {
		List<S> solutions = payoff.solutions().toList();
		return solutions.stream().mapToDouble(s -> sharingFunction(distanceFunction.getDistance(s, individual, payoff))).sum();
	}

	private double sharingFunction(double distance) {
		if (distance > threshold) {
			return 0;
		} else {
			return 1 - Math.pow(distance / threshold, alpha);
		}
	}
}
