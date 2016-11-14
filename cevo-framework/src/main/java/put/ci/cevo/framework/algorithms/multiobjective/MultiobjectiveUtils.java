package put.ci.cevo.framework.algorithms.multiobjective;

import put.ci.cevo.framework.algorithms.multiobjective.nsga2.NSGA2Fitness;
import put.ci.cevo.framework.fitness.MultiobjectiveFitness;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.sequence.transforms.LazyMap;
import put.ci.cevo.util.sequence.transforms.Transforms;

import java.util.*;
import java.util.Map.Entry;

import static java.lang.Double.POSITIVE_INFINITY;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class MultiobjectiveUtils {

	public static class ParetoFrontRank<S> {

		private final Map<EvaluatedIndividual<S>, Integer> rankByIndividuals;
		private final Map<Integer, List<EvaluatedIndividual<S>>> individualsByRank;

		public ParetoFrontRank(Map<EvaluatedIndividual<S>, Integer> rankByIndividuals,
				Map<Integer, List<EvaluatedIndividual<S>>> individualsByRank) {
			this.rankByIndividuals = rankByIndividuals;
			this.individualsByRank = individualsByRank;
		}

		public List<EvaluatedIndividual<S>> getParetoFront(int rank) {
			return individualsByRank.get(rank);
		}

		public int getRank(EvaluatedIndividual<S> individual) {
			return rankByIndividuals.get(individual);
		}

		public Iterable<List<EvaluatedIndividual<S>>> getParetoFronts() {
			return individualsByRank.values();
		}

	}

	public static class Sparsity<S> {

		private final Map<EvaluatedIndividual<S>, Double> sparsities = new LazyMap<EvaluatedIndividual<S>, Double>() {
			@Override
			protected Double transform(EvaluatedIndividual<S> k) {
				return 0.0;
			}
		};

		public double getSparsity(EvaluatedIndividual<S> individual) {
			return sparsities.get(individual);
		}

		public void computeSparsities(List<EvaluatedIndividual<S>> paretoFront) {
			int numObjectives = paretoFront.get(0).fitness(MultiobjectiveFitness.class).getObjectives().size();
			for (int o = 0; o < numObjectives; o++) {
				List<EvaluatedIndividual<S>> sortedFront = sortByObjective(paretoFront, o);

				sparsities.put(sortedFront.get(0), POSITIVE_INFINITY);
				sparsities.put(sortedFront.get(sortedFront.size() - 1), POSITIVE_INFINITY);

				for (int i = 1; i < sortedFront.size() - 2; i++) {
					double sparsity = sortedFront.get(i + 1).fitness(MultiobjectiveFitness.class).getObjective(o)
						- sortedFront.get(i - 1).fitness(MultiobjectiveFitness.class).getObjective(o);

					double previousSparsity = sparsities.get(sortedFront.get(i));
					sparsities.put(sortedFront.get(i), previousSparsity + sparsity);
				}
			}
		}

		public List<EvaluatedIndividual<S>> sparsest(int n) {
			List<Entry<EvaluatedIndividual<S>, Double>> entries = new LinkedList<>(sparsities.entrySet());

			Collections.sort(entries, new Comparator<Entry<EvaluatedIndividual<S>, Double>>() {
				@Override
				public int compare(Entry<EvaluatedIndividual<S>, Double> o1, Entry<EvaluatedIndividual<S>, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});

			return seq(entries).take(n).map(Transforms.<EvaluatedIndividual<S>> entryKey()).toList();
		}
	}

	public static <S> List<EvaluatedIndividual<S>> sortByObjective(List<EvaluatedIndividual<S>> population,
			final int objective) {
		Collections.sort(population, new Comparator<EvaluatedIndividual<S>>() {
			@Override
			public int compare(EvaluatedIndividual<S> o1, EvaluatedIndividual<S> o2) {
				return o1.fitness(MultiobjectiveFitness.class).compareOnObjective(
					o2.fitness(MultiobjectiveFitness.class), objective);
			}
		});
		return population;
	}

	/**
	 * Divides population into pareto front ranks by non-dominated sorting.
	 */
	public static <S> ParetoFrontRank<S> computeParetoFrontRanks(List<EvaluatedIndividual<S>> population) {
		List<EvaluatedIndividual<S>> pop = new ArrayList<>(population);
		Map<EvaluatedIndividual<S>, Integer> individualRanks = new TreeMap<>();
		Map<Integer, List<EvaluatedIndividual<S>>> frontRanks = new TreeMap<>();

		int rank = 1;
		do {
			List<EvaluatedIndividual<S>> paretoFront = computeParetoFront(pop);
			for (EvaluatedIndividual<S> individual : paretoFront) {
				individualRanks.put(individual, rank);
				pop.remove(individual);
			}
			frontRanks.put(rank, paretoFront);
			rank++;
		} while (!pop.isEmpty());

		return new ParetoFrontRank<>(individualRanks, frontRanks);
	}

	/**
	 * Divides population into pareto front ranks by non-dominated sorting.
	 */
	public static <S> Map<Integer, List<EvaluatedIndividual<S>>> partitionIntoRanks(
			List<EvaluatedIndividual<S>> population) {
		List<EvaluatedIndividual<S>> pop = new ArrayList<>(population);
		Map<Integer, List<EvaluatedIndividual<S>>> frontsByRank = new TreeMap<>();

		int rank = 1;
		do {
			List<EvaluatedIndividual<S>> paretoFront = computeParetoFront(pop);
			frontsByRank.put(rank, paretoFront);
			pop.removeAll(paretoFront);
			rank++;
		} while (!pop.isEmpty());

		return frontsByRank;
	}

	/**
	 * Computes a pareto non-dominated front.
	 */
	public static <S> List<EvaluatedIndividual<S>> computeParetoFront(List<EvaluatedIndividual<S>> inds) {
		List<EvaluatedIndividual<S>> front = new ArrayList<>();
		front.add(inds.get(0));
		for (int i = 1; i < inds.size(); i++) {
			MultiobjectiveFitness ind = (MultiobjectiveFitness) inds.get(i).fitness();
			boolean dominated = false;
			int frontSize = front.size();
			for (int j = 0; j < frontSize; j++) {
				MultiobjectiveFitness frontMember = (MultiobjectiveFitness) front.get(j).fitness();
				if (frontMember.paretoDominates(ind)) {
					dominated = true;
					break;
				} else if (ind.paretoDominates(frontMember)) {
					int size = front.size();
					front.set(j, front.get(size - 1));
					front.remove(size - 1);
					frontSize--;
					j--;
				}
			}
			if (!dominated) {
				front.add(inds.get(i));
			}
		}
		return front;
	}

	public static <S> Collection<? extends EvaluatedIndividual<S>> sparsest(List<EvaluatedIndividual<S>> front, int n) {
		Collections.sort(front, new Comparator<EvaluatedIndividual<S>>() {
			@Override
			public int compare(EvaluatedIndividual<S> o1, EvaluatedIndividual<S> o2) {
				return o2.fitness(NSGA2Fitness.class).compareTo(o1.fitness(NSGA2Fitness.class));
			}
		});
		return front.subList(0, n);
	}

	public static <S> List<List<EvaluatedIndividual<S>>> assignRanks(List<EvaluatedIndividual<S>> population) {
		List<EvaluatedIndividual<S>> pop = new ArrayList<>(population);
		List<List<EvaluatedIndividual<S>>> frontsByRank = new ArrayList<>();

		int rank = 1;
		do {
			List<EvaluatedIndividual<S>> front = MultiobjectiveUtils.computeParetoFront(pop);
			for (EvaluatedIndividual<S> individual : front) {
				individual.fitness(NSGA2Fitness.class).rank = rank;
				pop.remove(individual);
			}
			frontsByRank.add(front);
			rank++;
		} while (!pop.isEmpty());

		return frontsByRank;
	}

	public static <S> void assignSparsity(List<EvaluatedIndividual<S>> front) {
		int numObjectives = front.get(0).fitness(NSGA2Fitness.class).getObjectives().size();
		for (int o = 0; o < numObjectives; o++) {
			List<EvaluatedIndividual<S>> sortedFront = sortByObjective(front, o);

			sortedFront.get(0).fitness(NSGA2Fitness.class).sparsity = POSITIVE_INFINITY;
			sortedFront.get(front.size() - 1).fitness(NSGA2Fitness.class).sparsity = POSITIVE_INFINITY;

			for (int j = 1; j < front.size() - 1; j++) {
				NSGA2Fitness fj = front.get(j).fitness(NSGA2Fitness.class);
				NSGA2Fitness fjplus1 = front.get(j + 1).fitness(NSGA2Fitness.class);
				NSGA2Fitness fjminus1 = front.get(j - 1).fitness(NSGA2Fitness.class);

				fj.sparsity += (fjplus1.getObjective(o) - fjminus1.getObjective(o));
//					/ (fj.maxObjective[o] - fj.minObjective[o]);
			}
		}
	}

}
