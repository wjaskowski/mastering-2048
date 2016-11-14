package put.ci.cevo.framework.factories;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Creates a population half of which is drawn randomly and the other half are mutants from the fist half.
 */
public class MutatingPopulationFactory<T> implements PopulationFactory<T> {

	private final IndividualFactory<T> individualFactory;
	private final MutationOperator<T> mutation;

	@AccessedViaReflection
	public MutatingPopulationFactory(IndividualFactory<T> individualFactory, MutationOperator<T> mutation) {
		this.individualFactory = individualFactory;
		this.mutation = mutation;
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		Preconditions.checkArgument(populationSize % 2 == 0, "Population size must divide by two!");

		final List<T> population = new ArrayList<T>(populationSize);
		for (int i = 0; i < populationSize / 2; i++) {
			T individual = individualFactory.createRandomIndividual(random);
			population.add(individual);
			population.add(mutation.produce(individual, random));
		}
		return population;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("factory", individualFactory).add("mutation", mutation).toString();
	}
}
