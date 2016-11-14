package put.ci.cevo.experiments.connect4.ntuples;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.experiments.ntuple.NTuplesAllStraightFactory;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.YAxisSymmetryExpander;

import static put.ci.cevo.games.connect4.Connect4Board.BOARD_HEIGHT;
import static put.ci.cevo.games.connect4.Connect4Board.BOARD_WIDTH;
import static put.ci.cevo.games.connect4.Connect4Board.NUM_VALUES;

public class Connect4AllStraightFactory implements IndividualFactory<NTuples> {

	private final IndividualFactory<NTuples> factory;

	public Connect4AllStraightFactory(int tupleSize, double minWeight, double maxWeight) {
		this.factory  = new NTuplesAllStraightFactory(tupleSize, new RectSize(BOARD_HEIGHT, BOARD_WIDTH), NUM_VALUES,
				minWeight, maxWeight, new YAxisSymmetryExpander(BOARD_WIDTH));
	}

	@Override
	public NTuples createRandomIndividual(RandomDataGenerator random) {
		return factory.createRandomIndividual(random);
	}
}
