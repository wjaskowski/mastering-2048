package put.ci.cevo.framework.factories;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public class UniformRandomPopulationFactory<T> implements PopulationFactory<T> {

	private final IndividualFactory<? extends T> individualFactory;

	@AccessedViaReflection
	public UniformRandomPopulationFactory(IndividualFactory<? extends T> individualFactory) {
		this.individualFactory = individualFactory;
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		Preconditions.checkArgument(populationSize > 0, "Invalid population size: " + populationSize);

		final List<T> population = new ArrayList<>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			population.add(individualFactory.createRandomIndividual(random));
		}
		return population;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("factory", individualFactory).toString();
	}

}
