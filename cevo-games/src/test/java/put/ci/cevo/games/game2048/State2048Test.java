package put.ci.cevo.games.game2048;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class State2048Test {

	private State2048 state;

	@Before
	public void setup() {
		RandomDataGenerator random = new RandomDataGenerator();
		state = State2048.getInitialState(random);
	}

	@Test
	public void testGetInitialState() throws Exception {
		int nonZeroCount = 0;
		double[] features = state.getFeatures();
		for (double f : features) {
			if (f > 0)
				nonZeroCount++;
		}
		assertEquals(nonZeroCount, 2);
	}

	@Test
	public void testRotateBoard() throws Exception {
		double[] features = state.getFeatures();
		state.rotateBoard();
		state.rotateBoard();
		state.rotateBoard();
		state.rotateBoard();
		double[] rotatedFeatures = state.getFeatures();

		Assert.assertArrayEquals(features, rotatedFeatures, 0);
	}

	@Test
	public void testGetPossibleMoves() {
		double[] features = new double[16];
		state = new State2048(features);
		List<Action2048> possibleMoves = state.getPossibleMoves();
		Assert.assertTrue(possibleMoves.isEmpty());

		features[0] = 1;
		state = new State2048(features);
		possibleMoves = state.getPossibleMoves();
		Assert.assertTrue(possibleMoves.contains(Action2048.DOWN));
		Assert.assertTrue(possibleMoves.contains(Action2048.RIGHT));
		Assert.assertFalse(possibleMoves.contains(Action2048.LEFT));
		Assert.assertFalse(possibleMoves.contains(Action2048.UP));

		features[1] = 2;
		features[2] = 3;
		features[3] = 4;
		state = new State2048(features);
		possibleMoves = state.getPossibleMoves();
		Assert.assertTrue(possibleMoves.contains(Action2048.DOWN));
		Assert.assertFalse(possibleMoves.contains(Action2048.RIGHT));
		Assert.assertFalse(possibleMoves.contains(Action2048.LEFT));
		Assert.assertFalse(possibleMoves.contains(Action2048.UP));

		features[1] = 1;
		state = new State2048(features);
		possibleMoves = state.getPossibleMoves();
		Assert.assertTrue(possibleMoves.contains(Action2048.RIGHT));
	}

	@Test
	public void testMakeMoveRight() {
		double[] features = new double[16];
		features[0] = 1;
		features[1] = 1;
		features[2] = 3;
		features[3] = 4;
		state = new State2048(features);

		int reward = state.makeMove(Action2048.RIGHT);
		double[] features2 = state.getFeatures();
		Assert.assertEquals(0, features2[0], 0);
		Assert.assertEquals(2, features2[1], 0);
		Assert.assertEquals(3, features2[2], 0);
		Assert.assertEquals(4, features2[3], 0);
		Assert.assertEquals(4, reward, 0);
	}

	@Test
	public void testMakeMoveRightDouble() {
		double[] features = new double[16];
		features[0] = 1;
		features[1] = 1;
		features[2] = 3;
		features[3] = 3;
		state = new State2048(features);

		int reward = state.makeMove(Action2048.RIGHT);
		double[] features2 = state.getFeatures();
		Assert.assertEquals(0, features2[0], 0);
		Assert.assertEquals(0, features2[1], 0);
		Assert.assertEquals(2, features2[2], 0);
		Assert.assertEquals(4, features2[3], 0);
		Assert.assertEquals(20, reward, 0);
	}

	@Test
	public void testMakeMoveRightAllTheSame() {
		double[] features = new double[16];
		features[0] = 1;
		features[1] = 1;
		features[2] = 1;
		features[3] = 1;
		state = new State2048(features);

		int reward = state.makeMove(Action2048.RIGHT);
		double[] features2 = state.getFeatures();
		Assert.assertEquals(0, features2[0], 0);
		Assert.assertEquals(0, features2[1], 0);
		Assert.assertEquals(2, features2[2], 0);
		Assert.assertEquals(2, features2[3], 0);
		Assert.assertEquals(8, reward, 0);
	}

	@Test
	public void testMakeMoveLeft() {
		double[] features = new double[16];
		features[0] = 1;
		features[1] = 1;
		features[2] = 3;
		features[3] = 4;
		state = new State2048(features);

		int reward = state.makeMove(Action2048.LEFT);
		double[] features2 = state.getFeatures();
		Assert.assertEquals(2, features2[0], 0);
		Assert.assertEquals(3, features2[1], 0);
		Assert.assertEquals(4, features2[2], 0);
		Assert.assertEquals(0, features2[3], 0);
		Assert.assertEquals(4, reward, 0);
	}

	@Test
	public void testMakeMove16384() {
		double[] features = new double[16];
		features[0] = 1;
		features[1] = 1;
		features[2] = 13;
		features[3] = 13;
		state = new State2048(features);

		int reward = state.makeMove(Action2048.LEFT);
		double[] features2 = state.getFeatures();
		Assert.assertEquals(2, features2[0], 0);
		Assert.assertEquals(14, features2[1], 0);
		Assert.assertEquals(0, features2[2], 0);
		Assert.assertEquals(0, features2[3], 0);
		Assert.assertEquals(16388, reward, 0);
	}

}
