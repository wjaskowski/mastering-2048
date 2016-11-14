package put.ci.cevo.experiments.ntuple;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.encodings.ntuple.NTuples;

import java.util.List;

public class DoubleNTuplesFactory implements IndividualFactory<DoubleNTuples> {

	private final PopulationFactory<NTuples> nTuplesFactory;

	public DoubleNTuplesFactory(PopulationFactory<NTuples> nTuplesFactory) {
		this.nTuplesFactory = nTuplesFactory;
	}

	public DoubleNTuplesFactory(IndividualFactory<NTuples> nTuplesFactory) {
		this.nTuplesFactory = new UniformRandomPopulationFactory<>(nTuplesFactory);
	}

	@Override
	public DoubleNTuples createRandomIndividual(RandomDataGenerator random) {
		List<NTuples> pair = nTuplesFactory.createPopulation(2, random);
		return new DoubleNTuples(pair.get(0), pair.get(1));
	}
}
