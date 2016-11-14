package put.ci.cevo.experiments.ntuple;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.othello.OthelloBoard;

public class NTuplesAllRectanglesFactoryTest {
	@Test
	public void testNTuplesAllSquaresRandomIndividualFactory1() throws Exception {
		NTuplesAllRectanglesFactory factory = new NTuplesAllRectanglesFactory(
			new RectSize(1), OthelloBoard.SIZE, OthelloBoard.NUM_VALUES, -1, 1,
			new RotationMirrorSymmetryExpander(OthelloBoard.SIZE));
		NTuples ntuples = factory.createRandomIndividual(new RandomDataGenerator());

		Assert.assertEquals(10, ntuples.getMain().size());
	}

	@Test
	public void testNTuplesAllSquaresRandomIndividualFactory2() throws Exception {
		NTuplesAllRectanglesFactory factory = new NTuplesAllRectanglesFactory(
			new RectSize(2), OthelloBoard.SIZE, OthelloBoard.NUM_VALUES, -1, 1,
			new RotationMirrorSymmetryExpander(OthelloBoard.SIZE));
		NTuples ntuples = factory.createRandomIndividual(new RandomDataGenerator());

		Assert.assertEquals(10, ntuples.getMain().size());
	}

	@Test
	public void testNTuplesAllSquaresRandomIndividualFactory3() throws Exception {
		NTuplesAllRectanglesFactory factory = new NTuplesAllRectanglesFactory(
			new RectSize(3), OthelloBoard.SIZE, OthelloBoard.NUM_VALUES, -1, 1,
			new RotationMirrorSymmetryExpander(OthelloBoard.SIZE));
		NTuples ntuples = factory.createRandomIndividual(new RandomDataGenerator());

		Assert.assertEquals(6, ntuples.getMain().size());
	}
}
