package put.ci.cevo.games.encodings.ntuple;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.util.RandomFactory;

public class OthelloNTupleImprovedRandomFactory implements RandomFactory<NTuple> {

	private final NTupleImprovedRandomFactory factory;

	public OthelloNTupleImprovedRandomFactory(int tupleSize, double minWeight, double maxWeight) {
		this.factory = new NTupleImprovedRandomFactory(
			OthelloBoard.NUM_VALUES, tupleSize, OthelloBoard.SIZE, minWeight, maxWeight);
	}

	@Override
	public NTuple create(RandomDataGenerator random) {
		return factory.create(random);
	}
}
