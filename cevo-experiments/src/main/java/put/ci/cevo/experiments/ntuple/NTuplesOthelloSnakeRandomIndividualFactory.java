package put.ci.cevo.experiments.ntuple;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.OthelloNTupleRandomFactory;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.util.Lists;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class NTuplesOthelloSnakeRandomIndividualFactory implements IndividualFactory<NTuples> {

	private final SymmetryExpander expander;
	private final OthelloNTupleRandomFactory ntupleFactory;

	private final int numTuples;

	public NTuplesOthelloSnakeRandomIndividualFactory(int numTuples, int tupleSize, double minWeight, double maxWeight) {
		this(numTuples, tupleSize, minWeight, maxWeight, new RotationMirrorSymmetryExpander(OthelloBoard.SIZE));
	}

	@AccessedViaReflection
	public NTuplesOthelloSnakeRandomIndividualFactory(int numTuples, int tupleSize, double minWeight, double maxWeight,
			SymmetryExpander expander) {
		this.numTuples = numTuples;
		this.expander = expander;
		this.ntupleFactory = new OthelloNTupleRandomFactory(tupleSize, minWeight, maxWeight);
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		List<NTuple> tuples = Lists.fromFactory(numTuples, ntupleFactory, random);
		return new NTuples(tuples, expander);
	}
}
