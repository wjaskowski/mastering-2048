package put.ci.cevo.experiments.connect4.ntuples;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.connect4.Connect4Board;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTupleRandomFactory;
import put.ci.cevo.util.RandomFactory;

public class Connect4NTupleRandomFactory implements RandomFactory<NTuple> {

	private final NTupleRandomFactory factory;

	public Connect4NTupleRandomFactory(int tupleSize, double minWeight, double maxWeight) {
		this.factory = new NTupleRandomFactory(
			Connect4Board.NUM_VALUES, tupleSize, Connect4Board.SIZE, minWeight, maxWeight);
	}

	@Override
	public NTuple create(RandomDataGenerator random) {
		return factory.create(random);
	}
}
