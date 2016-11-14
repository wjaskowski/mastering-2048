package put.ci.cevo.games.encodings.ntuple;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.board.BoardPos;
import put.ci.cevo.games.board.BoardPosList;
import put.ci.cevo.games.board.RectSize;
import put.ci.cevo.games.encodings.ntuple.expanders.IdentitySymmetryExpander;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;

public class NTuplesLocationsGeneralSupplierTest {

	@Test
	public void testCreateRandomIndividual() throws Exception {
		BoardPosList positions = new BoardPosList(new BoardPos[] { new BoardPos(0, 1), new BoardPos(1, 1),
				new BoardPos(1, 2) });
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				new BoardPosList[] { positions }, new RectSize(4, 5), new IdentitySymmetryExpander()).get();

		Assert.assertEquals(1, ntuples.stream().mapToInt(List::size).sum());
	}

	@Test
	public void testRemovingDuplicates1Test() throws Exception {
		BoardPosList[] positionsList = { new BoardPosList("11"), new BoardPosList("1") };
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				positionsList, new RectSize(4, 5), new IdentitySymmetryExpander())
				.get();

		Assert.assertEquals(1, ntuples.stream().mapToInt(List::size).sum());
	}

	@Test
	public void testRemovingDuplicates2Test() throws Exception {
		BoardPosList[] positionsList = { new BoardPosList("1"), new BoardPosList("001|010|100"), new BoardPosList(
				"01|10") };
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				positionsList, new RectSize(4, 5), new IdentitySymmetryExpander())
				.get();

		Assert.assertEquals(1 + 1 + 1, ntuples.stream().mapToInt(List::size).sum());
	}

	@Test
	public void testRemovingDuplicates4Test() throws Exception {
		BoardPosList[] positionsList = {
				new BoardPosList("1"),
				new BoardPosList("01"),
				new BoardPosList("001"),
				new BoardPosList("0001"),
				new BoardPosList("00001"),
				new BoardPosList("00000|1"),
				new BoardPosList("00000|01"),
				new BoardPosList("00000|001"),
				new BoardPosList("00000|0001"),
				new BoardPosList("00000|00001"),
		};
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				positionsList, new RectSize(4, 5), new IdentitySymmetryExpander())
				.get();

		Assert.assertEquals(10, ntuples.stream().mapToInt(List::size).sum());
	}

	@Test
	public void testRemovingDuplicates3Test() throws Exception {
		BoardPosList[] positionsList = {
				new BoardPosList("1111"),
				new BoardPosList("1000|0100|0010|0001"),
				new BoardPosList("111"),
				new BoardPosList("001|010|100"),
				new BoardPosList("11"),
				new BoardPosList("01|10"),
				new BoardPosList("1"),
		};
		RectSize boardSize = new RectSize(4, 4);
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				positionsList, boardSize, new RotationMirrorSymmetryExpander(boardSize))
				.get();

		Assert.assertEquals(4, ntuples.size());
	}

	@Test
	public void testCreateRandomIndividualChar() throws Exception {
		//@formatter:off
		String[] matrix = new String[] {
			"1",
			"11",
		};
		//@formatter:on
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				new BoardPosList[] {new BoardPosList(matrix)}, new RectSize(4, 5), new IdentitySymmetryExpander()).get();

		Assert.assertEquals(1, ntuples.stream().mapToInt(List::size).sum());
	}

	@Test
	public void testCreateRandomIndividual2() throws Exception {
		BoardPosList positions = new BoardPosList(new BoardPos[] { new BoardPos(2, 1), new BoardPos(2, 2),
				new BoardPos(3, 2), new BoardPos(4, 2) });
		RectSize boardSize = new RectSize(5, 5);
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				new BoardPosList[] {positions}, boardSize, new RotationMirrorSymmetryExpander(boardSize))
				.get();

		Assert.assertEquals(1, ntuples.size());
		Assert.assertEquals(8, ntuples.stream().mapToInt(List::size).sum());
	}

	@Test
	public void testCreateRandomIndividual2Char() throws Exception {
		//@formatter:off
		String[] matrix = new String[] {
			"11",
			"01",
			"01",
		};
		//@formatter:on
		RectSize boardSize = new RectSize(5, 5);
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				new BoardPosList[] {new BoardPosList(matrix)}, boardSize, new RotationMirrorSymmetryExpander(boardSize))
				.get();

		Assert.assertEquals(1, ntuples.size());
	}

	@Test
	public void testCreateRandomIndividual3() throws Exception {
		BoardPosList positions = new BoardPosList(new BoardPos[] { new BoardPos(0, 0), new BoardPos(2, 1),
				new BoardPos(2, 2), new BoardPos(3, 2) });
		RectSize boardSize = new RectSize(5, 5);
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				new BoardPosList[] {positions}, boardSize, new RotationMirrorSymmetryExpander(boardSize))
				.get();

		Assert.assertEquals(1, ntuples.size());
	}

	@Test
	public void testCreateRandomIndividual3Char() throws Exception {
		//@formatter:off
		String[] matrix = new String[] {
			"100",
			"000",
			"011",
			"001",
		};
		//@formatter:on
		RectSize boardSize = new RectSize(5, 5);
		List<List<int[]>> ntuples = new NTuplesLocationsGeneralSupplier(
				new BoardPosList[] {new BoardPosList(matrix)}, boardSize, new RotationMirrorSymmetryExpander(boardSize))
				.get();

		Assert.assertEquals(1, ntuples.size());
	}
}
