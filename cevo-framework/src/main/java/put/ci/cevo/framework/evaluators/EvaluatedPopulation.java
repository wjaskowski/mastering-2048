package put.ci.cevo.framework.evaluators;

import org.apache.commons.lang3.builder.CompareToBuilder;
import put.ci.cevo.framework.PopulationUtils;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;

public class EvaluatedPopulation<T> implements Iterable<EvaluatedIndividual<T>> {

	private final List<EvaluatedIndividual<T>> population;
	private final long totalEffort;

	public EvaluatedPopulation() {
		this(emptyList());
	}

	public EvaluatedPopulation(List<EvaluatedIndividual<T>> population) {
		this(population, PopulationUtils.sumarizedEffort(population));
	}

	public EvaluatedPopulation(List<EvaluatedIndividual<T>> population, long totalEffort) {
		this.population = population;
		this.totalEffort = totalEffort;
	}

	public List<EvaluatedIndividual<T>> getPopulation() {
		return population;
	}

	public long getTotalEffort() {
		return totalEffort;
	}

	/**
	 * @return sorted evaluated invididuals. Higher fitnesses first
	 */
	public List<EvaluatedIndividual<T>> sorted() {
		ArrayList<EvaluatedIndividual<T>> players = new ArrayList<>(population);
		Collections.sort(players, (o1, o2) -> -new CompareToBuilder().append(o1, o2).toComparison());
		return players;
	}

	@Override
	public Iterator<EvaluatedIndividual<T>> iterator() {
		return population.iterator();
	}
}
