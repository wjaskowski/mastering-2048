package put.ci.cevo.experiments.ntuple;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.util.Lists;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class NTuplesRandomIndividualFactory implements IndividualFactory<NTuples> {

	private final SymmetryExpander expander;
	private final RandomFactory<NTuple> ntupleFactory;

	private final int numTuples;

	@AccessedViaReflection
	public NTuplesRandomIndividualFactory(int numTuples, RandomFactory<NTuple> ntupleFactory, SymmetryExpander expander) {
		this.ntupleFactory = ntupleFactory;
		this.numTuples = numTuples;
		this.expander = expander;
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		List<NTuple> tuples = Lists.fromFactory(numTuples, ntupleFactory, random);
		return new NTuples(tuples, expander);
	}
}
