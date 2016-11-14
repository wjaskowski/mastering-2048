package put.ci.cevo.games.encodings.ntuple;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.util.RandomFactory;

public class OthelloNTupleRandomFactory implements RandomFactory<NTuple> {

	private final NTupleRandomFactory factory;

	public OthelloNTupleRandomFactory(int tupleSize, double minWeight, double maxWeight) {
		this.factory = new NTupleRandomFactory(
			OthelloBoard.NUM_VALUES, tupleSize, OthelloBoard.SIZE, minWeight, maxWeight);
	}

	@Override
	public NTuple create(RandomDataGenerator random) {
		return factory.create(random);
	}
}
