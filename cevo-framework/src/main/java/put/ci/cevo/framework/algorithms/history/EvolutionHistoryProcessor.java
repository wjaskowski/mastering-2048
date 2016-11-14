package put.ci.cevo.framework.algorithms.history;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.sequence.NullTerminatedSequence;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.*;

import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Collections.max;
import static put.ci.cevo.util.Pair.create;
import static put.ci.cevo.util.RandomUtils.pickRandom;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class EvolutionHistoryProcessor {

	private final List<EvolutionState> history;

	public EvolutionHistoryProcessor(EvolutionHistory history) {
		this(history.getEvolutionHistory());
	}

	public EvolutionHistoryProcessor(List<EvolutionState> history) {
		this.history = history;
	}

	public <S> Map<Integer, List<EvaluatedIndividual<S>>> getSolutionsHistory() {
		return seq(history).toMap(new Transform<EvolutionState, Pair<Integer, List<EvaluatedIndividual<S>>>>() {
			@Override
			public Pair<Integer, List<EvaluatedIndividual<S>>> transform(EvolutionState state) {
				List<EvaluatedIndividual<S>> solutions = state.getEvaluatedSolutions();
				return create(state.getGeneration(), solutions);
			}
		}, new HashMap<Integer, List<EvaluatedIndividual<S>>>());
	}

	public <T> Map<Integer, List<EvaluatedIndividual<T>>> getTestsHistory() {
		return seq(history).toMap(new Transform<EvolutionState, Pair<Integer, List<EvaluatedIndividual<T>>>>() {
			@Override
			public Pair<Integer, List<EvaluatedIndividual<T>>> transform(EvolutionState state) {
				List<EvaluatedIndividual<T>> tests = state.getEvaluatedTests();
				return create(state.getGeneration(), tests);
			}
		}, new HashMap<Integer, List<EvaluatedIndividual<T>>>());
	}

	public Map<Integer, Long> getEffort() {
		return seq(history).toMap(new Transform<EvolutionState, Pair<Integer, Long>>() {
			@Override
			public Pair<Integer, Long> transform(EvolutionState state) {
				return create(state.getGeneration(), state.getTotalEffort());
			}
		}, new HashMap<Integer, Long>());
	}

	public <S> Sequence<EvaluatedIndividual<S>> getSolutionsPopulation(int generation) {
		Map<Integer, List<EvaluatedIndividual<S>>> history = getSolutionsHistory();
		return seq(history.get(generation));
	}

	public <T> Sequence<EvaluatedIndividual<T>> getTestsPopulation(int generation) {
		Map<Integer, List<EvaluatedIndividual<T>>> history = getTestsHistory();
		return seq(history.get(generation));
	}

	public <S> Sequence<EvaluatedIndividual<S>> getSolutionsLastPopulation() {
		Map<Integer, List<EvaluatedIndividual<S>>> history = getSolutionsHistory();
		return seq(history.get(max(history.keySet())));
	}

	public <T> Sequence<EvaluatedIndividual<T>> getTestsLastPopulation() {
		Map<Integer, List<EvaluatedIndividual<T>>> history = getTestsHistory();
		return seq(history.get(max(history.keySet())));
	}

	/** Subjectively best candidate solution for the last generation */
	public <S> EvaluatedIndividual<S> bestSolutionOfLastGeneration() {
		return bestSolution(max(getSolutionsHistory().keySet()));
	}

	/** Subjectively best test for the last generation */
	public <T> EvaluatedIndividual<T> bestTestOfLastGeneration() {
		return bestTest(max(getTestsHistory().keySet()));
	}

	/** Subjectively best candidate solution for a given <code>generation</code> */
	public <S> EvaluatedIndividual<S> bestSolution(int generation) {
		Map<Integer, List<EvaluatedIndividual<S>>> history = getSolutionsHistory();
		return max(history.get(generation));
	}

	/** Subjectively best test for a given <code>generation</code> */
	public <T> EvaluatedIndividual<T> bestTest(int generation) {
		Map<Integer, List<EvaluatedIndividual<T>>> history = getTestsHistory();
		return max(history.get(generation));
	}

	public <S> Sequence<EvaluatedIndividual<S>> bestSolutionFromEveryKGeneration(final int k) {
		final Iterator<Integer> generations = newTreeSet(getSolutionsHistory().keySet()).iterator();
		return new NullTerminatedSequence<EvaluatedIndividual<S>>() {
			@Override
			protected EvaluatedIndividual<S> getNext() {
				while (generations.hasNext()) {
					int generation = generations.next();
					if (generation % k == 0) {
						return bestSolution(generation);
					}
				}
				return null;
			}
		}.setSize(getSolutionsHistory().keySet().size());
	}

	public <S> Sequence<EvaluatedIndividual<S>> bestSolutionFromEachGeneration() {
		return bestSolutionFromEveryKGeneration(1);
	}

	public <T> Sequence<EvaluatedIndividual<T>> bestTestFromEachGeneration() {
		final Iterator<Integer> generations = getGenerations().iterator();
		return new NullTerminatedSequence<EvaluatedIndividual<T>>() {
			@Override
			protected EvaluatedIndividual<T> getNext() {
				if (generations.hasNext()) {
					return bestTest(generations.next());
				} else {
					return null;
				}
			}
		}.setSize(getSolutionsHistory().keySet().size());
	}

	public TreeSet<Integer> getGenerations() {
		return newTreeSet(getTestsHistory().keySet());
	}

	public <S> EvaluatedIndividual<S> getRandomSolution(int generation, RandomDataGenerator random) {
		Map<Integer, List<EvaluatedIndividual<S>>> history = getSolutionsHistory();
		return pickRandom(history.get(generation), random);
	}

	public <T> EvaluatedIndividual<T> getRandomTest(int generation, RandomDataGenerator random) {
		Map<Integer, List<EvaluatedIndividual<T>>> history = getTestsHistory();
		return pickRandom(history.get(generation), random);
	}

	public long getEffort(int generation) {
		return getEffort().get(generation);
	}

	public long getTotalEffort() {
		Map<Integer, Long> effort = getEffort();
		if (effort.isEmpty()) {
			return 0;
		}
		return effort.get(max(effort.keySet()));
	}

	public Sequence<Long> getGenerationalEffort() {
		TreeMap<Integer, Long> sortedEffort = new TreeMap<>(getEffort());
		return seq(sortedEffort.values());
	}
}
