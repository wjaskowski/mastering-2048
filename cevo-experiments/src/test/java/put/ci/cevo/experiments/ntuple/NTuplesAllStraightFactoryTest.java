package put.ci.cevo.experiments.ntuple;

import java.util.HashSet;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;

import com.carrotsearch.hppc.IntArrayList;

public class NTuplesAllStraightFactoryTest {

	@Test
	public void testCreateRandomIndividual() throws Exception {
		NTuplesAllStraightFactory factory = new NTuplesAllStraightFactory(
			2, new RectSize(3), 2, -1, 1, new RotationMirrorSymmetryExpander(new RectSize(3)));
		NTuples ntuples = factory.createRandomIndividual(new RandomDataGenerator());
		Assert.assertEquals(4, ntuples.getMain().size());
		Assert.assertEquals(24, ntuples.getAll().size());

		HashSet<IntArrayList> set = new HashSet<>();

		for (NTuple t : ntuples.getAll()) {
			IntArrayList loc = IntArrayList.from(t.getLocations());
			Assert.assertFalse(set.contains(loc));
			set.add(loc);
			for (int p : t.getLocations()) {
				Assert.assertTrue(BoardUtils.isValidPosition(p, 3));
			}
		}
	}

	@Test
	public void testCreateRandomIndividual2() throws Exception {
		NTuplesAllStraightFactory factory = new NTuplesAllStraightFactory(
			2, new RectSize(2), 3, -1, 1, new RotationMirrorSymmetryExpander(new RectSize(2)));
		NTuples ntuples = factory.createRandomIndividual(new RandomDataGenerator());

		Assert.assertEquals(2, ntuples.getMain().size());
		Assert.assertEquals(12, ntuples.getAll().size());

		HashSet<IntArrayList> set = new HashSet<>();

		for (NTuple t : ntuples.getAll()) {
			IntArrayList loc = IntArrayList.from(t.getLocations());
			Assert.assertFalse(set.contains(loc));
			set.add(loc);
			for (int p : t.getLocations()) {
				Assert.assertTrue(BoardUtils.isValidPosition(p, 2));
			}
		}
	}
}
