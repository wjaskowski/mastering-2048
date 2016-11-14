package put.ci.cevo.games.othello.mdp;

import static put.ci.cevo.games.board.Board.BLACK;
import static put.ci.cevo.games.board.Board.EMPTY;
import static put.ci.cevo.games.board.Board.WHITE;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import put.ci.cevo.games.board.BoardUtils;
import put.ci.cevo.games.othello.OthelloBoard;

public class OthelloRulesTest {

	private OthelloRules othelloRules;
	private OthelloState initialState;

	@Before
	public void setUp() {
		othelloRules = new OthelloRules();
		initialState = othelloRules.createInitialState();
	}

	@Test
	public void testCreateInitialState() throws Exception {
		Assert.assertEquals(BLACK, initialState.getPlayerToMove());

		OthelloBoard board = initialState.getBoard();
		Assert.assertEquals(WHITE, board.getValue(3, 3));
		Assert.assertEquals(WHITE, board.getValue(4, 4));
		Assert.assertEquals(BLACK, board.getValue(3, 4));
		Assert.assertEquals(BLACK, board.getValue(4, 3));

		double[] values = BoardUtils.getValues(board);
		int numEmptyLocations = 0;
		for (double val : values) {
			if (val == EMPTY) {
				numEmptyLocations++;
			}
		}
		Assert.assertEquals(60, numEmptyLocations);
	}

	@Test
	public void testFindMoves() throws Exception {
		List<OthelloMove> moves = othelloRules.findMoves(initialState);
		Assert.assertTrue(moves.contains(new OthelloMove(2, 3)));
		Assert.assertTrue(moves.contains(new OthelloMove(3, 2)));
		Assert.assertTrue(moves.contains(new OthelloMove(4, 5)));
		Assert.assertTrue(moves.contains(new OthelloMove(5, 4)));
	}

	@Test
	public void testMakeMove() throws Exception {
		double[] stateValues = BoardUtils.getValues(initialState.getBoard());
		List<OthelloMove> moves = othelloRules.findMoves(initialState);
		for (OthelloMove move : moves) {
			OthelloState afterState = othelloRules.makeMove(initialState, move);
			double[] afterStateValues = BoardUtils.getValues(afterState.getBoard());

			int numDiffLocations = 0;
			for (int i = 0; i < stateValues.length; i++) {
				if (stateValues[i] != afterStateValues[i]) {
					numDiffLocations++;
				}
			}

			Assert.assertEquals(2, numDiffLocations);
		}
	}
}
