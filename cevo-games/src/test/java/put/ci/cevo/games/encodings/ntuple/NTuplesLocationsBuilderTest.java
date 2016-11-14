package put.ci.cevo.games.encodings.ntuple;

import static put.ci.cevo.games.board.BoardUtils.toMarginPos;

import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.board.GenericBoard;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.eval.BoardNTupleEvaluator;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;

public class NTuplesLocationsBuilderTest {

	@Test
	public void testNTuplesBuilder() throws Exception {
		int B = 2;
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(
				new RotationMirrorSymmetryExpander(new RectSize(B)), false);

		// @formatter:off
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
		});
		// @formatter:on

		List<List<int[]>> ntuples = builder.build();

		Assert.assertEquals(1, ntuples.size());
		Assert.assertEquals(8, ntuples.get(0).size());
	}

	@Test
	public void testNTuplesBuilder2() throws Exception {
		int B = 4;
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(
				new RotationMirrorSymmetryExpander(new RectSize(B)), false);

		// @formatter:off
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
		});
		builder.addLocations(new int[] {
			toMarginPos(B, 3, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 0, 0),
		});
		builder.addLocations(new int[] {
			toMarginPos(B, 3, 0),
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
		});
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 1),
			toMarginPos(B, 0, 2),
			toMarginPos(B, 0, 0),
			toMarginPos(B, 0, 3),
		});
		// @formatter:on

		List<List<int[]>> locations = builder.build();

		Assert.assertEquals(1, locations.size());
		Assert.assertEquals(8, locations.get(0).size());
	}

	@Test
	public void testNTuplesBuilder5() throws Exception {
		int B = 4;
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(
				new RotationMirrorSymmetryExpander(new RectSize(B)), true);

		// @formatter:off
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
			toMarginPos(B, 0, 1),
			toMarginPos(B, 1, 1),
			toMarginPos(B, 2, 1),
			toMarginPos(B, 3, 1),
		});
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 0, 1),
			toMarginPos(B, 1, 1),
			toMarginPos(B, 0, 2),
			toMarginPos(B, 1, 2),
			toMarginPos(B, 0, 3),
			toMarginPos(B, 1, 3),
		});
		// @formatter:on

		List<List<int[]>> locations = builder.build();

		Assert.assertEquals(1, locations.size());
		Assert.assertEquals(8, locations.get(0).size());
	}

	@Test
	public void testNTuplesBuilder6() throws Exception {
		int B = 4;
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(
				new RotationMirrorSymmetryExpander(new RectSize(B)), true);

		// @formatter:off
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
		});
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 1),
			toMarginPos(B, 0, 2),
			toMarginPos(B, 0, 0),
			toMarginPos(B, 0, 3),
		});
		// @formatter:on

		List<List<int[]>> locations = builder.build();

		Assert.assertEquals(1, locations.size());
		Assert.assertEquals(8, locations.get(0).size());
	}

	@Test
	public void testNTuplesBuilder3() throws Exception {
		int B = 4;
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(
				new RotationMirrorSymmetryExpander(new RectSize(B)), false);

		// @formatter:off
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
		});
		builder.addLocations(new int[] {
			toMarginPos(B, 3, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 1, 1),
		});
		// @formatter:on

		List<List<int[]>> locations = builder.build();

		Assert.assertEquals(2, locations.size());
		Assert.assertEquals(16, locations.stream().mapToInt(List::size).sum());
	}

	@Test
	public void testNTuplesBuilderSymmetry() throws Exception {
		int B = 2;
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(
				new RotationMirrorSymmetryExpander(new RectSize(B)), false);

		// @formatter:off
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
		});
		// @formatter:on

		List<List<int[]>> locations = builder.build();
		Assert.assertEquals(1, locations.size());
		Assert.assertEquals(8, locations.stream().mapToInt(List::size).sum());

		NTuples nTuples = NTuples.createWithRandomWeights(locations, 4, -1, 1, new RandomDataGenerator(
				new MersenneTwister(123)));

		BoardNTupleEvaluator boardEvaluator = new BoardNTupleEvaluator();

		// Whether symmetric expanding works as expected (so that rotation and mirror does not change board value)
		//@formatter:off
		double value = boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 0, 3 },
				{ 2, 1 }
		}));

		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 2, 1 },
				{ 0, 3 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 3, 0 },
				{ 1, 2 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 2, 0 },
				{ 1, 3 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 1, 2 },
				{ 3, 0 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 3, 1 },
				{ 0, 2 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 0, 2 },
				{ 3, 1 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 1, 3 },
				{ 2, 0 }
		})), 0.00001);
		//@formatter:on
	}

	@Test
	public void testNTuplesBuilderSymmetry2() throws Exception {
		int B = 4;
		NTuplesLocationsBuilder builder = new NTuplesLocationsBuilder(new RotationMirrorSymmetryExpander(new RectSize(
				B)), false);

		// @formatter:off
		builder.addLocations(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
		});
		// @formatter:on

		List<List<int[]>> locations = builder.build();

		NTuples nTuples = NTuples.createWithRandomWeights(locations, 4, -1, 1, new RandomDataGenerator(
				new MersenneTwister(123)));

		BoardNTupleEvaluator boardEvaluator = new BoardNTupleEvaluator();

		// Whether symmetric expanding works as expected (so that rotation and mirror does not change board value)
		//@formatter:off
		double value = boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 0, 1, 2, 3 },
				{ 2, 1, 3, 0 },
				{ 1, 3, 0, 0 },
				{ 3, 2, 1, 2 },
		}));
		Assert.assertEquals(value,
			boardEvaluator.evaluate(nTuples, new GenericBoard(new int[][] {
				{ 3, 2, 1, 0 },
				{ 0, 3, 1, 2 },
				{ 0, 0, 3, 1 },
				{ 2, 1, 2, 3 },
		})), 0.00001);
		//@formatter:on
	}
}
