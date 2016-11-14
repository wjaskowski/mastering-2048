package put.ci.cevo.framework.retrospection;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.DoubleArrayListSupplier;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.google.common.collect.Multimaps.newListMultimap;
import static java.util.Collections.max;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class RetrospectionResult {

	private final Multimap<Integer, Double> fitness = newListMultimap(new TreeMap<Integer, Collection<Double>>(),
		new DoubleArrayListSupplier());

	public <T> void offer(Iterable<EvaluatedIndividual<T>> evaluated) {
		for (EvaluatedIndividual<T> evaluatedIndividual : evaluated) {
			offer(evaluatedIndividual);
		}
	}

	public <T> void offer(EvaluatedIndividual<T> evaluated) {
		fitness.put(evaluated.getGeneration(), evaluated.getFitness());
	}

	public Sequence<Double> getFitness(int generation) {
		return seq(fitness.get(generation));
	}

	public Sequence<Double> getSortedFitness(int generation, Ordering<Double> ordering) {
		return seq(ordering.sortedCopy(fitness.get(generation)));
	}

	public Sequence<Double> getFitnessValues() {
		return seq(fitness.values());
	}

	public Sequence<Double> bestOfEachGeneration() {
		return seq(fitness.keySet()).map(new Transform<Integer, Double>() {
			@Override
			public Double transform(Integer generation) {
				return max(fitness.get(generation));
			}
		});
	}

	public double lastGenerationBestFitness() {
		int lastGeneration = max(fitness.keySet());
		return max(fitness.get(lastGeneration));
	}

	public Multimap<Integer, Double> getFitness() {
		return fitness;
	}

	public static RetrospectionResult fromMap(Map<Integer, Collection<Double>> fitness) {
		RetrospectionResult result = new RetrospectionResult();
		for (Entry<Integer, Collection<Double>> entry : fitness.entrySet()) {
			result.fitness.putAll(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
