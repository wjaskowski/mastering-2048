package put.ci.cevo.experiments.ntuple;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTupleImprovedRandomFactory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.SymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.YAxisSymmetryExpander;
import put.ci.cevo.util.Lists;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class NTuplesConnect4SnakeImprovedRandomIndividualFactory implements IndividualFactory<NTuples> {

	private final SymmetryExpander expander;
	private final NTupleImprovedRandomFactory ntupleFactory;

	private final int numTuples;

	public NTuplesConnect4SnakeImprovedRandomIndividualFactory(int numTuples, int tupleSize, double minWeight,
			double maxWeight) {
		this(numTuples, tupleSize, minWeight, maxWeight, new YAxisSymmetryExpander(Connect4Board.BOARD_WIDTH));
	}

	@AccessedViaReflection
	public NTuplesConnect4SnakeImprovedRandomIndividualFactory(int numTuples, int tupleSize, double minWeight,
			double maxWeight, SymmetryExpander expander) {
		this.numTuples = numTuples;
		this.expander = expander;
		this.ntupleFactory = new NTupleImprovedRandomFactory(
				Connect4Board.NUM_VALUES, tupleSize, Connect4Board.SIZE, minWeight, maxWeight);
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		List<NTuple> tuples = Lists.fromFactory(numTuples, ntupleFactory, random);
		return new NTuples(tuples, expander);
	}
}
