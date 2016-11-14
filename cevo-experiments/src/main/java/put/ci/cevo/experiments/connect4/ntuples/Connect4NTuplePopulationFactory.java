package put.ci.cevo.experiments.connect4.ntuples;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;

import java.util.List;

public class Connect4NTuplePopulationFactory implements PopulationFactory<NTuples> {

	private final PopulationFactory<NTuples> factory;

	public Connect4NTuplePopulationFactory(int tupleSize, double minWeight, double maxWeight) {
		this.factory = new UniformRandomPopulationFactory<>(new Connect4AllStraightFactory(tupleSize, minWeight, maxWeight));
	}

	@Override
	public List<NTuples> createPopulation(int populationSize, RandomDataGenerator random) {
		return factory.createPopulation(populationSize, random);
	}
}
