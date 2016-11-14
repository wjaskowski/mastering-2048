package put.ci.cevo.experiments.ntuple;

import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.bigntuple.BigNTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.util.random.ThreadedRandom;

public class BigNTuplesGeneralSystematicFactoryTest {

	@Test
	public void testWithNoSystematicGeneration() throws Exception {
		BigNTuples ntuples = new BigNTuplesGeneralSystematicFactory("1111|1111", new RectSize(4, 4), 2, -1, +1,
				new RotationMirrorSymmetryExpander(new RectSize(4,4)), false)
				.createRandomIndividual(new ThreadedRandom(0).forThread());

		Assert.assertEquals(8, ntuples.getAll().size());
		Assert.assertEquals(1, ntuples.getMain().size());
	}
}