package put.ci.cevo.framework.algorithms.common;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public class Species<S> {

	private final EvolutionModel<S> evolutionModel;
	private final PopulationFactory<S> factory;

	private final int populationSize;

	@AccessedViaReflection
	public Species(EvolutionModel<S> evolutionModel, PopulationFactory<S> factory, int populationSize) {
		this.evolutionModel = evolutionModel;
		this.factory = factory;
		this.populationSize = populationSize;
	}

	public List<S> createInitialPopulation(RandomDataGenerator random) {
		return factory.createPopulation(populationSize, random);
	}

	public List<S> evolvePopulation(List<EvaluatedIndividual<S>> population, final ThreadedContext context) {
		List<S> evolved = evolutionModel.evolvePopulation(population, context);
		if (population.size() != evolved.size()) {
			throw new RuntimeException("Population size has changed! Expected: " + population.size() + ", was: "
				+ evolved.size());
		}
		return evolved;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("model", evolutionModel).add("factory", factory).add("pop", populationSize)
			.toString();
	}

}
