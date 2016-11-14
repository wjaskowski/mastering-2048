package put.ci.cevo.framework.fitness;

import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.LazyMap;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import static org.paukov.combinatorics.Factory.createVector;

public class DistinctionsFitnessSharing extends Distinctions {

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(final PayoffTable<S, T> payoff, ThreadedContext context) {
		final ObjectDoubleOpenHashMap<S> solutionsFitness = new ObjectDoubleOpenHashMap<S>(payoff.solutions().size());

		Generator<T> pairGenerator = Factory.createSimpleCombinationGenerator(createVector(payoff.tests().toList()), 2);
		for (ICombinatoricsVector<T> testsPair : pairGenerator) {
			Set<S> solutions = getSolutionsThatMakeDistinctionForPair(payoff, testsPair);
			final double distinctionsForPair = solutions.size();
			for (S solution : solutions) {
				double fitnessPart = 1 / distinctionsForPair;
				solutionsFitness.putOrAdd(solution, fitnessPart, fitnessPart);
			}
		}
		return new LazyMap<S, Fitness>(new IdentityHashMap<S, Fitness>()) {
			@Override
			protected Fitness transform(S solution) {
				Double fitness = solutionsFitness.get(solution);
				return fitness != null ? new ScalarFitness(fitness) : new ScalarFitness(0);
			}
		};
	}

}
