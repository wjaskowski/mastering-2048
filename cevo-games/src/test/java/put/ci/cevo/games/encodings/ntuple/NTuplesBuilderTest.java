package put.ci.cevo.games.encodings.ntuple;

import static put.ci.cevo.games.board.BoardUtils.toMarginPos;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.GenericBoard;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.eval.BoardNTupleEvaluator;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;

public class NTuplesBuilderTest {

	@Test
	public void testNTuplesBuilder() throws Exception {
		int B = 2;
		NTuplesBuilder builder = new NTuplesBuilder(
			4, -1.0, 1.0, new RotationMirrorSymmetryExpander(new RectSize(B)), new RandomDataGenerator(new MersenneTwister(123)), false);

		// @formatter:off
		builder.addTuple(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
		});
		// @formatter:on

		NTuples ntuples = builder.buildNTuples();

		Assert.assertEquals(1, ntuples.getMain().size());
		Assert.assertEquals(8, ntuples.getAll().size());
	}

	@Test
	public void testNTuplesBuilder2() throws Exception {
		int B = 4;
		NTuplesBuilder builder = new NTuplesBuilder(
			4, -1.0, 1.0, new RotationMirrorSymmetryExpander(new RectSize(B)), new RandomDataGenerator(new MersenneTwister(123)), false);

		// @formatter:off
		builder.addTuple(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
		});
		builder.addTuple(new int[] {
			toMarginPos(B, 3, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 0, 0),
		});
		builder.addTuple(new int[] {
			toMarginPos(B, 3, 0),
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
		});
		builder.addTuple(new int[] {
			toMarginPos(B, 0, 1),
			toMarginPos(B, 0, 2),
			toMarginPos(B, 0, 0),
			toMarginPos(B, 0, 3),
		});
		// @formatter:on

		NTuples ntuples = builder.buildNTuples();

		Assert.assertEquals(1, ntuples.getMain().size());
		Assert.assertEquals(8, ntuples.getAll().size());
	}

	@Test
	public void testNTuplesBuilder3() throws Exception {
		int B = 4;
		NTuplesBuilder builder = new NTuplesBuilder(
			4, -1.0, 1.0, new RotationMirrorSymmetryExpander(new RectSize(B)), new RandomDataGenerator(new MersenneTwister(123)), false);

		// @formatter:off
		builder.addTuple(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
		});
		builder.addTuple(new int[] {
			toMarginPos(B, 3, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 1, 1),
		});
		// @formatter:on

		NTuples ntuples = builder.buildNTuples();

		Assert.assertEquals(2, ntuples.getMain().size());
		Assert.assertEquals(16, ntuples.getAll().size());
	}

	@Test
	public void testNTuplesBuilderSymmetry() throws Exception {
		int B = 2;
		NTuplesBuilder builder = new NTuplesBuilder(
			4, -1.0, 1.0, new RotationMirrorSymmetryExpander(new RectSize(B)), new RandomDataGenerator(new MersenneTwister(123)), false);

		// @formatter:off
		builder.addTuple(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
		});
		// @formatter:on

		NTuples ntuples = builder.buildNTuples();
		Assert.assertEquals(1, ntuples.getMain().size());
		Assert.assertEquals(8, ntuples.getAll().size());

		BoardNTupleEvaluator boardEvaluator = new BoardNTupleEvaluator();

		// Weather symmetric expanding works as expected (so that rotation and mirror does not change board value)
		//@formatter:off
		double value = boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 0, 3 },
				{ 2, 1 }
		}));
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 2, 1 },
				{ 0, 3 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 3, 0 },
				{ 1, 2 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 2, 0 },
				{ 1, 3 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 1, 2 },
				{ 3, 0 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 3, 1 },
				{ 0, 2 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 0, 2 },
				{ 3, 1 }
		})), 0.00001);
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 1, 3 },
				{ 2, 0 }
		})), 0.00001);
		//@formatter:on
	}

	@Test
	public void testNTuplesBuilderSymmetry2() throws Exception {
		int B = 4;
		NTuplesBuilder builder = new NTuplesBuilder(
			4, -1.0, 1.0, new RotationMirrorSymmetryExpander(new RectSize(B)), new RandomDataGenerator(new MersenneTwister(123)), false);

		// @formatter:off
		builder.addTuple(new int[] {
			toMarginPos(B, 0, 0),
			toMarginPos(B, 1, 0),
			toMarginPos(B, 2, 0),
			toMarginPos(B, 3, 0),
		});
		// @formatter:on

		NTuples ntuples = builder.buildNTuples();

		BoardNTupleEvaluator boardEvaluator = new BoardNTupleEvaluator();

		// Weather symmetric expanding works as expected (so that rotation and mirror does not change board value)
		//@formatter:off
		double value = boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 0, 1, 2, 3 },
				{ 2, 1, 3, 0 },
				{ 1, 3, 0, 0 },
				{ 3, 2, 1, 2 },
		}));
		Assert.assertEquals(value,
			boardEvaluator.evaluate(ntuples, new GenericBoard(new int[][] {
				{ 3, 2, 1, 0 },
				{ 0, 3, 1, 2 },
				{ 0, 0, 3, 1 },
				{ 2, 1, 2, 3 },
		})), 0.00001);
		//@formatter:on
	}
}
