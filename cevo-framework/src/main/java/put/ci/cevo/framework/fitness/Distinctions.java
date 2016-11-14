package put.ci.cevo.framework.fitness;

import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;
import com.carrotsearch.hppc.cursors.ObjectDoubleCursor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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

public class Distinctions implements FitnessAggregate {

	@Override
	public <S, T> Map<S, Fitness> aggregateFitness(final PayoffTable<S, T> payoff, ThreadedContext context) {
		final ObjectDoubleOpenHashMap<S> solutionsFitness = new ObjectDoubleOpenHashMap<S>(payoff.solutions().size());

		Generator<T> pairGenerator = Factory.createSimpleCombinationGenerator(createVector(payoff.tests().toList()), 2);
		int total = 0;
		for (ICombinatoricsVector<T> testsPair : pairGenerator) {
			for (S solution : getSolutionsThatMakeDistinctionForPair(payoff, testsPair)) {
				solutionsFitness.putOrAdd(solution, 1, 1);
			}
			total++;
		}

		for (ObjectDoubleCursor<S> pair : solutionsFitness) {
			solutionsFitness.put(pair.key, pair.value / total);
		}

		return new LazyMap<S, Fitness>(new IdentityHashMap<S, Fitness>()) {
			@Override
			protected Fitness transform(S solution) {
				Double fitness = solutionsFitness.get(solution);
				return fitness != null ? new ScalarFitness(fitness) : new ScalarFitness(0);
			}
		};
	}

	protected <S, T> Set<S> getSolutionsThatMakeDistinctionForPair(PayoffTable<S, T> payoff,
			ICombinatoricsVector<T> testsPair) {
		Set<S> candidatesWhoMadeDistinctions = new ObjectOpenHashSet<S>();
		for (S solution : payoff.solutions()) {
			Double result0 = payoff.get(solution, testsPair.getValue(0));
			Double result1 = payoff.get(solution, testsPair.getValue(1));
			if (result0 != null && result1 != null && !result0.equals(result1)) {
				candidatesWhoMadeDistinctions.add(solution);
			}
		}
		return candidatesWhoMadeDistinctions;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
